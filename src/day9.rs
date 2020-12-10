use itertools::Itertools;

#[aoc_generator(day9)]
fn generator_input(input: &str) -> Vec<u64> {
    input.lines().map(|l| l.parse::<u64>().unwrap()).collect()
}

const WINDOW_SIZE: usize = 25;

#[aoc(day9, part1)]
fn part1(input: &[u64]) -> Option<u64> {
    for (i, num) in input.iter().enumerate() {
        if i < WINDOW_SIZE {
            continue;
        }
        let beg: usize = (i - WINDOW_SIZE) as usize;
        if !is_valid_number(&input[beg..i], *num) {
            return Some(*num)
        }
    }
    None
}

fn is_valid_number(w: &[u64], num: u64) -> bool {
    for t in w.iter().combinations(2).map(|p| p[0]+p[1]) {
        if num == t {
            return true
        }
    }
    false
}

#[aoc(day9, part2)]
fn part2(input: &[u64]) -> Option<u64> {
    let pos = (0..input.len()-1).find(|&i| contiguous_range(input, i));
    match pos {
        Some(idx) => {
            println!("index is {:?}", idx);
            let mut acc: u64 = 0;
            for (i, n) in input[idx..].iter().enumerate() {
                acc += n;
                if acc == SUM {
                    return Some(*input[idx.. idx+i].iter().min().unwrap() + *input[idx.. idx+i].iter().max().unwrap())
                }
            }
        },
        None => { panic!("Not suppose to happen") }
    }
    None
}

const SUM: u64 = 1212510616;

fn contiguous_range(input: &[u64], pos: usize) -> bool {
    let mut acc: u64 = 0;
    for num in &input[pos..] {
        acc += num;
        match acc {
            SUM => return true,
            a if a > SUM => return false,
            _ => continue
        }
    }        
    true
}

#[test]
fn test_check() {
    assert!(is_valid_number(&[20, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 23, 22, 24, 25], 45));
    assert!(!is_valid_number(&[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 23, 22, 24, 25, 45], 65));
    assert!(is_valid_number(&[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 23, 22, 24, 25, 45], 64));
}