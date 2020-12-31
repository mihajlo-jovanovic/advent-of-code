use num::integer::Roots;
use std::collections::HashSet;
use std::convert::TryInto;
use std::fmt;
use itertools::any;

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
fn part1(tiles: &[Tile]) -> u64 {
    println!("total tiles: {}", tiles.len());
    corners(tiles).iter().map(|t| t.id as u64).product()
}

fn corners(tiles: &[Tile]) -> [&Tile; 4] {
    let mut corners = tiles.iter().filter(|t| {
        let count_of_neighbors = tiles
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
    });
    //todo: clean this up
    [
        corners.next().unwrap(),
        corners.next().unwrap(),
        corners.next().unwrap(),
        corners.next().unwrap(),
    ]
}

fn borders_match(first: &[u8], second: &[u8]) -> bool {
    let border1_set: HashSet<_> = first.iter().cloned().collect();
    let border2_set: HashSet<_> = second.iter().cloned().collect();
    border1_set == border2_set
}

fn assemble_image(tiles: &[Tile]) -> Vec<(u8, u8)> {
    let mut img = Vec::new();
    let mut top: Tile = corners(tiles)[0].clone();
    let mut left: Tile = corners(tiles)[0].clone();
    let row_size = tiles.len().sqrt();
    for i in 0..tiles.len() {
        if i == 0 {
            //let mut corner = corners(tiles)[0].clone();
            let mut neighbor = tiles
                .iter()
                .find(|tile| tile.id != top.id && tile.is_adjacent(top.get_border(&Border::LEFT)));
            while neighbor.is_some() {
                top.rotate();
                neighbor = tiles
                    .iter()
                    .find(|tile| {
                        tile.id != top.id && tile.is_adjacent(top.get_border(&Border::LEFT))
                    });
            }
            if any(tiles, |tile| tile.id != top.id && tile.is_adjacent(top.get_border(&Border::TOP)))
            {
                top.flip();
            }
            //let &mut row = img.get(0).unwrap();
            //top = corner;
            print!("{}", top.id);
            for (x, y) in top
                .data
                .iter()
                .filter(|(x, y)| !(*x == 0 || *x == top.size - 1 || *y == 0 || *y == top.size - 1))
            {
                img.push((*x - 1, *y - 1));
            }

            left = top.clone();
        } else if i % row_size == 0 {
            println!();
            if let Some(jigsaw_piece) = tiles
                .iter()
                .find(|tile| tile.id != top.id && tile.is_adjacent(top.get_border(&Border::BOTTOM)))
            {
                let mut piece = jigsaw_piece.clone();
                match piece.line_up(top.get_border(&Border::BOTTOM), Border::TOP) {
                    Ok(_) => { },
                    _ => panic!("Could not line up tile along top border")
                }
                print!("{}", piece.id);
                let row: u8 = (i / row_size).try_into().unwrap();
                let offset = (top.size - 2) * row;
                for (x, y) in piece.data.iter().filter(|(x, y)| {
                    !(*x == 0 || *x == top.size - 1 || *y == 0 || *y == top.size - 1)
                }) {
                    img.push((*x - 1, *y - 1 + offset));
                }
                top = piece;
                left = top.clone();
            } else {
                panic!("No adjacent tile found");
            }
        } else {
            //let neighbor_to_the_left = row.get(i-1).unwrap();
            if let Some(jigsaw_piece) = tiles.iter().find(|tile| {
                tile.id != left.id && tile.is_adjacent(left.get_border(&Border::RIGHT))
            }) {
                let mut piece = jigsaw_piece.clone();
                match piece.line_up(left.get_border(&Border::RIGHT), Border::LEFT) {
                    Ok(_) => {},
                    _ => panic!("Could not line up tile along left border")
                }
                print!("{}", piece.id);
                if piece.id == 2311 {
                    println!("{}", piece);
                }
                let row: u8 = (i / row_size).try_into().unwrap();
                let offset = (top.size - 2) * row;
                let col: u8 = (i % row_size).try_into().unwrap();
                let offset_x = (top.size - 2) * col;
                println!(
                    "{} row: {}  offset: {}  col: {}  offset_x: {}",
                    piece.id, row, offset, col, offset_x
                );
                for (x, y) in piece.data.iter().filter(|(x, y)| {
                    !(*x == 0 || *x == top.size - 1 || *y == 0 || *y == top.size - 1)
                }) {
                    img.push((*x - 1 + offset_x, *y - 1 + offset));
                }
                left = piece;
            } else {
                panic!("No adjacent tile found");
            }
        }
    }
    img
}

