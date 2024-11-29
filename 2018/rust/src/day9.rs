use std::collections::VecDeque;

#[aoc_generator(day9)]
fn input_generator(input: &str) -> (usize, usize) {
    let parts: Vec<&str> = input.split_whitespace().collect();
    let players = parts[0].parse::<usize>().expect("Failed to parse players");
    let points = parts[6].parse::<usize>().expect("Failed to parse points");
    (players, points)
}

#[aoc(day9, part1)]
fn solve_part1(input: &(usize, usize)) -> usize {
    let (players, points) = input;
    let mut scores = vec![0; *players];
    let mut circle = VecDeque::new();
    circle.push_back(0);
    for marble in 1..=*points {
        if marble % 23 == 0 {
            circle.rotate_right(7);
            let score = marble + circle.pop_back().unwrap();
            circle.rotate_left(1);
            let player = (marble - 1) % *players;
            scores[player] += score;
        } else {
            circle.rotate_left(1);
            circle.push_back(marble);
        }
    }
    *scores.iter().max().unwrap()
}

#[aoc(day9, part2)]
fn solve_part2(input: &(usize, usize)) -> usize {
    solve_part1(&(input.0, input.1 * 100))
}

#[test]
fn test_input() {
    let input = "9 players; last marble is worth 25 points";
    assert_eq!(input_generator(input), (9, 25));
}

#[test]
fn test_part1() {
    assert_eq!(solve_part1(&(9, 25)), 32);
    assert_eq!(solve_part1(&(10, 1618)), 8317);
    assert_eq!(solve_part1(&(13, 7999)), 146373);
    assert_eq!(solve_part1(&(17, 1104)), 2764);
    assert_eq!(solve_part1(&(21, 6111)), 54718);
    assert_eq!(solve_part1(&(30, 5807)), 37305);
}
