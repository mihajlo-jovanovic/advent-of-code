use petgraph::algo::toposort;
use petgraph::graph::{DiGraph, NodeIndex};
use std::collections::HashMap;
use thiserror::Error;

type Graph = DiGraph<u32, ()>;
type NodeMap = HashMap<NodeIndex, String>;

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
fn input_generator(input: &str) -> (Graph, NodeMap) {
    let adjacency_list: Vec<(String, String)> = input.lines().flat_map(parse_line).collect();
    let node_weights: HashMap<String, u32> = input
        .lines()
        .map(parse_line_weights)
        .collect::<Result<HashMap<_, _>, crate::day7::Day7Error>>()
        .expect("Failed to parse weight");

    let mut graph = Graph::new();
    let mut nodes = HashMap::new();

    // Build graph
    for (v1, v2) in adjacency_list {
        let n1 = *nodes
            .entry(v1.clone())
            .or_insert_with(|| graph.add_node(node_weights[&v1]));
        let n2 = *nodes
            .entry(v2.clone())
            .or_insert_with(|| graph.add_node(node_weights[&v2]));
        graph.add_edge(n1, n2, ());
    }

    let node_labels = nodes.into_iter().map(|(k, v)| (v, k)).collect();
    (graph, node_labels)
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

/// Finds the index of the unique value in a slice of numbers.
/// Returns None if the slice has fewer than 3 elements or if no unique value is found.
fn find_unique_index(numbers: &[u32]) -> Option<usize> {
    if numbers.len() < 3 {
        return None;
    }

    // Identify the majority value (the value that is not unique).
    let majority = match (numbers[0], numbers[1], numbers[2]) {
        (a, b, _) if a == b => a,
        (a, _, c) if a == c => a,
        (_, b, _) => b,
    };

    numbers
        .iter()
        .enumerate()
        .find(|(_, &num)| num != majority)
        .map(|(i, _)| i)
}

/// Recursively calculates and updates the total weights of nodes in the graph.
/// Each node's weight is updated to include the sum of its children's weights.
fn sum_all_weights(graph: &mut DiGraph<u32, ()>, root: NodeIndex) -> u32 {
    let children: Vec<NodeIndex> = graph.neighbors(root).collect();

    let children_sum: u32 = children
        .iter()
        .map(|&child| sum_all_weights(graph, child))
        .sum();

    let node_weight = graph[root];
    graph[root] = node_weight + children_sum;
    graph[root]
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

#[derive(Error, Debug, PartialEq)]
pub enum Day7Error {
    #[error("failed to parse weight: {0}")]
    WeightParse(#[from] std::num::ParseIntError),
    #[error("missing node name")]
    MissingNode,
}
fn parse_line_weights(line: &str) -> Result<(String, u32), crate::day7::Day7Error> {
    let mut parts = line
        .split(" -> ")
        .next()
        .ok_or(Day7Error::MissingNode)?
        .split_whitespace();
    let node = parts.next().ok_or(Day7Error::MissingNode)?.to_string();
    let weight = parts
        .next()
        .ok_or(Day7Error::MissingNode)?
        .trim_matches(|c| c == '(' || c == ')')
        .parse()?;
    Ok((node, weight))
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
        Ok((String::from("kpjxln"), 44))
    );
}
