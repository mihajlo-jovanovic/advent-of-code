use std::collections::HashSet;

#[aoc_generator(day6, part1)]
fn generator_input(input: &str) -> Vec<usize> {
    input
        .split("\n\n")
        .map(|l| {
            let tmp = l.replace('\n', "");
            let mut distinct: HashSet<char> = HashSet::new();
            for c in tmp.chars() {
                distinct.insert(c);
            }
            distinct.len()
        })
        .collect()
}

#[aoc_generator(day6, part2)]
fn generator_input_part2(input: &str) -> Vec<usize> {
    input
        .split("\n\n")
        .map(|l| {
            let tmp: Vec<HashSet<char>> = l
                .split('\n')
                .map(|g| g.chars().collect::<HashSet<char>>())
                .collect();
            let common_answers: HashSet<char> = tmp[0]
                .iter()
                .filter(|b| tmp[1..].iter().all(|set| set.contains(*b)))
                .copied()
                .collect();
            common_answers.len()
        })
        .collect()
}

#[aoc(day6, part1)]
fn part1(input: &[usize]) -> usize {
    input.iter().sum()
}

#[aoc(day6, part2)]
fn part2(input: &[usize]) -> usize {
    input.iter().sum()
}
