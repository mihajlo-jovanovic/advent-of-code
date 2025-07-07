#[aoc_generator(day11)]
fn input_generator(input: &str) -> u32 {
    input.parse().expect("Failed to parse input")
}

fn calc_power_level(x: u32, y: u32, serial: u32) -> i32 {
    let rack_id = x + 10;
    let mut power_level = rack_id * y;
    power_level += serial;
    power_level *= rack_id;
    power_level = (power_level / 100) % 10;
    power_level as i32 - 5
}

fn calc_square_power(x: u32, y: u32, serial: u32) -> i32 {
    let mut power = 0;
    for i in 0..3 {
        for j in 0..3 {
            power += calc_power_level(x + i, y + j, serial);
        }
    }
    power
}

fn find_max_power(serial: u32) -> (u32, u32, i32) {
    let mut max_power = 0;
    let mut max_x = 0;
    let mut max_y = 0;
    for x in 1..=298 {
        for y in 1..=298 {
            let power = calc_square_power(x, y, serial);
            if power > max_power {
                max_power = power;
                max_x = x;
                max_y = y;
            }
        }
    }
    (max_x, max_y, max_power)
}

#[aoc(day11, part1)]
fn solve_part1(input: &u32) -> u32 {
    let result: (u32, u32, i32) = find_max_power(input.clone());
    println!("Max power: {:?}", result);
    1
}

fn calc_square_power_p2(x: u32, y: u32, serial: u32, sz: u32) -> i32 {
    let mut power = 0;
    for i in 0..sz {
        for j in 0..sz {
            power += calc_power_level(x + i, y + j, serial);
        }
    }
    power
}

fn find_max_power_p2(serial: u32) -> (u32, u32, i32, u32) {
    let mut max_power = 0;
    let mut max_x = 0;
    let mut max_y = 0;
    let mut max_size = 0;
    for size in 1..=50 {
        for x in 1..=(300-size+1) {
            for y in 1..=(300-size+1) {
                let power = calc_square_power_p2(x, y, serial, size);
                if power > max_power {
                    max_power = power;
                    max_x = x;
                    max_y = y;
                    max_size = size;
                }
            }
        }
        //println!("Size: {:?}, Max power: {:?}", size, max_power);
    }
    (max_x, max_y, max_power, max_size)
}

#[aoc(day11, part2)]
fn solve_part2(input: &u32) -> u32 {
    let result: (u32, u32, i32, u32) = find_max_power_p2(input.clone());
    println!("Max power: {:?}", result);
    1
}
#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_calc_power_level() {
        assert_eq!(calc_power_level(3, 5, 8), 4);
        assert_eq!(calc_power_level(122, 79, 57), -5);
        assert_eq!(calc_power_level(217, 196, 39), 0);
        assert_eq!(calc_power_level(101, 153, 71), 4);
    }
}
