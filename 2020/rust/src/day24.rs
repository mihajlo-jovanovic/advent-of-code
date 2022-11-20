use itertools::Itertools;
use std::collections::HashMap;

#[aoc_generator(day24)]
fn parse_tiles(input: &str) -> Vec<(i32, i32)> {
    input.lines().map(|l| parse_single_tile(l)).collect()
}

#[aoc(day24, part1)]
fn part1(input: &[(i32, i32)]) -> usize {
    println!("{:#?}", input);
    let cnt = input.iter().unique().count();
    let x = input
        .iter()
        .unique()
        .filter(|t| input.iter().filter(|a| a == t).count() % 2 == 0)
        .count();
    println!("{} {}", cnt, x);
    cnt - x
}

#[aoc(day24, part2)]
fn part2(input: &[(i32, i32)]) -> usize {
    let mut state: HashMap<(i32, i32), bool> = HashMap::new();
    for tile in input.iter().unique() {
        let occurences = input.iter().filter(|a| a == &tile).count();
        if occurences % 2 == 0 {
            state.insert(*tile, false);
        } else {
            state.insert(*tile, true);
        }
        for n in neighbors(tile).iter() {
            state.entry(*n).or_insert(false);
        }
    }
    // add other tiles neighboring the ones we already have
    let mut new_state = advance_a_day(state);
    for _ in 0..99 {
        new_state = advance_a_day(new_state);
    }

    new_state.values().filter(|t| **t).count()
}

fn advance_a_day(state: HashMap<(i32, i32), bool>) -> HashMap<(i32, i32), bool> {
    let mut new_state: HashMap<(i32, i32), bool> = HashMap::new();
    for tile in state.keys() {
        let flipped = neighbors(tile)
            .iter()
            .filter(|n| match state.get(*n) {
                Some(color) => *color,
                None => false,
            })
            .count();
        if let Some(color) = state.get(tile) {
            if (*color && (flipped == 0 || flipped > 2)) || (!*color && flipped == 2) {
                new_state.insert(*tile, !color);
            } else {
                new_state.insert(*tile, *color);
            }
        }
        // again, need to add new tiles
        for n in neighbors(tile).iter() {
            new_state.entry(*n).or_insert(false);
        }
    }
    new_state
}

fn neighbors(tile: &(i32, i32)) -> [(i32, i32); 6] {
    [
        (tile.0 + 2, tile.1),
        (tile.0 - 2, tile.1),
        (tile.0 + 1, tile.1 + 1),
        (tile.0 - 1, tile.1 - 1),
        (tile.0 + 1, tile.1 - 1),
        (tile.0 - 1, tile.1 + 1),
    ]
}

fn parse_single_tile(input: &str) -> (i32, i32) {
    let mut ns: char = '0';
    input
        .chars()
        .flat_map(|c| match c {
            'e' if ns == 's' => {
                ns = '0';
                Some((1, -1))
            }
            'e' if ns == 'n' => {
                ns = '0';
                Some((1, 1))
            }
            'e' => Some((2, 0)),
            'w' if ns == 's' => {
                ns = '0';
                Some((-1, -1))
            }
            'w' if ns == 'n' => {
                ns = '0';
                Some((-1, 1))
            }
            'w' => Some((-2, 0)),
            'n' => {
                ns = c;
                None
            }
            's' => {
                ns = c;
                None
            }
            _ => panic!("Invalid char"),
        })
        .fold((0, 0), |(x, y), acc| (x + acc.0, y + acc.1))
}

#[test]
fn test_input_parsing() {
    assert_eq!((1, -1), parse_single_tile("esew"));
    assert_eq!((0, 0), parse_single_tile("nwwswee"));
    assert!(neighbors(&(0, 0)).contains(&(1, -1)));
    assert!(neighbors(&(0, 0)).contains(&(-2, 0)));
    assert!(neighbors(&(0, 0)).contains(&(2, 0)));
}

#[test]
fn test_solution() {
    let test_input = "sesenwnenenewseeswwswswwnenewsewsw
neeenesenwnwwswnenewnwwsewnenwseswesw
seswneswswsenwwnwse
nwnwneseeswswnenewneswwnewseswneseene
swweswneswnenwsewnwneneseenw
eesenwseswswnenwswnwnwsewwnwsene
sewnenenenesenwsewnenwwwse
wenwwweseeeweswwwnwwe
wsweesenenewnwwnwsenewsenwwsesesenwne
neeswseenwwswnwswswnw
nenwswwsewswnenenewsenwsenwnesesenew
enewnwewneswsewnwswenweswnenwsenwsw
sweneswneswneneenwnewenewwneswswnese
swwesenesewenwneswnwwneseswwne
enesenwswwswneneswsenwnewswseenwsese
wnwnesenesenenwwnenwsewesewsesesew
nenewswnwewswnenesenwnesewesw
eneswnwswnwsenenwnwnwwseeswneewsenese
neswnwewnwnwseenwseesewsenwsweewe
wseweeenwnesenwwwswnew";
    assert_eq!(10, part1(&parse_tiles(test_input)));
    assert_eq!(2208, part2(&parse_tiles(test_input)));
}
