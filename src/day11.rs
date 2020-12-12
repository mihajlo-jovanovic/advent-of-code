use itertools::Itertools;
use std::collections::HashMap;

#[aoc_generator(day11)]
#[allow(unused_assignments)]
fn parse_seat_map(input: &str) -> HashMap<(i32, i32), bool> {
    let mut x = 0;
    let mut y = 0;
    let seat_map: Vec<(i32, i32)> = input
        .lines()
        .flat_map(move |l| {
            x = -1;
            let iter = l.chars().filter_map(move |c| {
                x += 1;
                if c == 'L' {
                    return Some((x, y));
                }
                None
            });
            y += 1;
            iter
        })
        .collect();
    let mut state: HashMap<(i32, i32), bool> = HashMap::new();
    for s in seat_map {
        state.insert(s, true);
    }
    state
}

#[aoc(day11, part1)]
fn part1(state: &HashMap<(i32, i32), bool>) -> usize {
    let mut prev_state = state.to_owned();
    loop {
        let mut state_changed = false;
        let mut new_state: HashMap<(i32, i32), bool> = prev_state.clone();
        for (pos, is_occupied) in &prev_state {
            let cnt = count_neighbors(*pos, &prev_state);
            if *is_occupied && cnt > 3 || !is_occupied && cnt == 0 {
                new_state.insert(*pos, !is_occupied);
                state_changed = true;
            }
        }
        if !state_changed {
            return prev_state.iter().filter(|s| *s.1).count();
        }
        prev_state = new_state;
    }
}

#[aoc(day11, part2)]
fn part2(state: &HashMap<(i32, i32), bool>) -> usize {
    let mut prev_state = state.to_owned();
    let max_x = state.keys().map(|(x,_)| x).max().unwrap();
    let max_y = state.keys().map(|(_,y)| y).max().unwrap();
    loop {
        let mut state_changed = false;
        let mut new_state: HashMap<(i32, i32), bool> = prev_state.clone();
        for (pos, is_occupied) in &prev_state {
            let cnt = [
                (0, 1),
                (1, 0),
                (-1, 0),
                (0, -1),
                (-1, 1),
                (1, -1),
                (-1, -1),
                (1, 1),
            ]
            .iter()
            .filter(|(x, y)| is_seat_occupied(*pos, &prev_state, *x, *y, *max_x, *max_y))
            .count();
            if *is_occupied && cnt > 4 || !is_occupied && cnt == 0 {
                new_state.insert(*pos, !is_occupied);
                state_changed = true;
            }
        }
        if !state_changed {
            return prev_state.iter().filter(|s| *s.1).count();
        }
        prev_state = new_state;
    }
}

fn count_neighbors(pos: (i32, i32), state: &HashMap<(i32, i32), bool>) -> usize {
    (pos.0 - 1..pos.0 + 2)
        .cartesian_product(pos.1 - 1..pos.1 + 2)
        .filter(move |n| match state.get(n) {
            Some(is_occupied) => pos != *n && *is_occupied,
            None => false,
        })
        .count()
}

fn is_seat_occupied(pos: (i32, i32), state: &HashMap<(i32, i32), bool>, x: i32, y: i32, max_x: i32, max_y: i32) -> bool {
    let mut cur: (i32, i32) = (pos.0 + x, pos.1 + y);
    loop {
        if cur.0 > max_x || cur.1 > max_y || cur.0 < 0 || cur.1 < 0 {
            return false;
        }
        match state.get(&cur) {
            Some(s) => return *s,
            _ => cur = (cur.0 + x, cur.1 + y),
        }
    }
}

#[test]
fn test_parsing() {
    let seat_map = parse_seat_map(
        "L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL",
    );
    assert_eq!(37, part1(&seat_map));
}

#[test]
fn test_count_neighbors() {
    let state = parse_seat_map(
        "L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL",
    );
    assert_eq!(6, count_neighbors((1, 1), &state));
    assert_eq!(2, count_neighbors((0, 0), &state));
    assert_eq!(3, count_neighbors((0, 1), &state));
}

#[test]
fn test_is_seat_taken() {
    let state = parse_seat_map(
        ".......L.
...L.....
.L.......
.........
..LL....L
....L....
.........
L........
...L.....",
    );
    let cnt = [
        (0, 1),
        (1, 0),
        (-1, 0),
        (0, -1),
        (-1, 1),
        (1, -1),
        (-1, -1),
        (1, 1),
    ]
    .iter()
    .filter(|(x, y)| is_seat_occupied((3, 4), &state, *x, *y, 8, 9))
    .count();
    assert_eq!(8, cnt);
    let state = parse_seat_map(
        ".............
.L.L.L.L.L.L.
.............",
    );
    let mut cloned = state.to_owned();
    cloned.insert((3, 1), false);
    let cnt = [(1, 0)]
        .iter()
        .filter(|(x, y)| is_seat_occupied((1, 1), &cloned, *x, *y, 11, 1))
        .count();
    assert_eq!(0, cnt);
}

#[test]
fn test_part2() {
    let state = parse_seat_map(
        "L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL",
    );
    assert_eq!(26, part2(&state));
}
