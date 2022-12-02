use aoc_runner_derive::{aoc, aoc_generator};

#[aoc_generator(day2)]
fn generator_input(input: &str) -> Vec<(char, char)> {
    input
        .lines()
        .map(|line| {
            let l: Vec<char> = line.chars().collect();
            (l[0], l[2])
        })
        .collect()
}

fn bonus(shape: &char) -> usize {
    match shape {
        'X' => 1,
        'Y' => 2,
        'Z' => 3,
        _ => {
            panic!("ups, did not expect this shape.")
        }
    }
}

fn score(p1: &char, p2: &char) -> usize {
    bonus(p2)
        + match (p1, p2) {
            ('A', 'X') => 3,
            ('A', 'Y') => 6,
            ('A', 'Z') => 0,
            ('B', 'X') => 0,
            ('B', 'Y') => 3,
            ('B', 'Z') => 6,
            ('C', 'X') => 6,
            ('C', 'Y') => 0,
            ('C', 'Z') => 3,
            _ => 0,
        }
}

fn score_p2(p1: &char, p2: &char) -> usize {
    match (p1, p2) {
        ('A', 'X') => bonus(&'Z'),
        ('A', 'Y') => 3 + bonus(&'X'),
        ('A', 'Z') => 6 + bonus(&'Y'),
        ('B', 'X') => bonus(&'X'),
        ('B', 'Y') => 3 + bonus(&'Y'),
        ('B', 'Z') => 6 + bonus(&'Z'),
        ('C', 'X') => bonus(&'Y'),
        ('C', 'Y') => 3 + bonus(&'Z'),
        ('C', 'Z') => 6 + bonus(&'X'),
        _ => 0,
    }
}

#[aoc(day2, part1)]
fn part1(input: &[(char, char)]) -> usize {
    input.iter().map(|(p1, p2)| score(p1, p2)).sum()
}

#[aoc(day2, part2)]
fn part2(input: &[(char, char)]) -> usize {
    input.iter().map(|(p1, p2)| score_p2(p1, p2)).sum()
}

#[test]
fn test_parsing() {
    let input = "A Y
B X
C Z";
    let res = generator_input(&input);
    assert_eq!(res, vec![('A', 'Y'), ('B', 'X'), ('C', 'Z')]);
    assert_eq!(15, part1(&res));
    assert_eq!(12, part2(&res));
}

#[test]
fn test_score() {
    assert_eq!(8, score(&'A', &'Y'));
    assert_eq!(1, score(&'B', &'X'));
    assert_eq!(6, score(&'C', &'Z'));
}
