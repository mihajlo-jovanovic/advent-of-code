use array2d::Array2D;

#[aoc_generator(day15)]
fn input_generator(input: &str) -> (Array2D<char>, String) {
    let parts = input.split("\n\n").collect::<Vec<&str>>();
    // Split the input into lines
    let lines: Vec<&str> = parts[0].lines().collect();
    let moves: String = parts[1].lines().collect::<Vec<&str>>().join("");
    //println!("{:?}", moves);

    // Calculate the dimensions
    let rows = lines.len();
    let cols = lines[0].len();

    // Collect all characters in row-major order
    let mut data = Vec::with_capacity(rows * cols);
    for line in &lines {
        for ch in line.chars() {
            data.push(ch);
        }
    }

    // Build an Array2D from the vector of chars
    (Array2D::from_row_major(&data, rows, cols).unwrap(), moves)
}

fn current_position(grid: &Array2D<char>) -> (usize, usize) {
    for row in 0..grid.num_rows() {
        for col in 0..grid.num_columns() {
            if grid[(row, col)] == '@' {
                return (row, col);
            }
        }
    }
    panic!("No current position found");
}

fn move_to_offsets(m: char) -> (i32, i32) {
    match m {
        '^' => (-1, 0),
        'v' => (1, 0),
        '<' => (0, -1),
        '>' => (0, 1),
        _ => panic!("Invalid move: {}", m),
    }
}

fn do_move(grid: &mut Array2D<char>, m: char) {
    let (row, col) = current_position(grid);
    let (drow, dcol) = move_to_offsets(m);
    let (new_row, new_col) = (row as i32 + drow, col as i32 + dcol);

    match grid[(new_row as usize, new_col as usize)] {
        '#' => {
            return;
        }
        'O' => {
            let can_move_box = move_box(grid, &(new_row, new_col), (drow, dcol));
            if !can_move_box {
                return;
            }
        }
        _ => (),
    }
    grid[(row, col)] = '.';
    grid[(new_row as usize, new_col as usize)] = '@';
}

fn move_box(grid: &mut Array2D<char>, pos: &(i32, i32), offset: (i32, i32)) -> bool {
    let (new_row, new_col) = (pos.0 + offset.0, pos.1 + offset.1);
    // check if the new position is a wall
    match grid[(new_row as usize, new_col as usize)] {
        '#' => false,
        'O' => {
            if move_box(grid, &(new_row, new_col), offset) {
                grid[(pos.0 as usize, pos.1 as usize)] = '.';
                grid[(new_row as usize, new_col as usize)] = 'O';
                true
            } else {
                false
            }
        }
        _ => {
            grid[(pos.0 as usize, pos.1 as usize)] = '.';
            grid[(new_row as usize, new_col as usize)] = 'O';
            true
        }
    }
}

fn can_move_box_p2(grid: &Array2D<char>, pos: &(i32, i32), offset: (i32, i32)) -> bool {
    let pos2 = if grid[(pos.0 as usize, pos.1 as usize)] == '[' {
        (pos.0, pos.1 + 1)
    } else {
        (pos.0, pos.1 - 1)
    };

    let new_positions: [(usize, usize); 2] = [
        ((pos.0 + offset.0) as usize, (pos.1 + offset.1) as usize),
        ((pos2.0 + offset.0) as usize, (pos2.1 + offset.1) as usize),
    ];

    for &(nx, ny) in &new_positions {
        if (nx, ny) == (pos2.0 as usize, pos2.1 as usize) {
            continue;
        }
        match grid[(nx, ny)] {
            '#' => return false,
            '[' | ']' => {
                if !can_move_box_p2(grid, &(nx as i32, ny as i32), offset) {
                    return false;
                }
            }
            _ => (),
        }
    }
    true
}

fn move_box_p2(grid: &mut Array2D<char>, pos: &(i32, i32), offset: (i32, i32)) {
    let pos2 = if grid[(pos.0 as usize, pos.1 as usize)] == '[' {
        (pos.0, pos.1 + 1)
    } else {
        (pos.0, pos.1 - 1)
    };

    let new_positions: [(usize, usize); 2] = [
        ((pos.0 + offset.0) as usize, (pos.1 + offset.1) as usize),
        ((pos2.0 + offset.0) as usize, (pos2.1 + offset.1) as usize),
    ];

    for &(nx, ny) in &new_positions {
        if (nx, ny) == (pos2.0 as usize, pos2.1 as usize) {
            continue;
        }
        match grid[(nx, ny)] {
            '#' => panic!("Invalid move"),
            '[' | ']' => {
                move_box_p2(grid, &(nx as i32, ny as i32), offset);
            }
            _ => (),
        }
    }

    grid[(pos.0 as usize, pos.1 as usize)] = '.';
    grid[(pos2.0 as usize, pos2.1 as usize)] = '.';
    if pos2.1 > pos.1 {
        grid[((pos.0 + offset.0) as usize, (pos.1 + offset.1) as usize)] = '[';
        grid[((pos2.0 + offset.0) as usize, (pos2.1 + offset.1) as usize)] = ']';
    } else {
        grid[((pos.0 + offset.0) as usize, (pos.1 + offset.1) as usize)] = ']';
        grid[((pos2.0 + offset.0) as usize, (pos2.1 + offset.1) as usize)] = '[';
    }
}

