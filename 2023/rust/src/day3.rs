use itertools::Itertools;
use lazy_static::lazy_static;
use std::collections::HashSet;

#[derive(Debug, Clone, PartialEq, Eq)]
enum NumberOrSymbol {
    Number(usize),
    Symbol(char),
}

#[derive(Debug, Clone, PartialEq, Eq)]
struct NumberWithPosition {
    number_or_symbol: NumberOrSymbol,
    position: (i32, i32),
}

#[aoc_generator(day3)]
fn input_generator(input: &str) -> Vec<NumberWithPosition> {
    let symbols: Vec<_> = input
        .lines()
        .enumerate()
        .flat_map(|(row_idx, line)| {
            line.chars().enumerate().flat_map(move |(col_idx, c)| {
                if !c.is_ascii_digit() && c != '.' {
                    Some(NumberWithPosition {
                        number_or_symbol: NumberOrSymbol::Symbol(c),
                        position: (col_idx.try_into().unwrap(), row_idx.try_into().unwrap()),
                    })
                } else {
                    None
                }
            })
        })
        .collect();

    use regex::Regex;

    let numbers: Vec<_> = input
        .lines()
        .enumerate()
        .flat_map(|(y, line)| {
            lazy_static! {
                static ref NUMBER_REGEX: Regex = Regex::new(r"\d+").unwrap();
            }

            let numbers_with_positions: Vec<NumberWithPosition> = NUMBER_REGEX
                .find_iter(line)
                .map(|match_| NumberWithPosition {
                    number_or_symbol: NumberOrSymbol::Number(match_.as_str().parse().unwrap()),
                    position: (match_.start().try_into().unwrap(), y.try_into().unwrap()),
                })
                .collect();
            numbers_with_positions
        })
        .collect();

    [symbols, numbers].concat()
}

fn neighbors(p: &(i32, i32)) -> HashSet<(i32, i32)> {
    let offsets = [
        (-1, 0),
        (1, 0),
        (0, -1),
        (0, 1),
        (-1, -1),
        (-1, 1),
        (1, -1),
        (1, 1),
    ];

    offsets
        .iter()
        .map(|&(dx, dy)| (p.0 + dx, p.1 + dy))
        .collect()
}

fn digit_coordinates(number: usize, start: (i32, i32)) -> Vec<(i32, i32)> {
    let num_digits = number.to_string().len();
    (0..num_digits as i32)
        .map(|i| (start.0 + i, start.1))
        .collect()
}

#[aoc(day3, part1)]
fn part1(input: &[NumberWithPosition]) -> usize {
    let (numbers, symbols): (Vec<_>, Vec<_>) =
        input
            .iter()
            .partition_map(|value| match value.number_or_symbol {
                NumberOrSymbol::Number(_) => itertools::Either::Left(value),
                NumberOrSymbol::Symbol(_) => itertools::Either::Right(value),
            });
    let adj_to_symbols = symbols
        .iter()
        .map(|number_with_position| number_with_position.position)
        .fold(HashSet::new(), |x, y| {
            x.union(&neighbors(&y)).cloned().collect()
        });
    numbers
        .iter()
        .filter_map(|num| match num.number_or_symbol {
            NumberOrSymbol::Number(n) => {
                if digit_coordinates(n, num.position)
                    .iter()
                    .any(|x| adj_to_symbols.contains(x))
                {
                    Some(n)
                } else {
                    None
                }
            }
            NumberOrSymbol::Symbol(_) => None,
        })
        .sum()
}

#[aoc(day3, part2)]
fn part2(input: &[NumberWithPosition]) -> usize {
    let (numbers, symbols): (Vec<_>, Vec<_>) =
        input
            .iter()
            .partition_map(|value| match value.number_or_symbol {
                NumberOrSymbol::Number(_) => itertools::Either::Left(value),
                NumberOrSymbol::Symbol(_) => itertools::Either::Right(value),
            });

    let number_coordinates: Vec<_> = numbers
        .iter()
        .map(|num| match num.number_or_symbol {
            NumberOrSymbol::Number(n) => (n, digit_coordinates(n, num.position)),
            NumberOrSymbol::Symbol(_) => panic!("expected a number!"),
        })
        .collect();

    symbols
        .iter()
        .filter_map(|symbol| {
            if symbol.number_or_symbol == NumberOrSymbol::Symbol('*') {
                let adj = neighbors(&symbol.position);
                let matching_numbers: Vec<_> = number_coordinates
                    .iter()
                    .filter(|(_, coords)| coords.iter().any(|coord| adj.contains(coord)))
                    .collect();

                if matching_numbers.len() == 2 {
                    Some(
                        matching_numbers
                            .iter()
                            .map(|(num, _)| num)
                            .product::<usize>(),
                    )
                } else {
                    None
                }
            } else {
                None
            }
        })
        .sum()
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::fs;

    const TEST_INPUT: &str = "467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..";

    #[test]
    fn test_parsing() {
        assert_eq!(16, input_generator(&TEST_INPUT).len());
        assert_eq!(
            Some(&NumberWithPosition {
                number_or_symbol: NumberOrSymbol::Symbol('*'),
                position: (3, 1)
            }),
            input_generator(&TEST_INPUT).first()
        );
        assert_eq!(
            Some(&NumberWithPosition {
                number_or_symbol: NumberOrSymbol::Number(598),
                position: (5, 9)
            }),
            input_generator(&TEST_INPUT).last()
        );
    }

    #[test]
    fn test_part1() {
        let input = fs::read_to_string("input/2023/day3.txt").unwrap();
        assert_eq!(4361, part1(&input_generator(TEST_INPUT)));
        assert_eq!(556057, part1(&input_generator(&input)));
    }

    #[test]
    fn test_part2() {
        let input = fs::read_to_string("input/2023/day3.txt").unwrap();
        assert_eq!(467835, part2(&input_generator(TEST_INPUT)));
        assert_eq!(82824352, part2(&input_generator(&input)));
    }
}
