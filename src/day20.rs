use std::collections::HashSet;
use std::fmt;

#[aoc_generator(day20)]
fn parse_input(input: &str) -> Vec<Tile> {
    input
        .split("\n\n")
        .map(|t| {
            let first_line = t.lines().next().unwrap();
            let id = first_line[5..first_line.find(':').unwrap()]
                .parse::<usize>()
                .unwrap();
            let mut x: i32 = 0;
            let mut y: u8 = 0;
            let data = t
                .lines()
                .skip(1)
                .flat_map(|l| {
                    x = -1;
                    let iter = l.chars().filter_map(move |c| {
                        x += 1;
                        if c == '#' {
                            return Some((x as u8, y));
                        }
                        None
                    });
                    y += 1;
                    iter
                })
                .collect();
            Tile {
                id,
                data,
                size: t.lines().nth(1).unwrap().chars().count() as u8,
            }
        })
        .collect()
}

#[aoc(day20, part1)]
fn part1(input: &[Tile]) -> u64 {
    input
        .iter()
        .filter(|t| {
            let count_of_neighbors = input
                .iter()
                .filter(|t2| {
                    t.id != t2.id
                        && (t.is_adjacent(t2.get_border(&Border::TOP))
                            || t.is_adjacent(t2.get_border(&Border::BOTTOM))
                            || t.is_adjacent(t2.get_border(&Border::LEFT))
                            || t.is_adjacent(t2.get_border(&Border::RIGHT)))
                })
                .count();
            count_of_neighbors == 2
        })
        .map(|t| t.id as u64)
        .product()
}

fn borders_match(first: &[u8], second: &[u8]) -> bool {
    let border1_set: HashSet<_> = first.iter().cloned().collect();
    let border2_set: HashSet<_> = second.iter().cloned().collect();
    border1_set == border2_set
}

#[derive(Debug, Clone)]
pub struct Tile {
    id: usize,
    data: Vec<(u8, u8)>,
    size: u8,
}
enum Border {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
}

// type Result<T> = std::result::Result<T, TileNotAdjacentError>;
#[derive(Debug, Clone)]
struct TileNotAdjacentError;

impl Tile {
    fn get_border(&self, border: &Border) -> Vec<u8> {
        match border {
            Border::TOP => self
                .data
                .iter()
                .filter(|(_, y)| *y == 0)
                .map(|(x, _)| *x)
                .collect(),
            Border::BOTTOM => self
                .data
                .iter()
                .filter(|(_, y)| *y == self.size - 1 as u8)
                .map(|(x, _)| *x)
                .collect(),
            Border::LEFT => self
                .data
                .iter()
                .filter(|(x, _)| *x == 0)
                .map(|(_, y)| *y)
                .collect(),
            Border::RIGHT => self
                .data
                .iter()
                .filter(|(x, _)| *x == self.size - 1)
                .map(|(_, y)| *y)
                .collect()
        }
    }

    /// Mutates tile (by rotating, flipping) to match given border and orientation
    fn line_up(&mut self, border: Vec<u8>, side: Border) -> Result<(), TileNotAdjacentError> {
        for _ in 0..4 {
            if borders_match(&self.get_border(&side), &border) {
                return Ok(());
            }
            self.rotate();
        }
        println!("Flipping horizontally...");
        self.flip();
        for _ in 0..4 {
            if borders_match(&self.get_border(&side), &border) {
                return Ok(());
            }
            self.rotate();
        }
        Err(TileNotAdjacentError)
    }

    /// Returns true is tile matches given border at any side or orientation, false otherwise
    fn is_adjacent(&self, border: Vec<u8>) -> bool {
        for side in [Border::LEFT, Border::RIGHT, Border::TOP, Border::BOTTOM].iter() {
            let b = self.get_border(side);
            if borders_match(&b, &border) {
                return true;
            } else {
                //try reversing
                let reversed: Vec<u8> = b.iter().map(|i| self.size - 1 - *i).collect();
                if borders_match(&reversed, &border) {
                    return true;
                }
            }
        }
        false
    }

    fn flip(&mut self) {
        let mut new_state: Vec<(u8, u8)> = Vec::new();
        for pixels in &self.data {
            new_state.push((pixels.0, 9 - pixels.1));
        }
        self.data = new_state;
    }

    fn rotate(&mut self) {
        let mut new_state: Vec<(u8, u8)> = Vec::new();
        for pixels in &self.data {
            new_state.push((9 - pixels.1, pixels.0));
        }
        self.data = new_state;
    }
}

impl fmt::Display for Tile {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let mut pretty: String = String::new();
        for y in 0..self.size {
            for x in 0..self.size {
                if self.data.contains(&(x, y)) {
                    pretty.push_str("#");
                } else {
                    pretty.push_str(".");
                }
            }
            pretty.push_str("\n");
        }
        write!(f, "{}", pretty)
    }
}

#[cfg(test)]
mod tests {
    use super::{parse_input, part1, Border};
    use std::fs;
    #[test]
    fn test_input_parsing() {
        let input =
            fs::read_to_string("input/2020/day20_sample_input.txt").expect("Could not read file");
        let tiles = parse_input(input.as_str());
        assert_eq!(9, tiles.len());
        assert_eq!(10, tiles[0].size);
        assert_eq!(20899048083289, part1(&tiles));
    }

    #[test]
    fn test_rearranging_tiles() {
        let input =
            fs::read_to_string("input/2020/day20_sample_input.txt").expect("Could not read file");
        let tiles = parse_input(input.as_str());
        let id1 = 1951;
        let id2 = 2311;
        let mut t1 = tiles
            .iter()
            .find(|t| t.id == id1)
            .expect("No matching tiles found")
            .clone();
        let mut t2 = tiles
            .iter()
            .find(|t| t.id == id2)
            .expect("No matching tiles found")
            .clone();
        assert!(t2.is_adjacent(t1.get_border(&Border::RIGHT)));
        t1.flip();
        // should still work
        assert!(t2.is_adjacent(t1.get_border(&Border::RIGHT)));
        t2.flip();
        assert!(t2.is_adjacent(t1.get_border(&Border::RIGHT)));
        // now let's try lining up
        t2.rotate();
        println!("BEFORE:");
        println!("{}", t1);
        println!("{}", t2);
        t2.line_up(t1.get_border(&Border::RIGHT), Border::LEFT);
        println!("AFTER:");
        println!("{}", t1);
        println!("{}", t2);
        assert_eq!(t1.get_border(&Border::RIGHT), t2.get_border(&Border::LEFT));

        let id3 = 2729;
        let mut t3 = tiles
            .iter()
            .find(|t| t.id == id3)
            .expect("No matching tiles found")
            .clone();
        println!("{}", t3);
        assert!(t3.is_adjacent(t1.get_border(&Border::BOTTOM)));
        t3.line_up(t1.get_border(&Border::BOTTOM), Border::TOP);
        println!("{}", t3);

        let id4 = 3079;
        let mut t4 = tiles
            .iter()
            .find(|t| t.id == id4)
            .expect("No matching tiles found")
            .clone();
        println!("3079: {}", t4);
        println!("{}", t2);
        assert!(t4.is_adjacent(t2.get_border(&Border::RIGHT)));
        let result = t4.line_up(t2.get_border(&Border::RIGHT), Border::LEFT);
        match result {
            Ok(_) => assert!(true),
            Err(_) => assert!(false),
        }
        println!("{}", t4);
    }
}
