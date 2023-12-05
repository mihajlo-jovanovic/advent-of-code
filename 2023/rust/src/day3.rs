use std::collections::HashSet;

#[aoc_generator(day3)]
fn input_generator(input: &str) -> Vec<(char, (usize, usize))> {
    input
        .lines()
        .enumerate()
        .flat_map(|(row_idx, line)| {
            line.chars().enumerate().flat_map(move |(col_idx, c)| {
                if !c.is_ascii_digit() && c != '.' {
                    Some((c, (col_idx, row_idx)))
                } else {
                    None
                }
            })
        })
        .collect()
}

// fn neighbors(pos: &[(usize,usize)]) -> Vec<(usize,usize)> {
//     let mut n: HashSet<(usize,usize)> = vec![].into_iter().collect();
//     pos.iter().
// }

fn neighbors(p: &(usize, usize)) -> HashSet<(usize,usize)> {
    let mut neighbors = HashSet::new();
    neighbors.insert((p.0 + 1, p.1));
    neighbors.insert((p.0 - 1, p.1));
    neighbors.insert((p.0, p.1 + 1));
    neighbors.insert((p.0, p.1 - 1));
    neighbors.insert((p.0 + 1, p.1 + 1));
    neighbors.insert((p.0 - 1, p.1 - 1));
    neighbors.insert((p.0 + 1, p.1 - 1));
    neighbors.insert((p.0 - 1, p.1 + 1));
    neighbors
}

#[aoc(day3, part1)]
fn part1(input: &[(char, (usize, usize))]) -> usize {
    //print!("{:?}", input);
    let tmp: Vec<(usize, usize)> = input.iter().map(|(_, pos)| *pos).collect();
    //print!("{:?}", tmp);
    let n = tmp.iter().fold(HashSet::new(), |x,y| { x.union(&neighbors(y)).cloned().collect() });
    print!("Total positions to include: {}", n.len());
    tmp.len()
}