fn do_move_p2(grid: &mut Array2D<char>, m: char) {
    let (row, col) = current_position(grid);
    let (drow, dcol) = move_to_offsets(m);
    let (new_row, new_col) = (row as i32 + drow, col as i32 + dcol);
    // check if the new position is a wall
    match grid[(new_row as usize, new_col as usize)] {
        '#' => {
            return;
        }
        '[' | ']' => {
            let can_move_box = can_move_box_p2(grid, &(new_row, new_col), (drow, dcol));
            if !can_move_box {
                return;
            }
            move_box_p2(grid, &(new_row, new_col), (drow, dcol));
        }
        _ => (),
    }
    grid[(row, col)] = '.';
    grid[(new_row as usize, new_col as usize)] = '@';
}

fn display_grid(grid: &Array2D<char>) {
    for row_iter in grid.rows_iter() {
        for element in row_iter {
            print!("{} ", element);
        }
        println!();
    }
}

fn simulate(grid: &mut Array2D<char>, moves: &str) {
    for m in moves.chars() {
        //clear the screen
        print!("\x1B[2J\x1B[1;1H");
        do_move(grid, m);
        display_grid(grid);
        std::thread::sleep(std::time::Duration::from_millis(500));
    }
}

fn simulate_p2(grid: &mut Array2D<char>, moves: &str) {
    for m in moves.chars() {
        //clear the screen
        print!("\x1B[2J\x1B[1;1H");
        do_move_p2(grid, m);
        display_grid(grid);
        std::thread::sleep(std::time::Duration::from_millis(200));
    }
}

fn calculate_sum(grid: &Array2D<char>) -> usize {
    let mut sum = 0;
    for (i, row_iter) in grid.rows_iter().enumerate() {
        for (j, element) in row_iter.enumerate() {
            if *element == 'O' {
                sum += i * 100 + j;
            }
        }
    }
    sum
}

fn calculate_sum_p2(grid: &Array2D<char>) -> usize {
    let mut sum = 0;
    for (i, row_iter) in grid.rows_iter().enumerate() {
        for (j, element) in row_iter.enumerate() {
            if *element == '[' {
                sum += i * 100 + j;
            }
        }
    }
    sum
}

fn scale_grid(grid: &Array2D<char>) -> Array2D<char> {
    let mut new_grid = Array2D::filled_with(',', grid.num_rows(), grid.num_columns() * 2);
    for row in 0..grid.num_rows() {
        for col in 0..grid.num_columns() {
            match grid[(row, col)] {
                '#' => {
                    new_grid[(row, col * 2)] = '#';
                    new_grid[(row, col * 2 + 1)] = '#';
                }
                'O' => {
                    new_grid[(row, col * 2)] = '[';
                    new_grid[(row, (col * 2) + 1)] = ']';
                }

                '.' => {
                    new_grid[(row, col * 2)] = '.';
                    new_grid[(row, (col * 2) + 1)] = '.';
                }
                '@' => {
                    new_grid[(row, col * 2)] = '@';
                    new_grid[(row, (col * 2) + 1)] = '.';
                }
                _ => panic!("Invalid character: {}", grid[(row, col)]),
            }
        }
    }
    new_grid
}

#[aoc(day15, part1)]
fn part1(input: &(Array2D<char>, String)) -> i32 {
    let mut grid = input.0.clone();
    //simulate(&mut grid, input.1.as_str());
    for m in input.1.as_str().chars() {
        do_move(&mut grid, m);
    }
    calculate_sum(&grid) as i32
}

#[aoc(day15, part2)]
fn part2(input: &(Array2D<char>, String)) -> i32 {
    let mut new_grid = scale_grid(&input.0);
    //simulate_p2(&mut new_grid, input.1.as_str());
    for m in input.1.as_str().chars() {
        do_move_p2(&mut new_grid, m);
    }
    //display_grid(&new_grid);
    calculate_sum_p2(&new_grid) as i32
}
