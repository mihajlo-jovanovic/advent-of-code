use std::collections::HashMap;

#[aoc_generator(day15)]
fn parse_input(input: &str) -> Vec<u32> {
    input
        .lines()
        .next()
        .unwrap()
        .split(',')
        .map(|c| c.parse::<u32>().unwrap())
        .collect()
}

#[aoc(day15, part1)]
fn part1(input: &[u32]) -> u32 {
    nth_num_spoken(input, 2020)
}

#[aoc(day15, part2)]
fn part2(input: &[u32]) -> u32 {
    nth_num_spoken(input, 30000000)
}

fn nth_num_spoken(input: &[u32], n: usize) -> u32 {
    memory_game(input).nth(n - input.len() - 1).unwrap()
}

#[derive(Debug)]
struct MemoryGame {
    curr: u32,
    turn: u32,
    state: HashMap<u32, (u32, u32)>,
}

impl Iterator for MemoryGame {
    type Item = u32;

    fn next(&mut self) -> Option<u32> {
        let cache_entry = self.state.get(&self.curr).unwrap();
        self.curr = cache_entry.1;
        self.turn += 1;
        let age = self.turn - self.state.entry(self.curr).or_insert((self.turn, 0)).0;
        self.state.insert(self.curr, (self.turn, age));
        Some(self.curr)
    }
}

fn memory_game(seed: &[u32]) -> MemoryGame {
    let mut state: HashMap<u32, (u32, u32)> = HashMap::new();
    for (i, num) in seed.iter().enumerate() {
        state.insert(*num, ((i + 1) as u32, 0));
    }
    MemoryGame {
        curr: seed[seed.len() - 1],
        turn: state.len() as u32,
        state,
    }
}

#[test]
fn test_seq() {
    assert_eq!(1, nth_num_spoken(&[1, 3, 2], 2020));
    assert_eq!(10, nth_num_spoken(&[2, 1, 3], 2020));
    assert_eq!(27, nth_num_spoken(&[1, 2, 3], 2020));
    assert_eq!(78, nth_num_spoken(&[2, 3, 1], 2020));
    assert_eq!(438, nth_num_spoken(&[3, 2, 1], 2020));
    assert_eq!(1836, nth_num_spoken(&[3, 1, 2], 2020));
    //assert_eq!(175594, nth_num_spoken(&[0, 3, 6], 30000000));
}
