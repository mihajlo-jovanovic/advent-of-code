use std::collections::VecDeque;
use std::iter::FromIterator;

#[aoc_generator(day23)]
fn parse_input(input: &str) -> Vec<u32> {
    input.chars().map(|c| c.to_digit(10).unwrap()).collect()
}

#[aoc(day23, part1)]
fn part1(input: &[u32]) -> String {
    let mut c = CrabCups::new(input, input[0]);
    for _ in 0..100 {
        c.make_a_move()
    }
    c.get_labels()
}

#[aoc(day23, part2)]
fn part2(input: &[u32]) -> u64 {
    let mut a = input.to_vec();
    a.extend(10..1000001);
    println!("total cups: {:?}", a.len());
    let mut c = CrabCups::new(&a, input[0]);
    for i in 0..10_000_000 {
        if i % 10_000 == 0 {
            println!("move #{:?}", i);
        }
        c.make_a_move()
    }
    c.get_labels_part2()
}

#[derive(Debug)]
pub struct CrabCups {
    cups: VecDeque<u32>,
    current_cup: u32,
    max_lbl: u32,
}

impl CrabCups {
    pub fn new(cups: &[u32], current_cup: u32) -> CrabCups {
        CrabCups {
            cups: VecDeque::from_iter(cups.iter().copied()),
            current_cup,
            max_lbl: *cups.iter().max().unwrap(),
        }
    }

    fn current_cup_idx(&self) -> usize {
        self.cups
            .iter()
            .position(|el| *el == self.current_cup)
            .expect("Cup not found")
    }

    fn destination_cup_idx(&self, destination: u32) -> usize {
        self.cups
            .iter()
            .position(|el| *el == destination)
            .expect("Destination cup not found")
    }

    pub fn make_a_move(&mut self) {
        let mut pick_up: VecDeque<u32> = VecDeque::new();
        if self.current_cup_idx() + 4 > self.cups.len() {
            for _ in 0..3 {
                let idx = (self.current_cup_idx() + 1) % self.cups.len();
                pick_up.push_back(self.cups.remove(idx).expect("Cup not found"));
            }
        } else {
            pick_up = self
                .cups
                .drain(self.current_cup_idx() + 1..self.current_cup_idx() + 4)
                .collect::<VecDeque<_>>();
        }
        // figure out destination label
        let mut dest = self.current_cup - 1;
        if dest == 0 {
            dest = self.max_lbl;
        }
        while pick_up.contains(&dest) {
            dest -= 1;
            if dest == 0 {
                dest = self.max_lbl;
            }
        }
        // insert to the right of destination
        for (i, cup) in pick_up.into_iter().enumerate() {
            self.cups.insert((self.destination_cup_idx(dest)+i+1) % self.cups.len(), cup);
        }
        // finally, set new current cup
        self.current_cup = self.cups[(self.current_cup_idx() + 1) % self.cups.len()];
    }

    fn get_labels(&mut self) -> String {
        let pos = self
            .cups
            .iter()
            .position(|c| *c == 1)
            .expect("Cup with label of 1 not found");
        if pos < self.cups.len() / 2 {
            self.cups.rotate_left(pos);
        } else {
            self.cups.rotate_right(self.cups.len() - pos);
        }

        self.cups
            .iter()
            .skip(1)
            .map(|c| c.to_string())
            .collect::<String>()
    }

    fn get_labels_part2(&self) -> u64 {
        let pos = self
        .cups
        .iter()
        .position(|c| *c == 1)
        .expect("Cup with label of 1 not found");
        (self.cups[(pos+1)%self.cups.len()] * self.cups[(pos+2)%self.cups.len()]) as u64
    }
}

#[test]
fn test_making_a_move() {
    let mut c = CrabCups::new(&[3, 8, 9, 1, 2, 5, 4, 6, 7], 3);
    c.make_a_move();
    assert_eq!("54673289", c.get_labels());
    c.make_a_move();
    assert_eq!("32546789", c.get_labels());
    c.make_a_move();
    assert_eq!("34672589", c.get_labels());
}

#[test]
fn test_part1() {
    let mut c = CrabCups::new(&[3, 8, 9, 1, 2, 5, 4, 6, 7], 3);
    for _ in 0..10 {
        c.make_a_move()
    }
    assert_eq!("67384529", part1(&[3, 8, 9, 1, 2, 5, 4, 6, 7]));
    assert_eq!("26354798", part1(&parse_input("284573961")));
}
