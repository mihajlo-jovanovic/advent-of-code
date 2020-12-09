use lazy_static::lazy_static;
use petgraph::algo::has_path_connecting;
use petgraph::{visit::EdgeRef, Graph};
use regex::Regex;
use std::collections::HashMap;

#[aoc_generator(day7)]
fn generator_input(input: &str) -> Graph<String, u32> {
    let edges: Vec<(&str, &str, u32)> = input
        .lines()
        .flat_map(move |l| {
            lazy_static! {
                static ref ROOT_BAG_RE: Regex =
                    Regex::new(r"^(?P<root>[[:word:]]+\s[[:word:]]+)").unwrap();
                static ref EDGE_BAG_RE: Regex =
                    Regex::new(r"(?P<num>[0-9]) (?P<color>[[:word:]]+\s[[:word:]]+) (bags|bag)")
                        .unwrap();
            }
            let caps = ROOT_BAG_RE.captures(l).expect("Bad line");
            let root = caps.name("root").unwrap().as_str();
            let iter = EDGE_BAG_RE.captures_iter(l).map(move |b| {
                let wgt: u32 = b
                    .name("num")
                    .unwrap()
                    .as_str()
                    .parse()
                    .expect("Parse error");
                let color = b.name("color").unwrap().as_str();
                (root, color, wgt)
            });
            iter
        })
        .collect();

    let mut g = Graph::new();
    let mut nodes = HashMap::new();
    for (v1, v2, wgt) in edges {
        let n1 = *nodes
            .entry(v1)
            .or_insert_with(|| g.add_node(v1.to_string()));
        let n2 = *nodes
            .entry(v2)
            .or_insert_with(|| g.add_node(v2.to_string()));
        g.add_edge(n1, n2, wgt);
    }
    g
}

#[aoc(day7, part1)]
fn part1(g: &Graph<String, u32>) -> usize {
    let shiny_gold = g.node_indices().find(|i| g[*i] == "shiny gold").unwrap();
    g.node_indices()
        .filter(|n| has_path_connecting(&g, *n, shiny_gold, None))
        .count()
        - 1
}

#[aoc[day7, part2]]
fn part2(g: &Graph<String, u32>) -> u32 {
    let shiny_gold = g.node_indices().find(|i| g[*i] == "shiny gold").unwrap();
    let mut acc = 0;

    let mut neighbors: Vec<_> = g
        .edges(shiny_gold)
        .map(|e| (e.target(), *e.weight()))
        .collect();

    while !neighbors.is_empty() {
        let mut new_neighbors = vec![];

        for (neighbor, wgt) in neighbors {
            acc += wgt;
            new_neighbors.extend(g.edges(neighbor).map(|e| (e.target(), wgt * e.weight())));
        }
        neighbors = new_neighbors;
    }
    acc
}

#[test]
fn test_generator() {
    let g = generator_input(
        "light red bags contain 1 bright white bag, 2 muted yellow bags.
dark orange bags contain 3 bright white bags, 4 muted yellow bags.
bright white bags contain 1 shiny gold bag.
muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
dark olive bags contain 3 faded blue bags, 4 dotted black bags.
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
dotted black bags contain no other bags.",
    );
    println!("{:?}", g);
    assert_eq!(4, part1(&g));
    assert_eq!(32, part2(&g));
}
