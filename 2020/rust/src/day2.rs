use std::convert::TryInto;

#[derive(Debug)]
pub struct Passwords {
    min: u32,
    max: u32,
    c: char,
    pwd: String,
}

#[aoc_generator(day2)]
fn generator_input(input: &str) -> Vec<Passwords> {
    input
        .lines()
        .map(|l| {
            let tokens: Vec<&str> = l.trim().split(' ').collect();
            let range: Vec<&str> = tokens[0].split('-').collect();
            Passwords {
                min: range[0].parse().unwrap(),
                max: range[1].parse().unwrap(),
                c: tokens[1].chars().next().unwrap(),
                pwd: tokens[2].to_string(),
            }
        })
        .collect()
}

fn is_valid(p: &Passwords) -> bool {
    let cnt: u32 = p.pwd.chars().map(|c| if c == p.c { 1 } else { 0 }).sum();
    cnt >= p.min && cnt <= p.max
}

#[aoc(day2 part1)]
fn part1(lines: &[Passwords]) -> usize {
    lines.iter().filter(|&p| is_valid(p)).count()
}

fn is_valid_part2(p: &Passwords) -> bool {
    let pos1 = p.pwd.chars().nth((p.min - 1).try_into().unwrap()).unwrap();
    let pos2 = p.pwd.chars().nth((p.max - 1).try_into().unwrap()).unwrap();
    pos1 == p.c && pos2 != p.c || pos1 != p.c && pos2 == p.c
}

#[aoc(day2 part2)]
fn part2(lines: &[Passwords]) -> usize {
    lines.iter().filter(|&p| is_valid_part2(p)).count()
}

#[test]
fn test_valid_password() {
    let p = Passwords {
        min: 14,
        max: 17,
        c: 'n',
        pwd: String::from("nnhnnnnnnnnnnnnnhnn"),
    };
    assert_eq!(true, is_valid(&p));
    assert_eq!(
        true,
        is_valid(&Passwords {
            min: 1,
            max: 3,
            c: 'a',
            pwd: String::from("abcde")
        })
    );
    assert_eq!(
        false,
        is_valid(&Passwords {
            min: 1,
            max: 3,
            c: 'b',
            pwd: String::from("cdefg")
        })
    );
    assert_eq!(
        true,
        is_valid(&Passwords {
            min: 2,
            max: 9,
            c: 'c',
            pwd: String::from("ccccccccc")
        })
    );
    assert_eq!(
        true,
        is_valid_part2(&Passwords {
            min: 1,
            max: 3,
            c: 'a',
            pwd: String::from("abcde")
        })
    );
    assert_eq!(
        false,
        is_valid_part2(&Passwords {
            min: 1,
            max: 3,
            c: 'b',
            pwd: String::from("cdefg")
        })
    );
    assert_eq!(
        false,
        is_valid_part2(&Passwords {
            min: 2,
            max: 9,
            c: 'c',
            pwd: String::from("ccccccccc")
        })
    );
}
