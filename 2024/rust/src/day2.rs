#[aoc_generator(day2)]
pub fn input_generator(input: &str) -> Vec<Vec<usize>> {
    input
        .lines()
        .map(|l| {
            l.split_whitespace()
                .filter_map(|num_str| num_str.parse::<usize>().ok())
                .collect()
        })
        .collect()
}

fn is_safe(report: &[usize]) -> bool {
    let within_bounds = |a: usize, b: usize| a.abs_diff(b) <= 3 && a != b;

    let is_increasing = report
        .windows(2)
        .all(|w| w[0] <= w[1] && within_bounds(w[0], w[1]));
    let is_decreasing = report
        .windows(2)
        .all(|w| w[0] >= w[1] && within_bounds(w[0], w[1]));
    is_increasing || is_decreasing
}

#[aoc(day2, part1)]
fn part1(input: &[Vec<usize>]) -> usize {
    input.iter().filter(|row| is_safe(row)).count()
}

fn lists_with_one_removed<T: Clone>(vec: &[T]) -> Vec<Vec<T>> {
    (0..vec.len())
        .map(|i| {
            vec.iter()
                .enumerate()
                .filter(|&(j, _)| j != i)
                .map(|(_, val)| val.clone())
                .collect()
        })
        .collect()
}

#[aoc(day2, part2)]
fn part2(input: &[Vec<usize>]) -> usize {
    input
        .iter()
        .filter(|row| lists_with_one_removed(row).iter().any(|r| is_safe(r)))
        .count()
}
