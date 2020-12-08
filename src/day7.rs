use lazy_static::lazy_static;
use petgraph::algo::has_path_connecting;
use petgraph::graphmap::DiGraphMap;
use regex::Regex;

#[aoc_generator(day7)]
fn generator_input(input: &str) -> usize {
    let al: Vec<(&str, &str)> = input
        .lines()
        .flat_map(move |l| {
            let tokens: Vec<&str> = l.split(" contain ").collect();
            let x = &tokens[0][..tokens[0].len() - 5];
            let iter = tokens[1].split(',').filter_map(move |b| {
                lazy_static! {
                    static ref RE: Regex =
                        Regex::new(r"\d+\s(?P<to>[[:word:]]+\s[[:word:]]+)\sbag").unwrap();
                }
                RE.captures(b).and_then(|cap| {
                    Some((x, (cap.name("to").map(|to| to.as_str()))?))
                })
            });
            iter
        })
        .collect();

    let g = DiGraphMap::<&str, &str>::from_edges(&al);
    g.nodes()
        .filter(|n| has_path_connecting(&g, n, "shiny gold", None))
        .count()
        - 1
}

#[aoc(day7, part1)]
fn part1(input: &usize) -> usize {
    *input
}

#[test]
fn test_generator() {
    let cnt = generator_input(
        "light red bags contain 1 bright white bag, 2 muted yellow bags.
dark orange bags contain 3 bright white bags, 4 muted yellow bags.
bright white bags contain 1 shiny gold bag.
muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
dark olive bags contain 3 faded blue bags, 4 dotted black bags.
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
dotted black bags contain no other bags.",
    );
    assert_eq!(4, cnt);
}
