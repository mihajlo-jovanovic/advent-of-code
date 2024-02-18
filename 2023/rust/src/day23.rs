use std::collections::{HashMap, HashSet};

#[aoc_generator(day23)]
pub fn input_generator(input: &str) -> HashSet<(u16, u16)> {
    input
        .lines()
        .enumerate()
        .flat_map(|(y, l)| {
            l.chars().enumerate().filter_map(move |(x, c)| {
                if c != '#' {
                    Some((x as u16, y as u16))
                } else {
                    None
                }
            })
        })
        .collect()
}

fn neighbors(paths: &HashSet<(u16, u16)>, (x, y): (u16, u16)) -> Vec<(u16, u16)> {
    let directions: [(i16, i16); 4] = [(-1, 0), (1, 0), (0, -1), (0, 1)];
    directions
        .iter()
        .map(|(dx, dy)| ((x as i16) + dx, (y as i16) + dy))
        .filter(|(dx, dy)| *dx >= 0 && *dy >= 0) // Calculate the neighbor coordinates
        .map(|(nx, ny)| (nx as u16, ny as u16)) // Convert coordinates to (u16, u16)
        .filter(|coord| paths.contains(coord)) // Keep only those that are in `paths`
        .collect() // Collect the results into a Vec
}

fn walk_path(
    paths: &HashSet<(u16, u16)>,
    start: (u16, u16),
    visited: &mut HashSet<(u16, u16)>,
) -> ((u16, u16), HashSet<(u16, u16)>) {
    let mut pos = start;
    visited.insert(start);

    loop {
        let ns = neighbors(paths, pos)
            .into_iter()
            .filter(|n| !visited.contains(n))
            .collect::<Vec<_>>();

        if ns.len() != 1 {
            break;
        }
        pos = ns[0];
        visited.insert(pos);
    }

    (pos, visited.clone())
}

type PathInfo = ((u16, u16), (u16, u16), usize);

fn helper(input: &HashSet<(u16, u16)>, v: (u16, u16)) -> Vec<PathInfo> {
    let neighbors = neighbors(input, v);
    let visited = HashSet::from([v]);
    neighbors
        .iter()
        .map(|n| walk_path(input, *n, &mut visited.clone()))
        .map(|(p, seen)| (p, v, (seen.len() - 1)))
        .collect()
}

fn build_reduced_graph(input: &HashSet<(u16, u16)>) -> Vec<PathInfo> {
    input
        .iter()
        .filter(|(x, y)| neighbors(input, (*x, *y)).len() > 2)
        .flat_map(move |v| helper(input, *v))
        .collect()
}

fn successors(g: &[PathInfo], v: (u16, u16)) -> HashMap<(u16, u16), usize> {
    g.iter()
        .filter(|(x, y, _)| *x == v || *y == v)
        .map(|(x, y, d)| (if *x == v { *y } else { *x }, *d))
        .collect()
}

fn dfs(g: &[PathInfo], start: (u16, u16), end: (u16, u16)) -> isize {
    let visited = HashSet::new();
    let mut stack = vec![(start, visited, 0)];
    let mut result: isize = -1;
    while let Some((curr, seen, d)) = stack.pop() {
        let mut seen_new = seen.clone();
        seen_new.insert(curr);
        let successors = successors(g, curr)
            .iter()
            .filter(|(n, _)| !seen.contains(n))
            .map(|(k, v)| (*k, seen_new.clone(), d + v))
            .collect::<Vec<_>>();

        for (n, vis, w) in successors {
            stack.push((n, vis, w));
        }
        if d as isize > result && curr == end {
            result = d as isize;
        }
    }
    result
}

#[aoc(day23, part2)]
fn part1(paths: &HashSet<(u16, u16)>) -> usize {
    let g = build_reduced_graph(paths);
    let res = dfs(&g, (1, 0), (139, 140));
    res as usize
}

#[cfg(test)]
mod tests {
    use super::*;

    const INPUT: &str = include_str!("../input/day23-sample.txt");

    #[test]
    fn parse_input() {
        let paths = input_generator(INPUT);
        assert_eq!(paths.len(), 213);
        assert!(paths.contains(&(1, 0)));
        assert!(paths.contains(&(1, 1)));
        assert!(paths.contains(&(2, 1)));
        assert!(!paths.contains(&(1, 2)));
        assert!(paths.contains(&(21, 22)));
    }

    #[test]
    fn test_walk_path() {
        let paths: HashSet<(u16, u16)> = input_generator(INPUT);
        let (last_pos, visited) = walk_path(&paths, (1, 0), &mut HashSet::from([(1, 0)]));
        assert_eq!(last_pos, (3, 5));
        assert_eq!(16, visited.len());
        let (last_pos, visited) = walk_path(&paths, (4, 5), &mut HashSet::from([(3, 5)]));
        assert_eq!(last_pos, (11, 3));
        assert_eq!(23, visited.len());
    }

    #[test]
    fn test_part2() {
        let paths: HashSet<(u16, u16)> = input_generator(INPUT);
        let g = build_reduced_graph(&paths);
        assert_eq!(154, dfs(&g, (1, 0), (21, 22)));
    }
}