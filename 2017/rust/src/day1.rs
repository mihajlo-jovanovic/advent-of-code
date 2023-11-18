#[aoc_generator(day1)]
pub fn input_generator(input: &str) -> Vec<u8> {
    // We need to convert the input string into a vector of integers
    input
        .chars()
        .map(|c| c.to_digit(10).unwrap() as u8)
        .collect()
}

#[aoc(day1, part1)]
pub fn solve_part1(input: &[u8]) -> usize {
    // Return the sum of all elements that are equal to the next element
    input
        .iter()
        .zip(input.iter().cycle().skip(1))
        .filter(|&(a, b)| a == b)
        .map(|(a, _)| a)
        .map(|&a| a as usize)
        .sum()
}

#[aoc(day1, part2)]
pub fn solve_part2(input: &[u8]) -> usize {
    input
        .iter()
        .zip(input.iter().cycle().skip(input.len() / 2))
        .filter(|&(a, b)| a == b)
        .map(|(a, _)| a)
        .map(|&a| a as usize)
        .sum()
}

// Write a unit test
#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_part1() {
        assert_eq!(solve_part1(&input_generator("1122")), 3);
        assert_eq!(solve_part1(&input_generator("1111")), 4);
        assert_eq!(solve_part1(&input_generator("1234")), 0);
        assert_eq!(solve_part1(&input_generator("91212129")), 9);
    }
}
