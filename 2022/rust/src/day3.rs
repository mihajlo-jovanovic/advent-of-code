use std::collections::HashSet;

use aoc_runner_derive::{aoc, aoc_generator};

#[aoc_generator(day3)]
fn generator_input(input: &str) -> Vec<String> {
    input.lines().map(|line| line.to_string()).collect()
}

fn priority(item: char) -> usize {
    if item.is_uppercase() {
        item as usize - 38
    } else {
        item as usize - 96
    }
}

fn common(rucksacks: Vec<&str>) -> char {
    *rucksacks
        .into_iter()
        .map(|s| s.to_owned())
        .reduce(|r1, r2| {
            let set1: HashSet<char> = r1.chars().collect();
            let set2: HashSet<char> = r2.chars().collect();
            set1.intersection(&set2).collect()
        })
        .unwrap()
        .chars()
        .collect::<Vec<char>>()
        .first()
        .unwrap()
}

#[aoc(day3, part1)]
fn part1(input: &[String]) -> usize {
    input
        .iter()
        .map(|s| {
            let (s1, s2) = s.split_at(s.len() / 2);
            priority(common(vec![s1, s2]))
        })
        .sum()
}

#[aoc(day3, part2)]
fn part2(input: &[String]) -> usize {
    input
        .chunks(3)
        .map(|s| priority(common(s.iter().map(|l| l as &str).collect())))
        .sum()
}

#[test]
fn test_priority() {
    assert_eq!(1, priority('a'));
    assert_eq!(2, priority('b'));
    assert_eq!(26, priority('z'));
    assert_eq!(27, priority('A'));
    assert_eq!(52, priority('Z'));
    assert_eq!(16, priority('p'));
    assert_eq!(38, priority('L'));
    assert_eq!(42, priority('P'));
    assert_eq!(22, priority('v'));
    assert_eq!(20, priority('t'));
    assert_eq!(19, priority('s'));
}

#[test]
fn test_common() {
    assert_eq!('p', common(vec!["vJrwpWtwJgWr", "hcsFMMfFFhFp"]));
    assert_eq!('L', common(vec!["jqHRNqRjqzjGDLGL", "rsFMfFZSrLrFZsSL"]));
    assert_eq!(
        'r',
        common(vec![
            "vJrwpWtwJgWrhcsFMMfFFhFp",
            "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL",
            "PmmdzqPrVvPwwTWBwg",
        ])
    );
    assert_eq!(
        'Z',
        common(vec![
            "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn",
            "ttgJtRGJQctTZtZT",
            "CrZsJsPPZsGzwwsLwLmpwMDw",
        ])
    );
}

#[test]
fn test_p1() {
    let input = "vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw";
    assert_eq!(157, part1(&generator_input(input)));
}

#[test]
fn test_p2() {
    let input = "vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw";
    assert_eq!(70, part2(&generator_input(input)));
}
