use std::str::Split;

use aoc_runner_derive::{aoc, aoc_generator};

type Range = (u8, u8);

fn contains(this: &Range, other: &Range) -> bool {
    this.0 <= other.0 && this.1 >= other.1
}

fn overlaps(this: &Range, other: &Range) -> bool {
    (this.0 <= other.0 && this.1 >= other.0) || (other.0 <= this.0 && other.1 >= this.0)
}

#[aoc_generator(day4)]
fn generator_input(input: &str) -> Vec<(Range, Range)> {
    input
        .lines()
        .map(|line| {
            let mut parts = line.split(',');
            (parse_range(&mut parts), parse_range(&mut parts))
        })
        .collect()
}

fn parse_range(parts: &mut Split<char>) -> (u8, u8) {
    let mut first_elf = parts.next().unwrap().split('-');
    let start = first_elf.next().unwrap().parse::<u8>().unwrap();
    let end = first_elf.next().unwrap().parse::<u8>().unwrap();
    (start, end)
}

#[aoc(day4, part1)]
fn part1(section_ranges: &[(Range, Range)]) -> usize {
    section_ranges.iter().fold(0, |acc, (r1, r2)| {
        if contains(r1, r2) || contains(r2, r1) {
            acc + 1
        } else {
            acc
        }
    })
}

#[aoc(day4, part2)]
fn part2(section_ranges: &[(Range, Range)]) -> usize {
    section_ranges.iter().fold(
        0,
        |acc, (r1, r2)| {
            if overlaps(r1, r2) {
                acc + 1
            } else {
                acc
            }
        },
    )
}

#[test]
fn test_contains() {
    assert!(!contains(&(2, 4), &(6, 6)));
    assert!(contains(&(2, 8), &(3, 7)));
    assert!(contains(&(4, 6), &(6, 6)));
    // contains is not commutative
    assert!(!contains(&(6, 6), &(4, 6)));
}

#[test]
fn test_overlap() {
    assert!(!overlaps(&(2, 4), &(6, 8)));
    assert!(overlaps(&(2, 8), &(3, 7)));
    assert!(overlaps(&(4, 6), &(6, 6)));
    // overlap IS commutative
    assert!(overlaps(&(6, 6), &(4, 6)));
}
