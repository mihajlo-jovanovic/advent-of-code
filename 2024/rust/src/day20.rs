use std::cmp::Ordering;
use std::collections::BinaryHeap;
use std::collections::{HashMap, HashSet};

#[aoc_generator(day20)]
fn input_generator(input: &str) -> HashMap<char, HashSet<(i32, i32)>> {
    let lines: Vec<&str> = input.lines().collect();
    let mut result: HashMap<char, HashSet<(i32, i32)>> = HashMap::new();

    for (y, line) in lines.iter().enumerate() {
        for (x, c) in line.chars().enumerate() {
            if c != '.' {
                result.entry(c).or_default().insert((x as i32, y as i32));
            }
        }
    }

    result
}

#[derive(Copy, Clone, Eq, PartialEq)]
struct State {
    cost: usize,
    position: Node,
}

// The priority queue depends on `Ord`.
// Explicitly implement the trait so the queue becomes a min-heap
// instead of a max-heap.
impl Ord for State {
    fn cmp(&self, other: &Self) -> Ordering {
        // Notice that we flip the ordering on costs.
        // In case of a tie we compare positions - this step is necessary
        // to make implementations of `PartialEq` and `Ord` consistent.
        other
            .cost
            .cmp(&self.cost)
            .then_with(|| self.position.pos.cmp(&other.position.pos))
    }
}

// `PartialOrd` needs to be implemented as well.
impl PartialOrd for State {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

// Each node is represented as a `usize`, for a shorter implementation.
struct Edge {
    node: Node,
    cost: usize,
}

// A struct representing the same fields as the Clojure map in `node-at`.
#[derive(Debug, Clone, PartialEq, Eq, Hash, Copy)]
pub struct Node {
    pub pos: (i32, i32),
    pub can_pass_through_walls: bool,
    pub start: Option<(i32, i32)>,
    pub end: Option<(i32, i32)>,
}

// Equivalent of the Clojure (node-at pos can-pass-through-walls? start end)
pub fn node_at(
    pos: (i32, i32),
    can_pass_through_walls: bool,
    start: Option<(i32, i32)>,
    end: Option<(i32, i32)>,
) -> Node {
    Node {
        pos,
        can_pass_through_walls,
        start,
        end,
    }
}

// Helper function equivalent to `within-bounds` in Clojure.
fn within_bounds(pos: (i32, i32), grid_size: usize) -> bool {
    let (x, y) = pos;
    x >= 0 && x < grid_size as i32 && y >= 0 && y < grid_size as i32
}

// Translated version of the `successors-p2` function.
fn successors_p2(
    walls: &HashSet<(i32, i32)>, // slice of wall positions
    grid_size: usize,
    node: &Node,
) -> Vec<Edge> {
    // Directions: up, down, right, left
    let deltas = [(0, 1), (0, -1), (1, 0), (-1, 0)];

    // Generate potential neighbors within grid bounds
    let (x, y) = node.pos;
    let mut normal_successors = Vec::new();
    for (dx, dy) in deltas {
        let new_pos = (x + dx, y + dy);
        if within_bounds(new_pos, grid_size) {
            normal_successors.push(new_pos);
        }
    }

    let mut successors = Vec::new();

    match (node.can_pass_through_walls, node.start) {
        (false, _) => {
            // (not can-pass-through-walls?)
            for p in normal_successors.into_iter().filter(|p| !walls.contains(p)) {
                successors.push(node_at(p, false, node.start, node.end));
            }
        }
        (true, Some(start_val)) => {
            // (can-pass-through-walls? and start != nil)
            // The Clojure code: (map #(node-at % false start %) ...)
            for p in normal_successors.into_iter().filter(|p| !walls.contains(p)) {
                successors.push(node_at(p, false, Some(start_val), Some(p)));
            }
        }
        (true, None) => {
            for p in normal_successors {
                if walls.contains(&p) {
                    // if it's a wall, store p in start
                    successors.push(node_at(p, true, Some(p), None));
                } else {
                    successors.push(node_at(p, true, None, None));
                }
            }
        }
    }

    // Convert each successor Node into a map entry { node -> 1 }
    let mut cost_map = vec![];
    for successor_node in successors {
        cost_map.push(Edge {
            node: successor_node,
            cost: 1,
        });
    }

    cost_map
}

// Dijkstra's shortest path algorithm.

// Start at `start` and use `dist` to track the current shortest distance
// to each node. This implementation isn't memory-efficient as it may leave duplicate
// nodes in the queue. It also uses `usize::MAX` as a sentinel value,
// for a simpler implementation.
fn shortest_path(
    map: &HashSet<(i32, i32)>,
    grid_size: usize,
    start: &Node,
) -> HashMap<Node, usize> {
    // dist[node] = current shortest distance from `start` to `node`
    let mut dist = HashMap::new();
    //for r in 0..grid_size {
    //    for c in 0..grid_size {
    //        if let Some(walls) = map.get(&'#') {
    //            if !walls.contains(&(r, c)) {
    //                dist.insert((r, c), usize::MAX);
    //            }
    //        }
    //    }
    //}

    let mut heap = BinaryHeap::new();

    // We're at `start`, with a zero cost
    dist.insert(*start, 0);
    heap.push(State {
        cost: 0,
        position: *start,
    });

    // Examine the frontier with lower cost nodes first (min-heap)
    while let Some(State { cost, position }) = heap.pop() {
        // Alternatively we could have continued to find all shortest paths
        //if position == *goal {
        //    return Some(cost);
        //}

        // Important as we may have already found a better way
        if cost > dist[&position] {
            continue;
        }

        // For each node we can reach, see if we can find a way with
        // a lower cost going through this node
        for edge in successors_p2(map, grid_size, &position) {
            let next = State {
                cost: cost + edge.cost,
                position: edge.node,
            };

            dist.entry(next.position).or_insert(usize::MAX);

            // If so, add it to the frontier and continue
            if next.cost < dist[&next.position] {
                heap.push(next);
                // Relaxation, we have now found a better way
                dist.insert(next.position, next.cost);
            }
        }
    }

    // Goal not reachable
    dist
}

#[aoc(day20, part1)]
fn part1(input: &HashMap<char, HashSet<(i32, i32)>>) -> usize {
    let walls = match input.get(&'#') {
        Some(walls) => walls,
        None => return 0,
    };

    let start_pos = *input[&'S']
        .iter()
        .next()
        .expect("Expected at least one 'S' position in the map");

    let end_pos = *input[&'E']
        .iter()
        .next()
        .expect("Expected at least one 'E' position in the map");

    let shortest_cost = shortest_path(walls, 15, &node_at(start_pos, true, None, None));

    let res: HashMap<_, _> = shortest_cost
        .iter()
        .filter(|(k, _)| k.pos == end_pos)
        .map(|(k, &v)| ((k.start, k.end), v))
        .collect();

    (2..65)
        .map(|i| {
            res.iter()
                // Each entry is ((start, end), value).
                // We want only those with end == 9316 - i.
                .filter(|(_, _value)| **_value == 84 - i)
                .count()
        })
        .sum()
}
