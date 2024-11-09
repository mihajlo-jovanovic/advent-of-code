use petgraph::algo::toposort;
use petgraph::graph::{DiGraph, NodeIndex};
use std::collections::HashMap;

#[aoc_generator(day7)]
/// Generates a directed graph and a mapping of node indices to their labels from the input string.
///
/// # Arguments
///
/// * `input` - A string slice that holds the input data.
///
/// # Returns
///
/// A tuple containing:
/// - A directed graph where nodes are labeled with weights.
/// - A HashMap mapping node indices to their corresponding labels.
fn input_generator(input: &str) -> (DiGraph<u32, ()>, HashMap<NodeIndex, String>) {
    let adjacency_list: Vec<(String, String)> = input.lines().flat_map(parse_line).collect();
    let node_weights: HashMap<String, u32> = input.lines().map(parse_line_weights).collect();

    let mut g = DiGraph::<u32, ()>::new();
    let mut nodes = HashMap::new();
    for (v1, v2) in adjacency_list {
        let label1 = v1.to_owned();
        let label2 = v2.to_owned();
        let n1 = *nodes
            .entry(v1)
            .or_insert_with(|| g.add_node(node_weights[&label1]));
        let n2 = *nodes
            .entry(v2)
            .or_insert_with(|| g.add_node(node_weights[&label2]));
        g.add_edge(n1, n2, ());
    }
    let mut node_labels: HashMap<NodeIndex, String> = HashMap::new();
    // Iterate over the nodes HashMap to create a new HashMap that maps node indices to their labels
    for (lbl, idx) in nodes {
        node_labels.insert(idx, lbl);
    }
    (g, node_labels)
}

#[aoc(day7, part1)]
fn part1(input: &(DiGraph<u32, ()>, HashMap<NodeIndex, String>)) -> String {
    let tp = toposort(&input.0, None).expect("error performing topological sort");
    input.1[&tp[0]].clone()
}

#[aoc(day7, part2)]
fn part2(input: &(DiGraph<u32, ()>, HashMap<NodeIndex, String>)) -> u32 {
    let root = toposort(&input.0, None).expect("error performing topological sort")[0];
    let graph = &mut input.0.clone();
    sum_all_weights(graph, root);
    let g = graph.clone();
    part2_helper_rec(&g, root, 0)
}

fn part2_helper_rec(graph: &DiGraph<u32, ()>, root: NodeIndex, diff: u32) -> u32 {
    let children: Vec<NodeIndex> = graph.neighbors(root).collect();
    //    if children.is_empty() {
    //        return graph[root]-children.iter().map(|i| graph[*i]).sum::<u32>()-diff
    //    }
    let child_weights: Vec<u32> = children.iter().map(|idx| graph[*idx]).collect();
    if let Some(unq) = find_unique_index(&child_weights) {
        let new_root = children[unq];
        let majority = if unq == 0 { 1 } else { 0 };
        let df2 = child_weights[unq] - child_weights[majority];

        part2_helper_rec(graph, new_root, df2)
    } else {
        return graph[root] - children.iter().map(|i| graph[*i]).sum::<u32>() - diff;
    }
}

fn find_unique_index(numbers: &[u32]) -> Option<usize> {
    if numbers.len() < 3 {
        return None;
    }

    // Identify the majority value (the value that is not unique).
    let majority = if numbers[0] == numbers[1] || numbers[0] == numbers[2] {
        numbers[0]
    } else {
        numbers[1]
    };

    // Find the index of the number that differs from the majority.
    for (i, &num) in numbers.iter().enumerate() {
        if num != majority {
            return Some(i);
        }
    }

    None
}

fn sum_all_weights(graph: &mut DiGraph<u32, ()>, root: NodeIndex) {
    let children: Vec<NodeIndex> = graph.neighbors(root).collect();
    if children.is_empty() {
        return;
    }

    for c in &children {
        sum_all_weights(graph, *c);
    }

    let total: u32 = children.iter().map(|idx| graph[*idx]).sum();
    if let Some(w) = graph.node_weight_mut(root) {
        *w += total;
    }
}

fn parse_line(l: &str) -> Vec<(String, String)> {
    let parts: Vec<&str> = l.split(" -> ").collect();
    let node = parts[0].split_whitespace().next().unwrap_or("");
    if parts.len() == 1 {
        return vec![];
    }
    parts[1]
        .split(", ")
        .map(|s| (node.to_string(), s.to_string()))
        .collect::<Vec<(String, String)>>()
}

fn parse_line_weights(l: &str) -> (String, u32) {
    let parts: Vec<&str> = l.split(" -> ").collect();
    let mut first_part = parts[0].split_whitespace();
    let node = first_part.next().unwrap_or("");
    let w = first_part.next().unwrap_or("");
    (
        node.to_string(),
        w.trim_matches(|c| c == '(' || c == ')')
            .parse::<u32>()
            .expect(""),
    )
}

#[test]
fn test_parsing() {
    assert_eq!(
        parse_line("kpjxln (44) -> dzzbvkv, gzdxgvj"),
        vec![
            (String::from("kpjxln"), String::from("dzzbvkv")),
            (String::from("kpjxln"), String::from("gzdxgvj"))
        ]
    );
}

#[test]
fn test_parse_weights() {
    assert_eq!(
        parse_line_weights("kpjxln (44) -> dzzbvkv, gzdxgvj"),
        (String::from("kpjxln"), 44)
    );
}
