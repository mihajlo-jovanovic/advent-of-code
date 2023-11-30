#[aoc_generator(day4)]
pub fn input_generator(input: &str) -> Vec<Vec<String>> {
    input
        .lines()
        .map(|line| {
            line.split_whitespace()
                .map(|word| word.to_string())
                .collect()
        })
        .collect()
}

fn generate_pairs(strings: &[String]) -> Vec<(String, String)> {
    let mut pairs = Vec::new();

    for (i, string1) in strings.iter().enumerate() {
        for string2 in strings.iter().skip(i + 1) {
            pairs.push((string1.clone(), string2.clone()));
        }
    }

    pairs
}

fn anagram(word1: &str, word2: &str) -> bool {
    let mut chars1: Vec<char> = word1.chars().collect();
    let mut chars2: Vec<char> = word2.chars().collect();

    chars1.sort();
    chars2.sort();

    chars1 == chars2
}

#[aoc(day4, part1)]
pub fn solve_part1(input: &[Vec<String>]) -> usize {
    input
        .iter()
        .filter(|line| {
            let pairs = generate_pairs(line);
            pairs.iter().all(|(w1, w2)| w1 != w2)
        })
        .count()
}

#[aoc(day4, part2)]
pub fn solve_part2(input: &[Vec<String>]) -> usize {
    input
        .iter()
        .filter(|line| {
            let pairs = generate_pairs(line);

            pairs.iter().all(|(w1, w2)| !anagram(w1, w2))
        })
        .count()
}