const SEA_MONSTER: [(i8, i8); 15] = [
    (0, 0),
    (5, 0),
    (6, 0),
    (11, 0),
    (12, 0),
    (17, 0),
    (18, 0),
    (19, 0),
    (18, -1),
    (1, 1),
    (4, 1),
    (7, 1),
    (10, 1),
    (13, 1),
    (16, 1),
];

fn is_part_of_sea_monster(img: &[(u8, u8)], point: (u8, u8)) -> bool {
    for (x, y) in SEA_MONSTER.iter() {
        let pos_x: i8 = point.0 as i8 + x;
        let pos_y: i8 = point.1 as i8 + y;
        if pos_x >= 0 && pos_y >= 0 && !img.contains(&(pos_x as u8, pos_y as u8)) {
            return false;
        }
    }
    true
}

#[aoc(day20, part2)]
fn find_sea_monsters(tiles: &[Tile]) -> usize {
    let img: Vec<(u8, u8)> = assemble_image(tiles);
    let size = (tiles.get(0).unwrap().size - 2) * tiles.len().sqrt() as u8;
    let mut whole_pic = Tile {
        id: 1,
        data: img.clone(),
        size,
    };
    whole_pic.flip();
    whole_pic.rotate();
    let mut sea_monster: HashSet<(u8, u8)> = HashSet::new();
    for (x, y) in &whole_pic.data {
        if is_part_of_sea_monster(&whole_pic.data, (*x, *y)) {
            println!("Found one! (at ({},{})", x, y);
            for (x1, y1) in SEA_MONSTER.iter() {
                let pos_x: i8 = *x as i8 + *x1;
                let pos_y: i8 = *y as i8 + *y1;
                sea_monster.insert((pos_x as u8, pos_y as u8));
            }
        }
    }
    img.len() - sea_monster.len()
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
                .collect(),
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
        //println!("Flipping horizontally...");
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
            new_state.push((pixels.0, self.size - 1 - pixels.1));
        }
        self.data = new_state;
    }

    fn rotate(&mut self) {
        let mut new_state: Vec<(u8, u8)> = Vec::new();
        for pixels in &self.data {
            new_state.push((self.size - 1 - pixels.1, pixels.0));
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
                    pretty.push('#');
                } else {
                    pretty.push('.');
                }
            }
            pretty.push('\n');
        }
        write!(f, "{}", pretty)
    }
}

#[cfg(test)]
mod tests {
    use std::fs;

    use num::integer::Roots;

    use super::{
        assemble_image, find_sea_monsters, is_part_of_sea_monster, parse_input, part1, Border, Tile,
    };

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
        match t2.line_up(t1.get_border(&Border::RIGHT), Border::LEFT) {
            Ok(_) => assert!(true),
            _ => assert!(false),
        }
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
        match t3.line_up(t1.get_border(&Border::BOTTOM), Border::TOP) {
            Ok(_) => assert!(true),
            _ => assert!(false),
        }
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

    #[test]
    fn assembling_img() {
        let input =
            fs::read_to_string("input/2020/day20_sample_input.txt").expect("Could not read file");
        let tiles = parse_input(input.as_str());
        let img: Vec<(u8, u8)> = assemble_image(&tiles);
        let size = (tiles.get(0).unwrap().size - 2) * tiles.len().sqrt() as u8;
        let mut whole_pic = Tile {
            id: 1,
            data: img.clone(),
            size,
        };
        println!("{}", whole_pic);
        assert!(img.contains(&(8, 0)));
        assert!(img.contains(&(9, 0)));
        whole_pic.flip();
        whole_pic.rotate();
        println!("{}", whole_pic);
        assert!(is_part_of_sea_monster(&whole_pic.data, (2, 3)));
        assert!(!is_part_of_sea_monster(&whole_pic.data, (1, 0)));
        assert!(is_part_of_sea_monster(&whole_pic.data, (1, 17)));
    }

    #[test]
    fn finding_sea_monsters() {
        let input =
            fs::read_to_string("input/2020/day20_sample_input.txt").expect("Could not read file");
        let tiles = parse_input(input.as_str());
        assert_eq!(273, find_sea_monsters(&tiles));
    }
}
