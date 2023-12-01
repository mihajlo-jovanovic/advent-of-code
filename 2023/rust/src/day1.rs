use itertools::Itertools;

#[aoc_generator(day1)]
pub fn input_generator(input: &str) -> Vec<String> {
    input.lines().map(|l| l.to_string()).collect()
}

const NUMBERS: [&str; 9] = [
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
];

fn find_first_occurrence_in_str(s: &str, numbers: [&str; 9]) -> u8 {
    numbers
        .iter()
        .enumerate()
        .flat_map(|(i, &n)| s.find(n).map(|pos| (i + 1, pos)))
        .merge(
            s
                .to_string()
                .chars()
                .enumerate()
                .filter(|(_, c)| c.is_ascii_digit())
                .map(|(i, c)| (c.to_digit(10).unwrap() as usize, i))
        )
        .min_by_key(|&(_, a)| a)
        .unwrap()
        .0 as u8
}

// write a unit test for find_first_occurrence_in_str
#[test]
fn test_find_first_occurrence_in_str() {
    assert_eq!(2, find_first_occurrence_in_str("two1nine", NUMBERS));
    assert_eq!(8, find_first_occurrence_in_str("eightwothree", NUMBERS));
    assert_eq!(1, find_first_occurrence_in_str("abcone2threexyz", NUMBERS));
    assert_eq!(2, find_first_occurrence_in_str("xtwone3four", NUMBERS));
    assert_eq!(4, find_first_occurrence_in_str("4nineeightseven2", NUMBERS));
    assert_eq!(1, find_first_occurrence_in_str("1q", NUMBERS));
    assert_eq!(1, find_first_occurrence_in_str("zoneight234", NUMBERS));
    assert_eq!(7, find_first_occurrence_in_str("7pqrstsixteen", NUMBERS));
    assert_eq!(2, find_first_occurrence_in_str("2onestsixteen", NUMBERS));
    assert_eq!(9, find_first_occurrence_in_str("nine1", NUMBERS));
}

#[test]
fn test_find_numbers() {
    assert_eq!(vec!((3, 1)), find_numbers("two1nine"));
    assert_eq!(vec!((0, 1), (4, 2)), find_numbers("1abc2"));
    assert_eq!(vec!((3, 3), (7, 8)), find_numbers("pqr3stu8vwx"));
    assert_eq!(vec!((1, 1), (3, 2), (5, 3), (7, 4), (9, 5)), find_numbers("a1b2c3d4e5f"));
    assert_eq!(vec!((4, 7)), find_numbers("treb7uchet"))
}

fn find_numbers(string: &str) -> Vec<(usize, usize)> {

    string.to_string()
        .chars()
        .enumerate()
        .filter(|(_, c)| c.is_ascii_digit())
        .map(|(i, c)| (i, c.to_digit(10).unwrap() as usize)).collect()

    //vec!((0,2),(3,1),(4,9))
}

fn find_digits_spelled_out(string: &str) -> Vec<(usize, usize)> {
    let nums = [
        "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
    ];
    nums.iter().enumerate().flat_map(|(i, s)| string.find(s).map(|pos| (pos, i+1))).collect()
}

#[test]
fn test_find_digits_spelled_out() {
    assert_eq!(vec!((0, 2),(4,9)), find_digits_spelled_out("two1nine"));
    assert_eq!(vec!((0, 2),(4,9), (8,2)), find_digits_spelled_out("two1ninetwo"));
    assert_eq!(vec!((4, 2), (7, 3), (0, 8),), find_digits_spelled_out("eightwothree"));
    assert_eq!(vec!((3, 1), (7, 3)), find_digits_spelled_out("abcone2threexyz"));
    assert_eq!(vec!((3, 1), (1, 2), (7, 4)), find_digits_spelled_out("xtwone3four"));
    assert_eq!(vec!((10, 7), (5, 8), (1, 9)), find_digits_spelled_out("4nineeightseven2"))
}

fn last_first_occurrence_in_str(my_string: &str, numbers: [&str; 9]) -> u8 {
    numbers
        .iter()
        .enumerate()
        .flat_map(|(s, &i)| my_string.rfind(i).map(|idx| (s + 1, idx)))
        .merge(
            my_string
                .to_string()
                .chars()
                .enumerate()
                .filter(|(_, c)| c.is_ascii_digit())
                .map(|(s, c)| (c.to_digit(10).unwrap() as usize, s))
        )
        .max_by_key(|&(_, a)| a)
        .unwrap()
        .0 as u8
}

#[test]
fn test_last_first_occurrence_in_str() {
    assert_eq!(9, last_first_occurrence_in_str("two1nine", NUMBERS));
    assert_eq!(3, last_first_occurrence_in_str("eightwothree", NUMBERS));
    assert_eq!(3, last_first_occurrence_in_str("abcone2threexyz", NUMBERS));
    assert_eq!(4, last_first_occurrence_in_str("xtwone3four", NUMBERS));
    assert_eq!(2, last_first_occurrence_in_str("4nineeightseven2", NUMBERS));
    assert_eq!(1, last_first_occurrence_in_str("1q", NUMBERS));
    assert_eq!(4, last_first_occurrence_in_str("zoneight234", NUMBERS));
    assert_eq!(6, last_first_occurrence_in_str("7pqrstsixteen", NUMBERS));
    assert_eq!(4, last_first_occurrence_in_str("2onestsix4teen", NUMBERS));
    assert_eq!(1, last_first_occurrence_in_str("2onestsix4teenone", NUMBERS));
}

#[aoc(day1, part1)]
fn part1(input: &[String]) -> u32 {
    input
        .iter()
        .map(|s| {
            let digits = s.chars().filter(|c| c.is_ascii_digit()).collect::<String>();
            let first_digit = digits.chars().next().unwrap().to_digit(10).unwrap();
            let second_digit = digits.chars().last().unwrap().to_digit(10).unwrap();
            first_digit * 10 + second_digit
        })
        .sum()
}

#[aoc(day1, part2)]
fn part2(input: &[String]) -> usize {
    input
        .iter()
        .map(|s| {
            let first_digit = find_first_occurrence_in_str(s.as_str(), NUMBERS);
            let second_digit = last_first_occurrence_in_str(s.as_str(), NUMBERS);
            (first_digit as usize * 10 + second_digit as usize) as usize
        })
        .sum()
}
