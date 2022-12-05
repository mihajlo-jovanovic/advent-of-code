use std::collections::hash_map::Entry::{Occupied, Vacant};
use std::collections::{BinaryHeap, HashMap};

use std::hash::Hash;

use petgraph::visit::EdgeRef;

use petgraph::algo::Measure;
use petgraph::visit::{GraphBase, IntoEdges, Visitable};
use petgraph::Graph;
use regex::Regex;
use std::cmp::Ordering;

#[derive(Copy, Clone, Debug)]
pub struct MinScored<K, T>(pub K, pub T);

impl<K: PartialOrd, T> PartialEq for MinScored<K, T> {
    #[inline]
    fn eq(&self, other: &MinScored<K, T>) -> bool {
        self.cmp(other) == Ordering::Equal
    }
}

impl<K: PartialOrd, T> Eq for MinScored<K, T> {}

impl<K: PartialOrd, T> PartialOrd for MinScored<K, T> {
    #[inline]
    fn partial_cmp(&self, other: &MinScored<K, T>) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

impl<K: PartialOrd, T> Ord for MinScored<K, T> {
    #[inline]
    fn cmp(&self, other: &MinScored<K, T>) -> Ordering {
        let a = &self.0;
        let b = &other.0;
        if a == b {
            Ordering::Equal
        } else if a < b {
            Ordering::Less
        } else if a > b {
            Ordering::Greater
        } else if a.ne(a) && b.ne(b) {
            // these are the NaN cases
            Ordering::Equal
        } else if a.ne(a) {
            // Order NaN less, so that it is last in the MinScore order
            Ordering::Greater
        } else {
            Ordering::Less
        }
    }
}

#[aoc_generator(day7)]
fn generator_input(input: &str) -> Vec<(char, char)> {
    input
        .lines()
        .map(|l| {
            let re = Regex::new(r"^Step (.) must be finished before step (.) can begin.$").unwrap();

            if let Some(caps) = re.captures(l) {
                let first_step: char = caps[1].chars().next().unwrap();
                let second_step: char = caps[2].chars().next().unwrap();
                println!("First step: {}", first_step);
                println!("Second step: {}", second_step);
                (first_step, second_step)
            } else {
                panic!("Input string did not match regular expression");
            }
        })
        .collect()
}

fn cost(c: char) -> usize {
    c as usize - 64
}

#[aoc(day7, part2)]
fn part2(adj_list: &[(char, char)]) -> usize {
    let mut graph: Graph<&char, usize> = Graph::new();
    let mut nodes = HashMap::new();
    let mut start: Option<_> = None;
    let mut end: Option<_> = None;
    for (n1, n2) in adj_list.iter() {
        let v1 = *nodes.entry(n1).or_insert_with(|| graph.add_node(n1));
        let v2 = *nodes.entry(n2).or_insert_with(|| graph.add_node(n2));
        graph.add_edge(v1, v2, cost(*n2));
        if *n1 == 'P' {
            start = Some(v1);
        }
        if *n2 == 'B' {
            end = Some(v2);
        }
    }
    //println!("{:?}", Dot::with_config(&graph, &[Config::EdgeNoLabel]));
    let path = astar(
        &graph,
        start.unwrap(),        // start
        |n| n == end.unwrap(), // is_goal
        |e| *e.weight(),       // edge_cost
        |_| 0,                 // estimate_cost
    );

    match path {
        Some((cost, path)) => {
            println!("The total cost was {}: {:?}", cost, path);
        }
        None => println!("There was no path"),
    }
    adj_list.len()
}

pub fn astar<G, F, H, K, IsGoal>(
    graph: G,
    start: G::NodeId,
    mut is_goal: IsGoal,
    mut edge_cost: F,
    mut estimate_cost: H,
) -> Option<(K, Vec<G::NodeId>)>
where
    G: IntoEdges + Visitable,
    IsGoal: FnMut(G::NodeId) -> bool,
    G::NodeId: Eq + Hash,
    F: FnMut(G::EdgeRef) -> K,
    H: FnMut(G::NodeId) -> K,
    K: Measure + Copy,
{
    let mut visit_next = BinaryHeap::new();
    let mut scores = HashMap::new(); // g-values, cost to reach the node
    let mut estimate_scores = HashMap::new(); // f-values, cost to reach + estimate cost to goal
    let mut path_tracker = PathTracker::<G>::new();

    let zero_score = K::default();
    scores.insert(start, zero_score);
    visit_next.push(MinScored(estimate_cost(start), start));

    while let Some(MinScored(estimate_score, node)) = visit_next.pop() {
        if is_goal(node) {
            let path = path_tracker.reconstruct_path_to(node);
            let cost = scores[&node];
            return Some((cost, path));
        }

        // This lookup can be unwrapped without fear of panic since the node was necessarily scored
        // before adding it to `visit_next`.
        let node_score = scores[&node];

        match estimate_scores.entry(node) {
            Occupied(mut entry) => {
                // If the node has already been visited with an equal or lower score than now, then
                // we do not need to re-visit it.
                if *entry.get() >= estimate_score {
                    continue;
                }
                entry.insert(estimate_score);
            }
            Vacant(entry) => {
                entry.insert(estimate_score);
            }
        }

        for edge in graph.edges(node) {
            let next = edge.target();
            let next_score = node_score + edge_cost(edge);

            match scores.entry(next) {
                Occupied(mut entry) => {
                    // No need to add neighbors that we have already reached through a shorter path
                    // than now.
                    if *entry.get() >= next_score {
                        continue;
                    }
                    entry.insert(next_score);
                }
                Vacant(entry) => {
                    entry.insert(next_score);
                }
            }

            path_tracker.set_predecessor(next, node);
            let next_estimate_score = next_score + estimate_cost(next);
            visit_next.push(MinScored(next_estimate_score, next));
        }
    }

    None
}

struct PathTracker<G>
where
    G: GraphBase,
    G::NodeId: Eq + Hash,
{
    came_from: HashMap<G::NodeId, G::NodeId>,
}

impl<G> PathTracker<G>
where
    G: GraphBase,
    G::NodeId: Eq + Hash,
{
    fn new() -> PathTracker<G> {
        PathTracker {
            came_from: HashMap::new(),
        }
    }

    fn set_predecessor(&mut self, node: G::NodeId, previous: G::NodeId) {
        self.came_from.insert(node, previous);
    }

    fn reconstruct_path_to(&self, last: G::NodeId) -> Vec<G::NodeId> {
        let mut path = vec![last];

        let mut current = last;
        while let Some(&previous) = self.came_from.get(&current) {
            path.push(previous);
            current = previous;
        }

        path.reverse();

        path
    }
}

#[test]
fn test_generator_input() {
    let input = "Step C must be finished before step A can begin.
Step C must be finished before step F can begin.
Step A must be finished before step B can begin.
Step A must be finished before step D can begin.
Step B must be finished before step E can begin.
Step D must be finished before step E can begin.
Step F must be finished before step E can begin.";
    assert_eq!(generator_input(input).len(), 7);
}

#[test]
fn test_cost() {
    assert_eq!(3, cost('C'));
    assert_eq!(5, cost('E'));
    assert_eq!(6, cost('F'));
    assert_eq!(1, cost('A'));
    assert_eq!(16, cost('P'));
}
