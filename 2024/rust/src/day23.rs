use std::collections::{BTreeSet, HashSet};

#[aoc_generator(day23)]
fn input_generator(input: &str) -> Vec<(String, String)> {
    input
        .lines()
        .map(|l| {
            let mut parts = l.split("-");
            (
                parts.next().unwrap().to_string(),
                parts.next().unwrap().to_string(),
            )
        })
        .collect()
}

fn all_pairs(l: &[String]) -> Vec<(String, String)> {
    let mut pairs = vec![];
    for i in 0..l.len() {
        for j in i + 1..l.len() {
            pairs.push((l[i].clone(), l[j].clone()));
        }
    }
    pairs
}

#[test]
fn test_all_pairs() {
    let l = vec![
        "yn".to_string(),
        "vc".to_string(),
        "cg".to_string(),
        "wq".to_string(),
    ];
    let pairs = all_pairs(&l);
    assert_eq!(pairs.len(), 6);
    assert_eq!(pairs[0], ("yn".to_string(), "vc".to_string()));
    assert_eq!(pairs[1], ("yn".to_string(), "cg".to_string()));
    assert_eq!(pairs[2], ("yn".to_string(), "wq".to_string()));
    assert_eq!(pairs[3], ("vc".to_string(), "cg".to_string()));
    assert_eq!(pairs[4], ("vc".to_string(), "wq".to_string()));
    assert_eq!(pairs[5], ("cg".to_string(), "wq".to_string()));
}

#[aoc(day23, part1)]
fn part1(input: &[(String, String)]) -> usize {
    //convert input into a HashMap, where keys are all possible values of either the first or the
    //second part of the tuple, and values are a Vector of all String values that appear in the
    //same tuple as the keys
    let mut map = std::collections::HashMap::new();
    for (a, b) in input {
        map.entry(a.clone()).or_insert(vec![]).push(b.clone());
        map.entry(b.clone()).or_insert(vec![]).push(a.clone());
    }
    let mut unique_combinations: HashSet<BTreeSet<String>> = HashSet::new();
    for (k, v) in map.iter() {
        let pairs = all_pairs(v);
        for (a, b) in pairs {
            if map.get(&a).unwrap().contains(&b)
                && (k.starts_with("t") || a.starts_with("t") || b.starts_with("t"))
            {
                let s: BTreeSet<String> = [k, &a, &b].iter().map(|&s| s.to_string()).collect();
                unique_combinations.insert(s);
            }
        }
    }

    //println!("{:?}", unique_combinations);
    unique_combinations.len()
}

struct Graph {
    adj_list: Vec<Vec<bool>>,
    size: usize,
}

impl Graph {
    fn new(size: usize) -> Self {
        Graph {
            adj_list: vec![vec![false; size]; size],
            size,
        }
    }

    fn add_edge(&mut self, u: usize, v: usize) {
        self.adj_list[u][v] = true;
        self.adj_list[v][u] = true;
    }

    fn is_connected(&self, u: usize, v: usize) -> bool {
        self.adj_list[u][v]
    }
}

fn find_largest_clique(g: &Graph) -> HashSet<usize> {
    let mut max_clique = HashSet::new();
    let mut current_clique = HashSet::new();

    fn branch_and_bound(
        g: &Graph,
        candidates: Vec<usize>,
        current_clique: &mut HashSet<usize>,
        max_clique: &mut HashSet<usize>,
    ) {
        if candidates.is_empty() {
            if current_clique.len() > max_clique.len() {
                *max_clique = current_clique.clone();
            }
            return;
        }

        for (i, &candidate) in candidates.iter().enumerate() {
            current_clique.insert(candidate);

            let next_candidates: Vec<usize> = candidates[i + 1..]
                .iter()
                .copied()
                .filter(|&v| g.is_connected(candidate, v))
                .collect();
            branch_and_bound(g, next_candidates, current_clique, max_clique);
            current_clique.remove(&candidate);
        }
    }

    let candidates: Vec<usize> = (0..g.size).collect();

    branch_and_bound(g, candidates, &mut current_clique, &mut max_clique);

    max_clique
}

#[aoc(day23, part2)]
fn part2(input: &[(String, String)]) -> String {
    let mut map = std::collections::HashMap::new();
    let mut nodes = vec![];
    let mut idx = 0;
    for (a, b) in input {
        let i1 = map.entry(a.clone()).or_insert(idx);
        if *i1 == idx {
            nodes.push(a.clone());
            idx += 1;
        }
        let i2 = map.entry(b.clone()).or_insert(idx);
        if *i2 == idx {
            nodes.push(b.clone());
            idx += 1;
        }
    }
    let mut g = Graph::new(nodes.len());
    for (a, b) in input {
        let i1 = *map.get(a).unwrap();
        let i2 = *map.get(b).unwrap();
        g.add_edge(i1, i2);
    }
    let clique = find_largest_clique(&g);
    let mut result = clique
        .into_iter()
        .map(|i| nodes[i].clone())
        .collect::<Vec<String>>();
    result.sort();
    result.join(",")
}
