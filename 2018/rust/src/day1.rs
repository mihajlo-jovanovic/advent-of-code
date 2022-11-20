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

#[aoc(day1, part2)]
fn part2(input: &[i32]) -> i32 {
    let mut acc: HashSet<i32> = HashSet::new();
    acc.insert(0);
    let result: i32 = input
        .into_iter()
        .cycle()
        .scan(0, |state, &x| {
            *state += x;
            Some(*state)
        })
        .find(move |x| {
            if acc.contains(x) {
                true
            } else {
                acc.insert(*x);
                false
            }
        })
        .unwrap();
    result
}

#[test]
fn test_p2() {
    let input = vec![1, -2, 3, 1];
    let result = part2(&input);
    assert_eq!(2, result);
    assert_eq!(0, part2(&vec![1, -1]));
    assert_eq!(10, part2(&vec![3, 3, 4, -2, -4]));
    assert_eq!(5, part2(&vec![-6, 3, 8, 5, -6]));
    assert_eq!(14, part2(&vec![7, 7, -2, -7, -4]));
}