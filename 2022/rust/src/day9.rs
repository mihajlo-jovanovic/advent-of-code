use std::collections::HashSet;

#[derive(Debug, Clone, Hash, PartialEq, Eq)]
struct Position {
    x: i32,
    y: i32,
}

#[derive(Debug)]
struct Rope {
    head: Position,
    tail: Position,
}

impl Rope {
    fn move_right(&mut self) -> Option<Position> {
        self.head.x += 1;
        if !&self.touching() {
            self.tail.x += 1;
            self.tail.y = self.head.y;
            return Some(self.tail.clone());
        }
        None
    }
    fn move_n_times(&mut self, d: char, c: u8) -> Vec<Position> {
        match d {
            'R' => (0..c).flat_map(|_| self.move_right()).collect(),
            'L' => (0..c).flat_map(|_| self.move_left()).collect(),
            'U' => (0..c).flat_map(|_| self.move_up()).collect(),
            'D' => (0..c).flat_map(|_| self.move_down()).collect(),
            _ => panic!("invalid instruction {}", d),
        }
    }
    fn move_left(&mut self) -> Option<Position> {
        self.head.x -= 1;
        if !&self.touching() {
            self.tail.x -= 1;
            self.tail.y = self.head.y;
            return Some(self.tail.clone());
        }
        None
    }
    fn move_up(&mut self) -> Option<Position> {
        self.head.y += 1;
        if !&self.touching() {
            self.tail.y += 1;
            self.tail.x = self.head.x;
            return Some(self.tail.clone());
        }
        None
    }
    fn move_down(&mut self) -> Option<Position> {
        self.head.y -= 1;
        if !&self.touching() {
            self.tail.y -= 1;
            self.tail.x = self.head.x;
            return Some(self.tail.clone());
        }
        None
    }

    fn touching(&self) -> bool {
        (self.head.x - self.tail.x).abs() <= 1 && (self.head.y - self.tail.y).abs() <= 1
    }
}

#[aoc_generator(day9)]
fn generator_input(input: &str) -> Vec<(char, u8)> {
    input
        .lines()
        .map(|line| {
            let mut tokens = line.split_whitespace();
            (
                tokens.next().unwrap().chars().next().unwrap(),
                tokens.next().unwrap().parse::<u8>().unwrap(),
            )
        })
        .collect()
}

#[aoc(day9, part1)]
fn part1(motions: &[(char, u8)]) -> usize {
    let mut s: Rope = Rope {
        head: Position { x: 0, y: 0 },
        tail: Position { x: 0, y: 0 },
    };
    let mut positions: HashSet<Position> = motions
        .iter()
        .flat_map(|(d, i)| s.move_n_times(*d, *i))
        .collect();
    positions.insert(Position { x: 0, y: 0 });
    positions.len()
}

#[allow(dead_code)]
fn print(state: &HashSet<Position>) {
    for y in (0..5).rev() {
        for x in 0..5 {
            if state.contains(&Position { x, y }) {
                print!("#");
            } else {
                print!(" ");
            }
        }
        println!();
    }
}

#[test]
fn test_moving() {
    let mut r: Rope = Rope {
        head: Position { x: 0, y: 0 },
        tail: Position { x: 0, y: 0 },
    };
    assert_eq!(
        Rope {
            head: Position { x: 1, y: 0 },
            tail: Position { x: 0, y: 0 },
        }
        .touching(),
        true
    );
    r.move_right();
    assert_eq!(r.tail, Position { x: 0, y: 0 });
    r.move_right();
    assert_eq!(r.tail, Position { x: 1, y: 0 });
    assert_eq!(
        Rope {
            head: Position { x: 4, y: -1 },
            tail: Position { x: 4, y: 0 },
        }
        .touching(),
        true
    );
    assert_eq!(
        Rope {
            head: Position { x: 2, y: 0 },
            tail: Position { x: 1, y: -2 },
        }
        .touching(),
        false
    );
}

#[test]
fn test_parsing() {
    let input = "R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2";
    let parsed = generator_input(input);
    assert_eq!(parsed.len(), 8);
    assert_eq!(parsed[0].0, 'R');
    assert_eq!(part1(&parsed), 13);
}
