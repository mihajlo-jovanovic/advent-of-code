use std::collections::HashSet;

#[aoc_generator(day19)]
pub fn input_generator(input: &str) -> (HashSet<String>, Vec<String>) {
    let parts: Vec<_> = input.split("\n\n").collect();
    let towel_patterns = parts[0].split(",").map(|s| s.trim().to_string()).collect();
    let designs = parts[1].split("\n").map(|s| s.to_string()).collect();
    (towel_patterns, designs)
}

fn can_construct(s: &str, parts: &HashSet<&str>) -> bool {
    let n = s.len();
    let mut dp = vec![false; n + 1];
    dp[0] = true; // Empty prefix can always be formed

    // Check each prefix
    for i in 0..n {
        if dp[i] {
            // If we can form s[..i], try to extend with any known part
            for j in i + 1..=n {
                let substring = &s[i..j];
                if parts.contains(substring) {
                    dp[j] = true;
                }
            }
        }
    }

    dp[n]
}

fn count_constructions(s: &str, parts: &HashSet<&str>) -> usize {
    let n = s.len();
    let mut dp = vec![0_usize; n + 1];
    dp[0] = 1; // One way to form the empty string

    for i in 0..n {
        // If dp[i] > 0, it means we have some ways to form s[..i]
        if dp[i] > 0 {
            for j in i + 1..=n {
                let substring = &s[i..j];
                if parts.contains(substring) {
                    dp[j] += dp[i];
                }
            }
        }
    }

    dp[n]
}

#[test]
fn test_can_construct() {
    let parts = vec!["r", "wr", "b", "g", "bwu", "rb", "gb", "br"];
    let parts_set: HashSet<&str> = parts.into_iter().collect();
    assert_eq!(can_construct("brwrr", &parts_set), true);
    assert_eq!(!can_construct("bbrgwb", &parts_set), true);
}

#[test]
fn test_count_constructions() {
    let parts = vec!["r", "wr", "b", "g", "bwu", "rb", "gb", "br"];
    let parts_set: HashSet<&str> = parts.into_iter().collect();
    assert_eq!(count_constructions("brwrr", &parts_set), 2);
    assert_eq!(count_constructions("bbrgwb", &parts_set), 0);
}

#[aoc(day19, part1)]
fn part1(input: &(HashSet<String>, Vec<String>)) -> usize {
    let (towel_patterns, designs) = input;
    let towel_patterns: HashSet<&str> = towel_patterns.iter().map(|s| s.as_str()).collect();
    designs
        .iter()
        .filter(|d| can_construct(d, &towel_patterns))
        .count()
}

#[aoc(day19, part2)]
fn part2(input: &(HashSet<String>, Vec<String>)) -> usize {
    let (towel_patterns, designs) = input;
    let towel_patterns: HashSet<&str> = towel_patterns.iter().map(|s| s.as_str()).collect();
    designs
        .iter()
        .map(|d| count_constructions(d, &towel_patterns))
        .sum()
}
