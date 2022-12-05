use aoc_runner_derive::{aoc, aoc_generator};
use regex::Regex;

use lazy_static::lazy_static;

#[aoc_generator(day5)]
fn generator_input(input: &str) -> Vec<(u8, u8, u8)> {
    input
        .lines()
        .map(|l| {
            lazy_static! {
                static ref RE: Regex = Regex::new(r"^move (\d+) from (\d+) to (\d+)$").unwrap();
            }
            if let Some(caps) = RE.captures(l) {
                let quantity: u8 = caps[1].parse::<u8>().unwrap();
                let c1: u8 = caps[2].parse::<u8>().unwrap();
                let c2: u8 = caps[3].parse::<u8>().unwrap();
                (quantity, c1, c2)
            } else {
                panic!("Input string did not match regular expression");
            }
        })
        .collect()
}

#[aoc(day5, part1)]
fn part1(instructions: &[(u8, u8, u8)]) -> String {
    // let c1 = vec!['Z', 'N'];
    // let c2 = vec!['M', 'C', 'D'];
    // let c3 = vec!['P'];
    // let mut crates = vec![c1, c2, c3];
    let mut crates = vec![
        vec!['Q', 'H', 'C', 'T', 'N', 'S', 'V', 'B'],
        vec!['G', 'B', 'D', 'W'],
        vec!['B', 'Q', 'S', 'T', 'R', 'W', 'F'],
        vec!['N', 'D', 'J', 'Z', 'S', 'W', 'G', 'L'],
        vec!['F', 'V', 'D', 'P', 'M'],
        vec!['J', 'W', 'F'],
        vec!['V', 'J', 'B', 'Q', 'N', 'L'],
        vec!['N', 'S', 'Q', 'J', 'C', 'R', 'T', 'G'],
        vec!['M', 'D', 'W', 'C', 'Q', 'S', 'J'],
    ];
    for c in &mut crates {
        c.reverse();
    }
    for (q, c1, c2) in instructions {
        let from: &mut Vec<char> = &mut crates[*c1 as usize - 1];
        let u: Vec<_> = from.drain(from.len() - (*q as usize)..).collect();
        let to: &mut Vec<char> = &mut crates[*c2 as usize - 1];
        to.extend(u);
        // part #1
        // for _ in 0..*q {
        //     let from: &mut Vec<char> = &mut crates[*c1 as usize - 1];
        //     let tmp = from.pop().unwrap();
        //     let to: &mut Vec<char> = &mut crates[*c2 as usize - 1];
        //     to.push(tmp);
        //     println!("moved a single crate {} from {} to {}...", tmp, c1, c2);
        // }
    }
    let s: String = crates.iter().map(|c| c.last().unwrap()).collect();
    s
}
