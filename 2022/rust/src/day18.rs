use std::collections::{HashMap, HashSet};

use itertools::{iproduct, Itertools};
use petgraph::algo::dijkstra;
use petgraph::{Directed, Graph};

#[aoc_generator(day18)]
fn generator_input(input: &str) -> Vec<(i8, i8, i8)> {
    input
        .lines()
        .map(|line| {
            let mut parts = line.split(',');
            (
                parts.next().unwrap().parse::<i8>().unwrap(),
                parts.next().unwrap().parse::<i8>().unwrap(),
                parts.next().unwrap().parse::<i8>().unwrap(),
            )
        })
        .collect()
}

fn pixels3d(max: i8) -> Vec<(i8, i8, i8)> {
    iproduct!(0..max, 0..max, 0..max).collect()
}

fn neighbors(p: &(i8, i8, i8)) -> Vec<(i8, i8, i8)> {
    vec![
        (p.0 + 1, p.1, p.2),
        (p.0 - 1, p.1, p.2),
        (p.0, p.1 + 1, p.2),
        (p.0, p.1 - 1, p.2),
        (p.0, p.1, p.2 + 1),
        (p.0, p.1, p.2 - 1),
    ]
}

fn is_adjacent(c1: &(i8, i8, i8), c2: &(i8, i8, i8)) -> bool {
    neighbors(c1).contains(c2)
}

#[aoc(day18, part1)]
fn part1(input: &[(i8, i8, i8)]) -> usize {
    (input.len() * 6)
        - input
            .iter()
            .cartesian_product(input)
            .filter(|(c1, c2)| is_adjacent(c1, c2))
            .count()
}

#[aoc(day18, part2)]
fn part2(input: &[(i8, i8, i8)]) -> usize {
    let mut graph: Graph<(), (), Directed> = Graph::new();
    let mut nodes = HashMap::new();
    for i in input {
        let n = graph.add_node(());
        nodes.insert(i, n);
    }
    let all_points = pixels3d(30);
    let adj_list: Vec<(&(i8, i8, i8), (i8, i8, i8))> = all_points
        .iter()
        .flat_map(|v| neighbors(v).into_iter().map(move |t| (v, t)))
        .filter(|&(v, _)| !input.contains(v))
        .collect();
    for (v1, v2) in &adj_list {
        let n1 = *nodes.entry(*v1).or_insert_with(|| graph.add_node(()));
        let n2 = *nodes.entry(v2).or_insert_with(|| graph.add_node(()));
        graph.add_edge(n1, n2, ());
    }
    let n = nodes.get(&(0, 0, 0)).unwrap();
    let res = dijkstra(&graph, *n, None, |_| 1);
    // cubes are outside if they are reachable from an outside point (0,0,0)
    let outside: Vec<(i8, i8, i8)> = input
        .iter()
        .filter(|k| res.get(nodes.get(k).unwrap()).is_some())
        .cloned()
        .collect();
    let air_pockets: HashSet<&(i8, i8, i8)> = nodes
        .iter()
        .filter(|(_, v)| res.get(v).is_none())
        .map(|(k, _)| *k)
        .collect();

    part1(&outside)
        - outside
            .iter()
            .flat_map(neighbors)
            .filter(|n| air_pockets.contains(n))
            .count()
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::fs;

    const INPUT: &str = "2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5";

    #[test]
    fn test_parsing() {
        assert_eq!(13, generator_input(&INPUT).len());
        assert_eq!(Some(&(2, 2, 2)), generator_input(&INPUT).first());
        assert_eq!(Some(&(2, 3, 5)), generator_input(&INPUT).last());
    }

    #[test]
    fn test_neighbors() {
        let p = (1, 1, 1);
        assert_eq!(6, neighbors(&p).len());
        assert!(neighbors(&p).contains(&(1, 1, 2)));
    }

    #[test]
    fn test_pixels() {
        let all_points = pixels3d(20);
        assert_eq!(all_points.len(), 8000);
        assert!(all_points.contains(&(1, 1, 1)));
        assert!(all_points.contains(&(19, 0, 19)));
    }

    #[test]
    fn test_is_adjacent() {
        let c1 = (1, 1, 1);
        let c2 = (1, 1, 2);
        assert!(is_adjacent(&c1, &c2));
        // adjacent is commutative
        assert!(is_adjacent(&c2, &c1));
        let c3 = (1, 2, 2);
        assert!(!is_adjacent(&c1, &c3));
    }

    #[test]
    fn test_part1() {
        let input = fs::read_to_string("input/2022/additional.txt").unwrap();
        assert_eq!(64, part1(&generator_input(INPUT)));
        assert_eq!(108, part1(&generator_input(&input)));
    }

    #[test]
    fn test_part2() {
        let input = fs::read_to_string("input/2022/additional.txt").unwrap();
        assert_eq!(58, part2(&generator_input(INPUT)));
        assert_eq!(90, part2(&generator_input(&input)));
    }
}
