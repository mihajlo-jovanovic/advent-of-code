use regex::Regex;
use std::collections::HashMap;

#[aoc_generator(day19, part1)]
fn parse_input(input: &str) -> usize {
    let mut groups = input.split("\n\n");
    let rules = groups.next().unwrap();
    let messages = groups.next().unwrap();
    let re: Regex = Regex::new(&parse_regex(rules)).unwrap();
    messages.lines().filter(|l| re.is_match(l)).count()
}

#[aoc_generator(day19, part2)]
fn parse_input_part2(input: &str) -> usize {
    let mut groups = input.split("\n\n");
    let rules = groups.next().unwrap();
    let messages = groups.next().unwrap();

    let rule_42_expanded = parse_regex_part2(rules, 42);

    let rule_31_expanded = parse_regex_part2(rules, 31);
    // println!("rule 42: {}", rule_42_expanded);
    // println!("rule 31: {}", rule_31_expanded);
    println!(
        "rule 0 expanded (after mod): {}",
        parse_regex_part2(rules, 0)
    );
    // rule 0 expanded (after mod): 811

    // Not proud of this, but could not figure out hot to do a regex for ab|aabb|aaabbb...basically same num of times
    let mut nested = String::from("(");
    for i in 1..5 {
        nested.push_str("(");
        for _ in 0..i {
            nested.push_str(&rule_42_expanded);
        }
        for _ in 0..i {
            nested.push_str(&rule_31_expanded);
        }
        if i < 4 {
            nested.push_str(")|");
        } else {
            nested.push_str(")")
        }
    }
    nested.push_str(")");

    let re: Regex = Regex::new(&format!("^{0}+{1}$", rule_42_expanded, nested)).unwrap();
    messages.lines().filter(|l| re.is_match(l)).count()
}

#[aoc(day19, part1)]
fn part1(input: &usize) -> usize {
    *input
}

#[aoc(day19, part2)]
fn part2(input: &usize) -> usize {
    *input
}

fn parse_re_recur(input: &str, rules: &HashMap<u8, &str>) -> String {
    match input {
        "a" | "b" => input.to_string(),
        _ if input.contains('|') => {
            let mut spl = input.split(" | ");
            format!(
                "({}|{})",
                parse_re_recur(spl.next().unwrap(), rules),
                parse_re_recur(spl.next().unwrap(), rules)
            )
        }
        _ => input
            .split(' ')
            .map(|r| parse_re_recur(rules.get(&r.parse::<u8>().unwrap()).unwrap(), rules))
            .collect(),
    }
}

fn parse_re_recur_part2(input: &str, rules: &HashMap<u8, &str>) -> String {
    match input {
        "a" | "b" | "8" | "11" => input.to_string(),
        _ if input.contains('|') => {
            let mut spl = input.split(" | ");
            format!(
                "({}|{})",
                parse_re_recur_part2(spl.next().unwrap(), rules),
                parse_re_recur_part2(spl.next().unwrap(), rules)
            )
        }
        _ => input
            .split(' ')
            .map(|r| {
                if r == "8" || r == "11" {
                    parse_re_recur_part2(r, rules)
                } else {
                    parse_re_recur_part2(rules.get(&r.parse::<u8>().unwrap()).unwrap(), rules)
                }
            })
            .collect(),
    }
}

fn build_rules_map(input: &str) -> HashMap<u8, &str> {
    input
        .lines()
        .map(|l| {
            let tokens: Vec<&str> = l.split(':').collect();
            let mut val = tokens[1].trim();
            if val.contains('\"') {
                val = &val[1..2];
            }
            (tokens[0].parse::<u8>().unwrap(), val)
        })
        .collect()
}

fn parse_regex(input: &str) -> String {
    let rules = build_rules_map(input);
    format!("^{}$", parse_re_recur(rules.get(&0u8).unwrap(), &rules))
}

fn parse_regex_part2(input: &str, root_rule: u8) -> String {
    let rules = build_rules_map(input);
    parse_re_recur_part2(rules.get(&root_rule).unwrap(), &rules)
}

#[test]
fn test_parse_regex() {
    let rules = "0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: \"a\"
5: \"b\"";
    assert_eq!("^a((aa|bb)(ab|ba)|(ab|ba)(aa|bb))b$", parse_regex(rules));
    let re = Regex::new(&parse_regex(rules)).unwrap();
    assert!(re.is_match("ababbb"));
    assert!(!re.is_match("bababa"));
    assert!(re.is_match("abbbab"));
    assert!(!re.is_match("aaabbb"));
    assert!(!re.is_match("aaaabbb"));
}

#[test]
fn test_part1() {
    assert_eq!(
        2,
        parse_input(
            "0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: \"a\"
5: \"b\"

ababbb
bababa
abbbab
aaabbb
aaaabbb
"
        )
    );
}

#[test]
fn test_parse_re_recur() {
    let mut rules: HashMap<u8, &str> = HashMap::new();
    rules.insert(4, "a");
    rules.insert(5, "b");
    rules.insert(1, "2 3 | 3 2");
    rules.insert(2, "4 4 | 5 5");
    rules.insert(3, "4 5 | 5 4");
    assert_eq!("a", parse_re_recur("4", &rules));
    assert_eq!("b", parse_re_recur("5", &rules));
    assert_eq!("ab", parse_re_recur("4 5", &rules));
    assert_eq!("(ab|ba)", parse_re_recur("4 5 | 5 4", &rules));
    assert_eq!(
        "a((aa|bb)(ab|ba)|(ab|ba)(aa|bb))b",
        parse_re_recur("4 1 5", &rules)
    );
    //assert_eq!("c", parse_re_recur("2 3 | 3 2", &rules));
}

#[test]
fn test_part2() {
    let test_input = "42: 9 14 | 10 1
9: 14 27 | 1 26
10: 23 14 | 28 1
1: \"a\"
11: 42 31
5: 1 14 | 15 1
19: 14 1 | 14 14
12: 24 14 | 19 1
16: 15 1 | 14 14
31: 14 17 | 1 13
6: 14 14 | 1 14
2: 1 24 | 14 4
0: 8 11
13: 14 3 | 1 12
15: 1 | 14
17: 14 2 | 1 7
23: 25 1 | 22 14
28: 16 1
4: 1 1
20: 14 14 | 1 15
3: 5 14 | 16 1
27: 1 6 | 14 18
14: \"b\"
21: 14 1 | 1 14
25: 1 1 | 1 14
22: 14 14
8: 42
26: 14 22 | 1 20
18: 15 15
7: 14 5 | 1 21
24: 14 1

abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
bbabbbbaabaabba
babbbbaabbbbbabbbbbbaabaaabaaa
aaabbbbbbaaaabaababaabababbabaaabbababababaaa
bbbbbbbaaaabbbbaaabbabaaa
bbbababbbbaaaaaaaabbababaaababaabab
ababaaaaaabaaab
ababaaaaabbbaba
baabbaaaabbaaaababbaababb
abbbbabbbbaaaababbbbbbaaaababb
aaaaabbaabaaaaababaa
aaaabbaaaabbaaa
aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
babaaabbbaaabaababbaabababaaab
aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba";
    assert_eq!(12, parse_input_part2(test_input));
}
