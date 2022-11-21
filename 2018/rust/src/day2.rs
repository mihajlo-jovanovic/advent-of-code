use itertools::Itertools;

#[aoc_generator(day2)]
fn generator_input(input: &str) -> Vec<String> {
    input.lines().map(String::from).collect()
}

#[aoc(day2, part2)]
fn part2(input: &[String]) -> Option<String> {
    input
        .iter()
        .cartesian_product(input)
        .find(|(s1, s2)| humming(s1.as_str(), s2.as_str()) == 1)
        .map(|(a, b)| common(a, b))
}

fn humming(a: &str, b: &str) -> u8 {
    a.chars()
        .zip(b.chars())
        .map(|(c1, c2)| if c1 == c2 { 0 } else { 1 })
        .sum()
}

fn common(a: &str, b: &str) -> String {
    a.chars()
        .zip(b.chars())
        .flat_map(|(c1, c2)| if c1 == c2 { Some(c1) } else { None })
        .collect()
}

#[cfg(test)]
mod tests {
    use crate::day2::{generator_input, humming, part2};

    const INPUT: &str = "abcde
fghij
klmno
pqrst
fguij
axcye
wvxyz";

    #[test]
    fn test_humming() {
        assert_eq!(0, humming("abcde", "abcde"));
        assert_eq!(2, humming("abcde", "abdce"));
        assert_eq!(2, humming("abcde", "axcye"));
        assert_eq!(1, humming("fghij", "fguij"));
    }

    #[test]
    fn test_part2() {
        let parsed = generator_input(INPUT);
        assert_eq!(Some(String::from("fgij")), part2(&parsed))
    }
}
