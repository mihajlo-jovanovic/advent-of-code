use itertools::Itertools;
use lazy_static::lazy_static;
use regex::Regex;
use std::collections::{HashMap, HashSet};
use std::ops::Range;

#[aoc_generator(day15)]
fn generator_input(input: &str) -> Vec<((isize, isize), (isize, isize))> {
    input
        .lines()
        .map(|line| {
            lazy_static! {
                static ref RE: Regex = Regex::new(
                    r"^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)$"
                )
                .unwrap();
            }
            if let Some(caps) = RE.captures(line) {
                let x1 = caps[1].parse::<isize>().unwrap();
                let y1 = caps[2].parse::<isize>().unwrap();
                let x2 = caps[3].parse::<isize>().unwrap();
                let y2 = caps[4].parse::<isize>().unwrap();
                ((x1, y1), (x2, y2))
            } else {
                panic!("Input string did not match regular expression");
            }
        })
        .collect()
}

#[aoc(day15, part1)]
fn part1(input: &[((isize, isize), (isize, isize))]) -> usize {
    let y_coord = 2000000;
    let rng = -3366052..7487719;
    do_part_1(input, y_coord, rng)
}

fn do_part_1(input: &[((isize, isize), (isize, isize))], y_coord: isize, rng: Range<i32>) -> usize {
    let m = scanner_ranges(input);
    let cnt = rng
        .filter(|x| {
            let p = (*x as isize, y_coord as isize);
            for (k, v) in &m {
                let d = distance(k, &p);
                if d <= *v {
                    return true;
                }
            }
            false
        })
        .count();
    cnt - beacons_at_y(input, y_coord)
}

fn beacons_at_y(input: &[((isize, isize), (isize, isize))], y_coord: isize) -> usize {
    let beacon_count: HashSet<(isize, isize)> = input
        .iter()
        .map(|(_, b)| b)
        .filter(|(_, y)| *y == y_coord)
        .cloned()
        .collect();
    beacon_count.len()
}

fn scanner_ranges(input: &[((isize, isize), (isize, isize))]) -> HashMap<(isize, isize), isize> {
    input
        .iter()
        .map(|(s, b)| (*s, distance(s, b)))
        .into_iter()
        .collect()
}

#[aoc(day15, part2)]
fn part2(input: &[((isize, isize), (isize, isize))]) -> usize {
    let search_area = 4000000;
    do_part_2(input, search_area)
}

fn do_part_2(input: &[((isize, isize), (isize, isize))], search_area: isize) -> usize {
    let m = scanner_ranges(input);
    let mut x = 0;
    while x <= search_area {
        let mut y = 0;
        while y <= search_area {
            let p = (x as isize, y as isize);
            let skip = m.iter().map(|(k, v)| *v - distance(k, &p)).max().unwrap();
            if skip < 0 {
                //println!("Found it: {:?}", p);
                return (4000000 * p.0 + p.1) as usize;
            }
            if skip > 0 {
                y += skip;
            } else {
                y += 1;
            }
        }
        x += 1;
    }
    panic!("Could not find solution")
}

fn distance(scanner: &(isize, isize), beacon: &(isize, isize)) -> isize {
    (scanner.0 - beacon.0).abs() + (scanner.1 - beacon.1).abs()
}

#[allow(dead_code)]
fn within_range(scanner: &(isize, isize), dist: isize) -> Vec<(isize, isize)> {
    let x = scanner.0;
    let y = scanner.1;
    (x - dist..=x + dist)
        .cartesian_product(y - dist..=y + dist)
        .filter(|b| distance(scanner, b) <= dist)
        .collect()
}

#[test]
fn test_parsing() {
    let input = "Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3";
    let parsed = generator_input(input);
    assert_eq!((2, 18), parsed[0].0);
    assert_eq!((-2, 15), parsed[0].1);
    assert_eq!((20, 1), parsed[13].0);
    assert_eq!((15, 3), parsed[13].1);
    assert_eq!(26, do_part_1(&parsed, 10, -40..40));
    assert_eq!(56000011, do_part_2(&parsed, 20));
}

#[test]
fn test_distance() {
    let scanner = (8, 7);
    let beacon = (2, 10);
    assert_eq!(distance(&scanner, &beacon), 9);
    let scanner = (2, 18);
    let beacon = (-2, 15);
    assert_eq!(distance(&scanner, &beacon), 7);
}

#[test]
fn test_within_range() {
    let scanner = (8, 7);
    let range = within_range(&scanner, 9);
    println!("{:?}", range);
    let scanner = (0, 0);
    let range = within_range(&scanner, 2);
    println!("{:?}", range);
}
