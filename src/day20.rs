#[aoc_generator(day20)]
fn parse_input(input: &str) -> Vec<Tile> {
    input
        .split("\n\n")
        .map(|t| {
            let first_line = t.lines().next().unwrap();
            let id = first_line[5..first_line.find(':').unwrap()]
                .parse::<u32>()
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
            Tile { id, data }
        })
        .collect()
}

#[aoc(day20, part1)]
fn part1(input: &[Tile]) -> u128 {
    // find all tiles that have exactly two borders not matching any other tiles
    let corners: Vec<Tile> = input
        .iter()
        .filter(|t| {
            let cnt = t
                .borders()
                .iter()
                .filter(|b| {
                    for tile in input {
                        if tile.id == t.id {
                            continue;
                        }
                        if tile.borders().contains(b) {
                            return false;
                        }
                    }
                    true
                })
                .count();
            cnt >= 4
        })
        .cloned()
        .collect();
    println!("total corners: {} {:#?}", corners.len(), corners);
    corners.iter().map(|tile| tile.id as u128).product()
}

#[derive(Debug, Clone)]
pub struct Tile {
    id: u32,
    data: Vec<(u8, u8)>,
}

impl Tile {
    pub fn borders(&self) -> [u16; 8] {
        let base: u16 = 2;
        let top: u16 = self
            .data
            .iter()
            .filter(|c| c.1 == 0)
            .map(|c| base.pow(c.0 as u32))
            .sum();
        let bottom: u16 = self
            .data
            .iter()
            .filter(|c| c.1 == 9)
            .map(|c| base.pow(c.0 as u32))
            .sum();
        let left: u16 = self
            .data
            .iter()
            .filter(|c| c.0 == 0)
            .map(|c| base.pow(c.1 as u32))
            .sum();
        let right: u16 = self
            .data
            .iter()
            .filter(|c| c.0 == 9)
            .map(|c| base.pow(c.1 as u32))
            .sum();
        let top_2: u16 = self
            .data
            .iter()
            .filter(|c| c.1 == 0)
            .map(|c| base.pow(9 - c.0 as u32))
            .sum();
        let bottom_2: u16 = self
            .data
            .iter()
            .filter(|c| c.1 == 9)
            .map(|c| base.pow(9 - c.0 as u32))
            .sum();
        let left_2: u16 = self
            .data
            .iter()
            .filter(|c| c.0 == 0)
            .map(|c| base.pow(9 - c.1 as u32))
            .sum();
        let right_2: u16 = self
            .data
            .iter()
            .filter(|c| c.0 == 9)
            .map(|c| base.pow(9 - c.1 as u32))
            .sum();
        [top, top_2, bottom, bottom_2, left, left_2, right, right_2]
    }
}

#[test]
fn test_input_parsing() {
    let test_input = "Tile 2311:
..##.#..#.
##..#.....
#...##..#.
####.#...#
##.##.###.
##...#.###
.#.#.#..##
..#....#..
###...#.#.
..###..###

Tile 1951:
#.##...##.
#.####...#
.....#..##
#...######
.##.#....#
.###.#####
###.##.##.
.###....#.
..#.#..#.#
#...##.#..

Tile 1171:
####...##.
#..##.#..#
##.#..#.#.
.###.####.
..###.####
.##....##.
.#...####.
#.##.####.
####..#...
.....##...

Tile 1427:
###.##.#..
.#..#.##..
.#.##.#..#
#.#.#.##.#
....#...##
...##..##.
...#.#####
.#.####.#.
..#..###.#
..##.#..#.

Tile 1489:
##.#.#....
..##...#..
.##..##...
..#...#...
#####...#.
#..#.#.#.#
...#.#.#..
##.#...##.
..##.##.##
###.##.#..

Tile 2473:
#....####.
#..#.##...
#.##..#...
######.#.#
.#...#.#.#
.#########
.###.#..#.
########.#
##...##.#.
..###.#.#.

Tile 2971:
..#.#....#
#...###...
#.#.###...
##.##..#..
.#####..##
.#..####.#
#..#.#..#.
..####.###
..#.#.###.
...#.#.#.#

Tile 2729:
...#.#.#.#
####.#....
..#.#.....
....#..#.#
.##..##.#.
.#.####...
####.#.#..
##.####...
##..#.##..
#.##...##.

Tile 3079:
#.#.#####.
.#..######
..#.......
######....
####.#..#.
.#...#.##.
#.#####.##
..#.###...
..#.......
..#.###...";
    let tiles = parse_input(test_input);
    assert_eq!(9, tiles.len());
    assert_eq!(20899048083289, part1(&parse_input(test_input)));
}
