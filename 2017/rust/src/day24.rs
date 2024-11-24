use petgraph::graph::{NodeIndex, UnGraph};
use petgraph::dot::{Dot, Config};
use std::collections::HashMap;
use std::collections::HashSet;

type Graph = UnGraph<usize, ()>;
type NodeMap = HashMap<NodeIndex, usize>;

#[aoc_generator(day24)]
fn input_generator(input: &str) -> (Graph, NodeMap) {
    let adjacency_list: Vec<(usize, u8)> = input
        .lines()
        .map(|line| {
            let mut iter = line.split('/');
            let a = iter.next().unwrap().parse().unwrap();
            let b = iter.next().unwrap().parse().unwrap();
            (a, b)
        })
        .collect();

    let mut graph = Graph::new_undirected();
    let mut nodes = HashMap::new();

    // Build graph
    for (v1, v2) in adjacency_list {
        let n1 = *nodes.entry(v1).or_insert_with(|| graph.add_node(v1));
        let n2 = *nodes
            .entry(v2.into())
            .or_insert_with(|| graph.add_node(v2.into()));
        graph.add_edge(n1, n2, ());
    }
    // Output the tree to `graphviz` `DOT` format
    println!("{:?}", Dot::with_config(&graph, &[Config::EdgeNoLabel]));

    let node_labels = nodes.into_iter().map(|(k, v)| (v, k)).collect();
    (graph, node_labels)
}

fn dfs(g: &Graph, start: &NodeIndex) -> usize {
    let visited = HashSet::new();
    let mut stack = vec![(*start, visited, 0)];
    let mut result: usize = 0;
    while let Some((curr, seen, d)) = stack.pop() {
        println!("curr: {}, d: {}", g[curr], d);
        let mut seen_new = seen.clone();
        let children = g.neighbors(curr).collect::<Vec<_>>();

        if !seen.contains(&(curr, curr)) {
        if let Some(n) = children.iter().find(|n2| *n2 == &curr) {
            seen_new.insert((curr, *n));
            stack.push((*n, seen_new.clone(), d + g[curr] + g[*n]));
            continue;
        }}

        for n in children {
            if seen_new.contains(&(curr, n)) || seen_new.contains(&(n, curr)) {
                continue;
            }
            seen_new.insert((curr, n));
            println!("inserting to seen: {} -> {}", g[curr], g[n]);
            stack.push((n, seen_new.clone(), d + g[curr] + g[n]));
        }

        //        for (n, vis, w) in &children {
        if d > result {
            result = d;
        }
        //            stack.push((&n, vis.clone(), *w));
        //        }
    }
    result
}

#[aoc(day24, part1)]
fn part1(input: &(Graph, HashMap<NodeIndex, usize>)) -> usize {
    let root: &NodeIndex = input
        .1
        .iter()
        .filter(|(_, v)| **v == 0)
        .map(|(k, _)| k)
        .next()
        .unwrap();

    // println!("num of edges: {}", input.0.edge_count());
    //    let g = &input.0;
    //    g.node_indices().for_each(|n| {
    //        println!("{}: {:?}", n.index(), g.neighbors(n).collect::<Vec<_>>());
    //    });
    //    *g.node_weights()
    //        .max()
    //        .unwrap()
    //
    dfs(&input.0, root)
}
