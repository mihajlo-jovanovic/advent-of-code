use std::collections::HashSet;

#[allow(unused_assignments)]
#[aoc_generator(day17, part1)]
fn parse_input(input: &str) -> Vec<(i32, i32, i32)> {
    let mut x = 0;
    let mut y = 0;
    let z = 0;
    input
        .lines()
        .flat_map(move |l| {
            x = -1;
            let iter = l.chars().filter_map(move |c| {
                x += 1;
                if c == '#' {
                    return Some((x, y, z));
                }
                None
            });
            y += 1;
            iter
        })
        .collect()
}

#[allow(unused_assignments)]
#[aoc_generator(day17, part2)]
fn parse_input_4d(input: &str) -> Vec<(i32, i32, i32, i32)> {
    let mut x = 0;
    let mut y = 0;
    let z = 0;
    let w = 0;
    input
        .lines()
        .flat_map(move |l| {
            x = -1;
            let iter = l.chars().filter_map(move |c| {
                x += 1;
                if c == '#' {
                    return Some((x, y, z, w));
                }
                None
            });
            y += 1;
            iter
        })
        .collect()
}

#[aoc(day17, part1)]
fn part1(active_cell_coords: &[(i32, i32, i32)]) -> usize {
    let mut new_active = step(&active_cell_coords);
    for _ in 0..5 {
        new_active = step(&new_active);
    }
    new_active.len()
}
// To-do: someday when I learn Rust a little better I will go back and clean this up to
// use generics and avoid copy/paste...but not today.
#[aoc(day17, part2)]
fn part2(active_cell_coords: &[(i32, i32, i32, i32)]) -> usize {
    let mut new_active = step_4d(&active_cell_coords);
    for _ in 0..5 {
        new_active = step_4d(&new_active);
    }
    new_active.len()
}

fn step(active: &[(i32, i32, i32)]) -> Vec<(i32, i32, i32)> {
    //let new_active: Vec<(i32,i32,i32)> = Vec::new();
    let mut new_active: Vec<(i32, i32, i32)> = active
        .iter()
        .filter(move |c| {
            let cnt = get_neighbors(*c)
                .iter()
                .filter(|n| active.contains(n))
                .count();
            cnt == 2 || cnt == 3
        })
        .cloned()
        .collect();
    let mut inactive: HashSet<(i32, i32, i32)> =
        get_neighbors(active.get(0).unwrap()).into_iter().collect();
    for c in active.iter().skip(1) {
        inactive.extend(get_neighbors(c));
    }
    for c in inactive {
        let cnt = get_neighbors(&c)
            .iter()
            .filter(|n| active.contains(n))
            .count();
        if cnt == 3 && !new_active.contains(&c) {
            new_active.push(c);
        }
    }

    new_active
}

fn step_4d(active: &[(i32, i32, i32, i32)]) -> Vec<(i32, i32, i32, i32)> {
    //let new_active: Vec<(i32,i32,i32)> = Vec::new();
    let mut new_active: Vec<(i32, i32, i32, i32)> = active
        .iter()
        .filter(move |c| {
            let cnt = get_neighbors_4d(*c)
                .iter()
                .filter(|n| active.contains(n))
                .count();
            cnt == 2 || cnt == 3
        })
        .cloned()
        .collect();
    let mut inactive: HashSet<(i32, i32, i32, i32)> = get_neighbors_4d(active.get(0).unwrap())
        .into_iter()
        .collect();
    for c in active.iter().skip(1) {
        inactive.extend(get_neighbors_4d(c));
    }
    for c in inactive {
        let cnt = get_neighbors_4d(&c)
            .iter()
            .filter(|n| active.contains(n))
            .count();
        if cnt == 3 && !new_active.contains(&c) {
            new_active.push(c);
        }
    }

    new_active
}

fn get_neighbors(cell: &(i32, i32, i32)) -> Vec<(i32, i32, i32)> {
    let mut n = Vec::new();
    for x in cell.0 - 1..cell.0 + 2 {
        for y in cell.1 - 1..cell.1 + 2 {
            for z in cell.2 - 1..cell.2 + 2 {
                if (x, y, z) != *cell {
                    n.push((x, y, z));
                }
            }
        }
    }
    n
}

fn get_neighbors_4d(cell: &(i32, i32, i32, i32)) -> Vec<(i32, i32, i32, i32)> {
    let mut n = Vec::new();
    for x in cell.0 - 1..cell.0 + 2 {
        for y in cell.1 - 1..cell.1 + 2 {
            for z in cell.2 - 1..cell.2 + 2 {
                for w in cell.3 - 1..cell.3 + 2 {
                    if (x, y, z, w) != *cell {
                        n.push((x, y, z, w));
                    }
                }
            }
        }
    }
    n
}

#[test]
fn test_parsing() {
    let active_cell_coords = parse_input(
        ".#.
..#
###",
    );
    assert_eq!(5, active_cell_coords.len());
    println!("{:#?}", active_cell_coords);
    assert!(active_cell_coords.contains(&(1, 0, 0)));
}

#[test]
fn test_get_neighbors() {
    let neightbors = get_neighbors(&(1, 2, 3));
    println!("{:#?}", neightbors);
    assert_eq!(26, neightbors.len());
    assert!(neightbors.contains(&(2, 2, 2)));
    assert!(neightbors.contains(&(0, 2, 3)));
    assert!(!neightbors.contains(&(-1, 2, 3)));
}

#[test]
fn test_step() {
    let active_cell_coords = parse_input(
        ".#.
..#
###",
    );
    let mut new_active = step(&active_cell_coords);
    println!("{:#?}", new_active);
    assert_eq!(11, new_active.len());
    for _ in 0..5 {
        println!("{:#?}", new_active.len());
        new_active = step(&new_active);
    }
    assert_eq!(112, new_active.len());
}

// #[test]
// fn test_step_4d() {
//     let active_cell_coords = parse_input_4d(
//         ".#.
// ..#
// ###",
//     );
//     let mut new_active = step_4d(&active_cell_coords);
//     println!("{:#?}", new_active);
//     assert_eq!(29, new_active.len());
//     for _ in 0..5 {
//         println!("{:#?}", new_active.len());
//         new_active = step_4d(&new_active);
//     }
//     assert_eq!(848, new_active.len());
// }
