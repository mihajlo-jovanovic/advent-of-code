use aoc_runner_derive::{aoc, aoc_generator};
use std::collections::HashSet;

#[aoc_generator(day1)]
fn generator_input(input: &str) -> Vec<i32> {
    input
        .lines()
        .map(|l| l.parse::<i32>().expect("not a number?"))
        .collect()
}

#[aoc(day1, part1)]
fn part1(input: &[i32]) -> i32 {
    input.iter().sum()
}