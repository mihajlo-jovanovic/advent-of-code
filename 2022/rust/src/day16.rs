use lazy_static::lazy_static;
use petgraph::algo::dijkstra;
use petgraph::{Directed, Graph};
use regex::Regex;
use std::collections::HashMap;
use std::fmt::Debug;

#[derive(Debug, Eq, PartialEq, Clone)]
pub struct Valve {
    label: String,
    flow_rate: u16,
    tunnels: Vec<String>,
}

impl Valve {
    pub fn new(label: String, flow_rate: u16, tunnels: Vec<String>) -> Self {
        Self {
            label,
            flow_rate,
            tunnels,
        }
    }
}

pub trait Backtracking {
    fn release_pressure(&self) -> u16;
    fn release_pressure_r(
        &self,
        open: Vec<&Valve>,
        closed: Vec<&Valve>,
        at: &Valve,
        remaining_mins: u8,
    ) -> u16;
}

pub struct ValveSystemState {
    valves: Vec<Valve>,
    dist: Vec<Vec<u8>>,
}

impl ValveSystemState {
    pub fn new(valves: Vec<Valve>, dist: Vec<Vec<u8>>) -> Self {
        Self { valves, dist }
    }
}

impl Backtracking for ValveSystemState {
    fn release_pressure(&self) -> u16 {
        let closed: Vec<&Valve> = self.valves.iter().filter(|&v| v.flow_rate > 0).collect();
        let start = self.valves.iter().find(|&v| v.label == "AA").unwrap();
        //println!("Starting at {:?}", start);
        self.release_pressure_r(vec![], closed, start, 30)
    }

    fn release_pressure_r(
        &self,
        open: Vec<&Valve>,
        closed: Vec<&Valve>,
        at: &Valve,
        remaining_mins: u8,
    ) -> u16 {
        if closed.is_empty() {
            return open
                .iter()
                .map(|&v| v.flow_rate * (remaining_mins as u16))
                .sum();
        } else {
            closed
                .iter()
                .map(|&c| {
                    let start = self.valves.iter().position(|v| v == at).unwrap();
                    let end = self.valves.iter().position(|v| v == c).unwrap();
                    let d = self.dist[start][end];
                    //println!("Distance is: {:?}", d);
                    let total_pressure: u16 =
                        open.iter().map(|&v| v.flow_rate * (d as u16 + 1)).sum();
                    let mut new_open = open.clone();
                    new_open.push(c);
                    let new_closed: Vec<&Valve> =
                        closed.iter().filter(|&&v| v != c).cloned().collect();
                    if d + 1 >= remaining_mins {
                        // need to re-calculate as remaining mins is smaller
                        let total_pressure: u16 = open
                            .iter()
                            .map(|&v| v.flow_rate * (remaining_mins as u16))
                            .sum();
                        total_pressure
                    } else {
                        self.release_pressure_r(new_open, new_closed, c, remaining_mins - d - 1)
                            + total_pressure
                    }
                })
                .max()
                .unwrap()
        }
    }
}

#[aoc_generator(day16)]
fn generator_input(input: &str) -> Vec<Valve> {
    let parsed: Vec<Valve> = input
        .lines()
        .map(|line| {
            lazy_static! {
                static ref RE_VALVES: Regex = Regex::new(
                    r"Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (([A-Z]{2}),?\s?)+"
                )
                .unwrap();
                static ref RE_TUNNELS: Regex = Regex::new(
                    r"(?P<label>[A-Z]{2}),?\s?"
                )
                .unwrap();
            }
            let iter = RE_TUNNELS.captures_iter(line).map(move |b| {
                let t: String = b
                    .name("label")
                    .unwrap()
                    .as_str()
                    .parse()
                    .expect("Parse error");
                t
            });
            let tunnels: Vec<String> = iter.skip(1).collect();
            if let Some(caps) = RE_VALVES.captures(line) {
                let label = caps[1].to_owned();
                let flow_rate = caps[2].parse::<u16>().unwrap();
                Valve::new(label, flow_rate, tunnels)
            } else {
                panic!("Input string did not match regular expression");
            }
        })
        .collect();
    parsed
}

#[aoc(day16, part1)]
fn part1(valves: &[Valve]) -> u16 {
    // First, build a graph of tunnels
    let mut graph: Graph<String, u16, Directed> = Graph::new();
    let mut nodes = HashMap::new();
    let adj_list: Vec<(String, String)> = valves
        .iter()
        .flat_map(|v| v.tunnels.iter().map(|t| (v.label.to_owned(), t.to_owned())))
        .collect();
    for (v1, v2) in adj_list {
        let label1 = v1.to_owned();
        let label2 = v2.to_owned();
        let n1 = *nodes.entry(v1).or_insert_with(|| graph.add_node(label1));
        let n2 = *nodes.entry(v2).or_insert_with(|| graph.add_node(label2));
        graph.add_edge(n1, n2, 1);
    }
    //println!("{:?}", Dot::with_config(&graph, &[Config::EdgeNoLabel]));
    // Now, let's pre-compute shortest paths to each pair of valves
    let mut dist: Vec<Vec<u8>> = vec![];
    for v in valves {
        let n = nodes.get(&v.label).unwrap();
        let res = dijkstra(&graph, *n, None, |_| 1);
        let tmp: Vec<u8> = valves
            .iter()
            .map(|v| *res.get(nodes.get(&v.label).unwrap()).unwrap())
            .collect();
        dist.push(tmp);
    }
    let tmp = ValveSystemState::new(valves.to_vec(), dist);
    tmp.release_pressure()
}

#[test]
fn test_parsing() {
    let input = "Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II";
    let valves = generator_input(&input);
    assert_eq!(valves.len(), 10);
    assert_eq!(
        *valves.get(0).unwrap(),
        Valve::new(
            String::from("AA"),
            0,
            vec![String::from("DD"), String::from("II"), String::from("BB")],
        )
    );
    assert_eq!(
        *valves.get(9).unwrap(),
        Valve::new(String::from("JJ"), 21, vec![String::from("II")])
    );
    assert_eq!(1651, part1(&valves));
}
