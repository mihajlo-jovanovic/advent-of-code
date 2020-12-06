#[derive(Debug, Copy, Clone)]
pub struct BoardingPass {
    row: i32,
    col: i32,
    seat_id: i32,
}

#[aoc_generator(day5)]
fn generator_input(input: &str) -> Vec<BoardingPass> {
    input
        .lines()
        .map(|b| {
            let row_str = &b[..7].replace("B", "1").replace("F", "0");
            let row = i32::from_str_radix(row_str, 2).unwrap();
            let col_str = &b[7..].replace("R", "1").replace("L", "0");
            let col = i32::from_str_radix(col_str, 2).unwrap();
            BoardingPass {
                row,
                col,
                seat_id: row * 8 + col,
            }
        })
        .collect()
}

#[aoc(day5, part1)]
fn part1(input: &[BoardingPass]) -> i32 {
    match input.iter().max_by_key(|b| b.seat_id) {
        Some(b) => b.seat_id,
        None => panic!("Something went wrong"),
    }
}
