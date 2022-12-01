use aoc_runner_derive::{aoc, aoc_generator};

#[aoc_generator(day1)]
fn generator_input(input: &str) -> Vec<Vec<usize>> {
    input
        .split("\n\n")
        .into_iter()
        .map(|grp| {
            grp.lines()
                .map(|l| l.parse::<usize>().expect("not a number"))
                .collect()
        })
        .collect()
}

#[aoc(day1, part1)]
fn part1(input: &[Vec<usize>]) -> usize {
    input.iter().map(|l| l.iter().sum()).max().unwrap()
}

#[aoc(day1, part2)]
fn part2(input: &[Vec<usize>]) -> usize {
    let mut l: Vec<usize> = input.iter().map(|l| l.iter().sum()).collect();
    l.sort();
    l[l.len() - 3..].iter().sum()
}
