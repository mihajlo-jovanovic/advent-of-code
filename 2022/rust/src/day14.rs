use std::cmp::{max, min};
use std::collections::HashSet;

use itertools::Itertools;

#[aoc_generator(day14)]
fn generator_input(input: &str) -> HashSet<(u16, u16)> {
    input
        .lines()
        .flat_map(|s| {
            let points = s.split(" -> ");
            let tmp: Vec<(u16, u16)> = points
                .map(|p| {
                    let mut x = p.split(',');
                    (
                        x.next().unwrap().parse::<u16>().unwrap(),
                        x.next().unwrap().parse::<u16>().unwrap(),
                    )
                })
                .collect();
            helper(&tmp)
        })
        .collect()
}

fn helper(path: &[(u16, u16)]) -> HashSet<(u16, u16)> {
    path.windows(2)
        .flat_map(|p| {
            (min(p[0].0, p[1].0)..max(p[0].0, p[1].0) + 1)
                .cartesian_product(min(p[0].1, p[1].1)..max(p[0].1, p[1].1) + 1)
        })
        .collect()
}

#[aoc(day14, part1)]
fn part1(input: &HashSet<(u16, u16)>) -> usize {
    println!("{:?}", input);
    input.len()
}

#[test]
fn test_generator_input() {
    let parsed_path = generator_input(
        &"498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9",
    );
    println!("{:?}", parsed_path);
    assert_eq!(parsed_path.len(), 20);
}
