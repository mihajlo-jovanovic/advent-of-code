use core::fmt;
use std::collections::HashMap;
use std::fmt::Formatter;

use array2d::Array2D;

const BOARD_SIZE: usize = 1000;

#[derive(Debug, Clone)]
struct Claims {
    fabric: Array2D<usize>,
}

type Coords = (usize, usize);

impl Claims {
    fn new(rows: Vec<(usize, Coords, Coords)>) -> Claims {
        let mut fabric =
            Array2D::from_row_major(&[0; BOARD_SIZE * BOARD_SIZE], BOARD_SIZE, BOARD_SIZE);
        for (id, (x1, y1), (x2, y2)) in rows.iter() {
            for x in *x1..(*x1 + *x2) {
                for y in *y1..(*y1 + *y2) {
                    if fabric[(x, y)] == 0 {
                        fabric[(x, y)] = *id
                    } else {
                        //using max usize as sentinel, for overlap
                        fabric[(x, y)] = usize::MAX
                    }
                }
            }
        }
        Claims { fabric }
    }

    fn count(&self, el: usize) -> usize {
        self.fabric
            .elements_row_major_iter()
            .filter(|&x| *x == el)
            .count()
    }
}

//useful for debugging, with smaller board size (i.e. 10)
impl fmt::Display for Claims {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        let mut pretty: String = String::new();
        for y in 0..BOARD_SIZE {
            for x in 0..BOARD_SIZE {
                let y = match self.fabric[(x, y)] {
                    0 => '.',
                    usize::MAX => 'X',
                    _ => char::from_digit(self.fabric[(x, y)] as u32, 10).unwrap(),
                };
                pretty.push(y);
            }
            pretty.push('\n');
        }
        write!(f, "{}", pretty)
    }
}

#[aoc_generator(day3)]
fn generator_input(input: &str) -> (Claims, HashMap<usize, usize>) {
    let rows: Vec<(usize, Coords, Coords)> = input
        .lines()
        .map(move |s| {
            let mut tokens = s.split(' ');
            let id: usize = tokens.next().unwrap()[1..].parse().unwrap();
            let mut dim = tokens.nth(1).unwrap().split(',');
            let x: usize = dim.next().unwrap().parse().unwrap();
            let tmp = dim.next().unwrap();
            let y: usize = tmp[0..tmp.len() - 1].parse().unwrap();
            let mut sz = tokens.next().unwrap().split('x');
            let x2: usize = sz.next().unwrap().parse().unwrap();
            let y2: usize = sz.next().unwrap().parse().unwrap();
            (id, (x, y), (x2, y2))
        })
        .collect();
    let id2sz: HashMap<usize, usize> = rows.iter().map(|(id, _, (x, y))| (*id, x * y)).collect();
    (Claims::new(rows), id2sz)
}

#[aoc(day3, part1)]
fn part1(claims: &(Claims, HashMap<usize, usize>)) -> usize {
    claims.0.count(usize::MAX)
}

#[aoc(day3, part2)]
fn part2(claims: &(Claims, HashMap<usize, usize>)) -> usize {
    claims
        .1
        .iter()
        .find_map(|(&id, &sz)| {
            if claims.0.count(id) == sz {
                Some(id)
            } else {
                None
            }
        })
        .unwrap()
}

#[test]
fn test_parsing() {
    let input = "#1 @ 1,3: 4x4
#2 @ 3,1: 4x4
#3 @ 5,5: 2x2";
    let parsed = generator_input(&input);
    let claims = parsed.0;
    assert_eq!(0, claims.fabric[(0, 0)]);
    assert_eq!(1, claims.fabric[(1, 5)]);
    assert_eq!(2, claims.fabric[(5, 1)]);
    assert_eq!(usize::MAX, claims.fabric[(3, 3)]);
    assert_eq!(12, claims.count(1));
    assert_eq!(12, claims.count(2));
    assert_eq!(4, claims.count(3));
}

#[test]
fn test_p1() {
    let input = "#1 @ 1,3: 4x4
#2 @ 3,1: 4x4
#3 @ 5,5: 2x2";
    let claims = generator_input(&input);
    assert_eq!(4, part1(&claims));
}

#[test]
fn test_p2() {
    let input = "#1 @ 1,3: 4x4
#2 @ 3,1: 4x4
#3 @ 5,5: 2x2";
    let claims = generator_input(&input);
    assert_eq!(3, part2(&claims));
}
