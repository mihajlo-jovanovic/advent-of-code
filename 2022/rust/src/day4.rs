use std::str::Split;

use aoc_runner_derive::{aoc, aoc_generator};

#[derive(Debug)]
struct Range {
    id: usize,
    start: usize,
    end: usize,
}

impl Range {
    fn new(id: usize, start: usize, end: usize) -> Self {
        Range { id, start, end }
    }

    fn contains(&self, other: &Range) -> bool {
        self.start <= other.start && self.end >= other.end
    }

    fn overlaps(&self, other: &Range) -> bool {
        (self.start <= other.start && self.end >= other.start)
            || (other.start <= self.start && other.end >= self.start)
    }
}

#[aoc_generator(day4)]
fn generator_input(input: &str) -> Vec<(Range, Range)> {
    let mut id: usize = 0;
    input
        .lines()
        .map(|line| {
            let mut parts = line.split(',');
            let range1 = parse_range(id, &mut parts);
            id += 1;
            let range2 = parse_range(id, &mut parts);
            id += 1;
            (range1, range2)
        })
        .collect()
}

fn parse_range(id: usize, parts: &mut Split<char>) -> Range {
    let (start, end) = parse_start_end(parts);
    Range::new(id, start, end)
}

fn parse_start_end(parts: &mut Split<char>) -> (usize, usize) {
    let mut first_elf = parts.next().unwrap().split('-');
    let start = first_elf.next().unwrap().parse::<usize>().unwrap();
    let end = first_elf.next().unwrap().parse::<usize>().unwrap();
    (start, end)
}

#[aoc(day4, part1)]
fn part1(section_ranges: &[(Range, Range)]) -> usize {
    section_ranges.iter().fold(0, |acc, (r1, r2)| {
        if r1.contains(r2) || r2.contains(r1) {
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
            if r1.overlaps(r2) {
                acc + 1
            } else {
                acc
            }
        },
    )
}

#[test]
fn test_contains() {
    let range1 = Range::new(1, 2, 4);
    let range2 = Range::new(2, 6, 6);
    assert!(!range1.contains(&range2));
    let range3 = Range::new(1, 2, 8);
    let range4 = Range::new(2, 3, 7);
    assert!(range3.contains(&range4));
    let range5 = Range::new(1, 4, 6);
    let range6 = Range::new(2, 6, 6);
    assert!(range5.contains(&range6));
    // contains is not commutative
    assert!(!range6.contains(&range5));
}

#[test]
fn test_overlap() {
    let range1 = Range::new(1, 2, 4);
    let range2 = Range::new(2, 6, 8);
    assert!(!range1.overlaps(&range2));
    let range3 = Range::new(1, 2, 8);
    let range4 = Range::new(2, 3, 7);
    assert!(range3.overlaps(&range4));
    let range5 = Range::new(1, 4, 6);
    let range6 = Range::new(2, 6, 6);
    assert!(range5.overlaps(&range6));
    // overlap IS commutative
    assert!(range6.overlaps(&range5));
}
