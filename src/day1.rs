use itertools::Itertools;

#[aoc_generator(day1)]
fn generator_input(input: &str) -> Vec<i32> {
    input.lines().map(|l| l.parse::<i32>().unwrap()).collect()
}

const SUM: i32 = 2020;

#[aoc(day1, part1)]
fn part1(lines: &[i32]) -> Option<i32> {
    for (i, n1) in lines.iter().enumerate() {
        for (j, n2) in lines.iter().enumerate() {
            if i == j {
                continue;
            }
            if n1 + n2 == SUM {
                println!("{} {}", n1, n2);
                return Some(n1 * n2);
            }
        }
    }
    None
}

#[aoc(day1, part2)]
fn part2(lines: &[i32]) -> Option<i32> {
    for n1 in lines {
        for n2 in lines {
            for n3 in lines {
                if n1 + n2 + n3 == SUM {
                    return Some(n1 * n2 * n3);
                }
            }
        }
    }
    None
}

#[allow(dead_code)]
fn part1_alt(lines: &[i32]) -> Option<i32> {
    lines.iter().permutations(2).filter(|p| p[0]+p[1]==SUM).map(|p| p[0]*p[1]).next()
}

#[allow(dead_code)]
fn part2_alt(lines: &[i32]) -> Option<i32> {
    lines.iter().permutations(3).filter(|p| p[0]+p[1]+p[2]==SUM).map(|p| p[0]*p[1]*p[2]).next()
}

#[allow(dead_code)]
const TEST_INPUT: &[i32] = &[1721, 979, 366, 299, 675, 1456];

#[test]
fn test_part1() {
    assert_eq!(Some(514579), part1(TEST_INPUT));
    assert_eq!(Some(514579), part1_alt(TEST_INPUT));
}

#[test]
fn test_part2() {
    assert_eq!(Some(241861950), part2(TEST_INPUT));
    assert_eq!(Some(241861950), part2_alt(TEST_INPUT));
}
