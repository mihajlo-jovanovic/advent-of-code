use std::collections::HashSet;
use std::convert::TryInto;

#[aoc_generator(day3)]
#[allow(unused_assignments)]
fn input_generator(input: &str) -> Vec<(i32, i32)> {
    let mut x = 0;
    let mut y = 0;
    input
        .lines()
        .flat_map(move |l| {
            x = -1;
            let iter = l.chars().filter_map(move |c| {
                x += 1;
                if c == '#' {
                    return Some((x, y));
                }
                None
            });
            y += 1;
            iter
        })
        .collect()
}

#[aoc(day3, part1)]
fn part1(input: &[(i32, i32)]) -> usize {
    input.iter().filter(|(x, y)| *x == (y * 3) % 31).count()
}

#[aoc(day3, part1, alt_w_sets)]
fn part1_alt(input: &[(i32, i32)]) -> i32 {
    let map: HashSet<(i32, i32)> = input.iter().cloned().collect();
    cnt_trees(map, 3, 1, 31, 400)
}

fn slope(right: i32, down: i32, max_x: i32, max_y: i32) -> Vec<(i32, i32)> {
    let mut coords: Vec<(i32, i32)> = Vec::new();
    coords.push((0, 0));
    let mut x = 0;
    let mut y = 0;
    loop {
        x = (x + right) % max_x;
        y += down;
        if y > max_y {
            break;
        }
        coords.push((x, y));
    }
    coords
}

fn cnt_trees(map: HashSet<(i32, i32)>, x: i32, y: i32, max_x: i32, max_y: i32) -> i32 {
    let coords: HashSet<(i32, i32)> = slope(x, y, max_x, max_y).into_iter().collect();
    let output: Vec<(i32, i32)> = map.intersection(&coords).cloned().collect();
    output.len().try_into().unwrap()
}

#[aoc(day3, part2)]
fn part2(input: &[(i32, i32)]) -> i32 {
    let slopes = [(3, 1), (1, 1), (5, 1), (7, 1), (1, 2)];
    slopes
        .iter()
        .map(|p| cnt_trees(input.iter().cloned().collect(), p.0, p.1, 31, 400))
        .product()
}

#[test]
fn test_match() {
    let s = slope(1, 2, 31, 100);
    println!("Slope coords: {:?}", s);
    assert_eq!(true, s.contains(&(0, 0)));
    assert_eq!(true, s.contains(&(7, 14)));
    assert_eq!(true, s.contains(&(10, 20)));
    assert_eq!(true, s.contains(&(19, 38)));
    assert_eq!(true, s.contains(&(0, 62)));
}

#[test]
fn test_cnt_trees() {
    assert_eq!(0, cnt_trees(HashSet::new(), 31, 400, 1, 2));
}
