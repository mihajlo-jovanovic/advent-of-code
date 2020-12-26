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

// fn part2(input: &[u8]) -> (u32, u32) {
//     let x = (0..1000000).collect();
//     let mut c = CrabCups::new(&x, input[0]);
//     (1,2)
// }

#[derive(Debug)]
pub struct CrabCups {
    cups: VecDeque<u32>,
    current_cup: u32,
}

impl CrabCups {
    pub fn new(cups: &[u32], current_cup: u32) -> CrabCups {
        CrabCups {
            cups: VecDeque::from_iter(cups.iter().copied()),
            current_cup,
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
        {
            let mut dest = self.current_cup - 1;
            if dest == 0 {
                dest = 9;
            }
            while !self.cups.contains(&dest) {
                dest -= 1;
                if dest == 0 {
                    dest = 9;
                }
            }
            let mut i: usize = 1;
            for cup in pick_up.into_iter() {
                self.cups
                    .insert((self.destination_cup_idx(dest) + i) % self.cups.len(), cup);
                i += 1
            }
        }
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
}

#[test]
fn test_making_a_move() {
    let mut c = CrabCups::new(&[3, 8, 9, 1, 2, 5, 4, 6, 7], 3);
    c.make_a_move();
    println!("{:#?}", c);
    c.make_a_move();
    println!("{:#?}", c);
    c.make_a_move();
    println!("{:#?}", c);
}

#[test]
fn test_part1() {
    let mut c = CrabCups::new(&[3, 8, 9, 1, 2, 5, 4, 6, 7], 3);
    for _ in 0..10 {
        c.make_a_move()
    }
    println!("{:#?}", c);
    assert_eq!("67384529", part1(&[3, 8, 9, 1, 2, 5, 4, 6, 7]));
    assert_eq!("26354798", part1(&parse_input("284573961")));
}

#[test]
fn test_vec_functions() {
    let mut a = vec![3, 8, 9, 1, 2, 5, 4, 6, 7];
    let rem: Vec<_> = a.drain(1..4).collect();
    for (i, el) in rem.iter().enumerate() {
        a.insert(2 + i, *el);
    }
    assert_eq!(a, [3, 2, 8, 9, 1, 5, 4, 6, 7]);
}
