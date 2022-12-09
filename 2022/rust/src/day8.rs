use array2d::Array2D;
use itertools::Itertools;

const RADIX: u32 = 10;

#[aoc_generator(day8)]
fn generator_input(input: &str) -> Array2D<u8> {
    let nums: Vec<u8> = input
        .lines()
        .flat_map(|line| line.chars().map(|c| c.to_digit(RADIX).unwrap() as u8))
        .collect();
    let grid_sz = (nums.len() as f64).sqrt();
    Array2D::from_row_major(&nums, grid_sz as usize, grid_sz as usize)
}

fn visible(grid: &Array2D<u8>, pos: (usize, usize)) -> bool {
    let row: Vec<u8> = grid.row_iter(pos.0).cloned().collect();
    let col: Vec<u8> = grid.column_iter(pos.1).cloned().collect();
    row[0..pos.1].iter().all(|&c| c < grid[pos])
        || row[pos.1 + 1..].iter().all(|&c| c < grid[pos])
        || col[0..pos.0].iter().all(|&c| c < grid[pos])
        || col[pos.0 + 1..].iter().all(|&c| c < grid[pos])
}

fn left(row: &[u8], cell: u8) -> usize {
    row.iter()
        .rev()
        .find_position(|&c| *c >= cell)
        .map(|(idx, _)| idx + 1)
        .unwrap_or(row.len())
}

fn right(row: &[u8], cell: u8) -> usize {
    row.iter()
        .find_position(|&c| *c >= cell)
        .map(|(idx, _)| idx + 1)
        .unwrap_or(row.len())
}

fn scenic_score(grid: &Array2D<u8>, pos: (usize, usize)) -> usize {
    let row: Vec<u8> = grid.row_iter(pos.0).cloned().collect();
    let col: Vec<u8> = grid.column_iter(pos.1).cloned().collect();
    left(&row[0..pos.1], grid[pos])
        * right(&row[pos.1 + 1..], grid[pos])
        * left(&col[0..pos.0], grid[pos])
        * right(&col[pos.0 + 1..], grid[pos])
}

#[aoc(day8, part1)]
fn part1(grid: &Array2D<u8>) -> usize {
    (0..grid.num_rows())
        .cartesian_product(0..grid.num_rows())
        .filter(|&(x, y)| visible(grid, (x, y)))
        .count()
}

#[aoc(day8, part2)]
fn part2(grid: &Array2D<u8>) -> usize {
    (0..grid.num_rows())
        .cartesian_product(0..grid.num_rows())
        .map(|(x, y)| scenic_score(grid, (x, y)))
        .max()
        .unwrap()
}

#[cfg(test)]
mod tests {
    use crate::day8::*;

    const TEST_INPUT: &str = "30373
25512
65332
33549
35390";

    #[test]
    fn test_generator_input() {
        let res = generator_input(&TEST_INPUT);
        println!("{:?}", res);
        assert_eq!(res[(0, 0)], 3);
        assert_eq!(res[(1, 0)], 2);
        assert_eq!(res[(2, 0)], 6);
        assert_eq!(res[(0, 1)], 0);
        assert_eq!(res[(0, 2)], 3);
        assert_eq!(res[(1, 3)], 1);
        assert_eq!(res[(3, 1)], 3);
        assert_eq!(res[(3, 2)], 5);
        assert_eq!(res[(3, 3)], 4);
    }

    #[test]
    fn test_visible() {
        let res = generator_input(&TEST_INPUT);
        assert_eq!(true, visible(&res, (0, 0)));
        assert_eq!(true, visible(&res, (0, 1)));
        assert_eq!(true, visible(&res, (0, 2)));
        assert_eq!(true, visible(&res, (0, 3)));
        assert_eq!(true, visible(&res, (0, 4)));
        assert_eq!(true, visible(&res, (1, 0)));
        assert_eq!(true, visible(&res, (1, 1)));
        assert_eq!(true, visible(&res, (1, 2)));
        assert_eq!(false, visible(&res, (1, 3)));
        assert_eq!(true, visible(&res, (1, 4)));
        assert_eq!(true, visible(&res, (2, 1)));
        assert_eq!(false, visible(&res, (2, 2)));
        assert_eq!(false, visible(&res, (3, 1)));
        assert_eq!(true, visible(&res, (3, 2)));
        assert_eq!(false, visible(&res, (3, 3)));
        assert_eq!(21, part1(&res));
    }

    #[test]
    fn test_scenic_score() {
        let res = generator_input(&TEST_INPUT);
        assert_eq!(scenic_score(&res, (1, 2)), 4);
        assert_eq!(scenic_score(&res, (3, 2)), 8);
        assert_eq!(part2(&res), 8);
        let row: Vec<u8> = res.row_iter(1).cloned().collect();
        let col: Vec<u8> = res.column_iter(2).cloned().collect();
        assert_eq!(left(&row[0..2], 5), 1);
        assert_eq!(right(&row[3..], 5), 2);
        assert_eq!(left(&col[0..1], 5), 1);
        assert_eq!(right(&col[2..], 5), 2);
        let row: Vec<u8> = res.row_iter(3).cloned().collect();
        let col: Vec<u8> = res.column_iter(2).cloned().collect();
        assert_eq!(left(&row[0..2], 5), 2);
        assert_eq!(right(&row[3..], 5), 2);
        assert_eq!(left(&col[0..3], 5), 2);
        assert_eq!(right(&col[4..], 5), 1);
        assert_eq!(8, part2(&res));
    }
}
