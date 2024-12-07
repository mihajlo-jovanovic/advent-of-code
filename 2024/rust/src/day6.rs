use std::collections::HashSet;

#[aoc_generator(day6)]
pub fn input_generator(input: &str) -> HashSet<(i16, i16)> {
    input
        .lines()
        .enumerate()
        .flat_map(|(y, l)| {
            l.chars().enumerate().filter_map(move |(x, c)| {
                if c == '#' {
                    Some((x as i16, y as i16))
                } else {
                    None
                }
            })
        })
        .collect()
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
enum Direction {
    Up,
    Down,
    Left,
    Right,
}

#[derive(Debug, Eq, PartialEq, Hash, Clone, Copy)]
struct State {
    x: i16,
    y: i16,
    direction: Direction,
}

fn change_direction(direction: Direction) -> Direction {
    match direction {
        Direction::Up => Direction::Right,
        Direction::Down => Direction::Left,
        Direction::Left => Direction::Up,
        Direction::Right => Direction::Down,
    }
}

fn off_grid(pos: (i16, i16), mx_sz: usize) -> bool {
    pos.0 < 0 || pos.1 < 0 || pos.0 as usize >= mx_sz || pos.1 as usize >= mx_sz
}

fn walk_path(walls: &HashSet<(i16, i16)>, mut state: State) -> HashSet<(i16, i16)> {
    let mut visited = HashSet::new();
    let mx_sz = walls.iter().fold(0, |acc, (x, _)| acc.max(*x as usize));

    loop {
        let (px, py) = (state.x, state.y);
        visited.insert((px, py));
        let (nx, ny) = match state.direction {
            Direction::Up => (px, py - 1),
            Direction::Down => (px, py + 1),
            Direction::Left => (px - 1, py),
            Direction::Right => (px + 1, py),
        };

        if off_grid((nx, ny), mx_sz + 1) {
            return visited;
        }

        if walls.contains(&(nx, ny)) {
            state.direction = change_direction(state.direction);
            continue;
        }

        visited.insert((nx, ny));
        state.x = nx;
        state.y = ny;
    }
}

#[aoc(day6, part1)]
fn part1(walls: &HashSet<(i16, i16)>) -> usize {
    let state = State {
        x: 52,
        y: 43,
        direction: Direction::Up,
    };
    walk_path(walls, state).len()
}

fn in_a_loop(walls: &HashSet<(i16, i16)>, mut state: State) -> bool {
    let mut visited = HashSet::new();
    let mx_sz = walls.iter().fold(0, |acc, (x, _)| acc.max(*x as usize));

    loop {
        visited.insert(state);
        let (px, py) = (state.x, state.y);
        let (nx, ny) = match state.direction {
            Direction::Up => (px, py - 1),
            Direction::Down => (px, py + 1),
            Direction::Left => (px - 1, py),
            Direction::Right => (px + 1, py),
        };

        if visited.contains(&State {
            x: nx,
            y: ny,
            direction: state.direction,
        }) {
            return true;
        }

        if off_grid((nx, ny), mx_sz + 1) {
            return false;
        }

        if walls.contains(&(nx, ny)) {
            state.direction = change_direction(state.direction);
            continue;
        }

        state.x = nx;
        state.y = ny;
    }
}

#[aoc(day6, part2)]
fn part2(walls: &HashSet<(i16, i16)>) -> usize {
    let state = State {
        x: 52,
        y: 43,
        direction: Direction::Up,
    };
    let mut walls2 = walls.clone();
    let path = walk_path(walls, state);
    let mut count = 0;
    for pos in path {
        walls2.insert(pos);
        if in_a_loop(&walls2, state) {
            count += 1;
        }
        walls2.remove(&pos);
    }
    count
}

#[cfg(test)]
mod tests {
    use super::*;

    const INPUT: &str = include_str!("../input/2024/day6-sample.txt");

    #[test]
    fn parse_input() {
        let paths = input_generator(INPUT);
        assert_eq!(paths.len(), 8);
        assert!(paths.contains(&(4, 0)));
        assert!(paths.contains(&(9, 1)));
        assert!(paths.contains(&(2, 3)));
        assert!(paths.contains(&(6, 9)));
        assert!(!paths.contains(&(7, 9)));
    }

    #[test]
    fn in_a_loop_test() {
        let mut walls = input_generator(INPUT);
        let state = State {
            x: 4,
            y: 6,
            direction: Direction::Up,
        };
        assert_eq!(in_a_loop(&walls, state), false);
        walls.insert((3, 6));
        assert_eq!(in_a_loop(&walls, state), true);
        walls.remove(&(3, 6));
        walls.insert((5, 6));
        assert_eq!(in_a_loop(&walls, state), false);
        walls.remove(&(5, 6));
        walls.insert((7, 9));
        assert_eq!(in_a_loop(&walls, state), true);
    }
}
