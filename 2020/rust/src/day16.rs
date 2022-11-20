#[aoc_generator(day16)]
fn parse_input(input: &str) -> (Vec<Field>, Vec<Vec<u32>>) {
    let mut groups = input.split("\n\n");
    if let Some(fields) = groups.next() {
        let fields: Vec<Field> = fields.lines().map(|l| Field::new(l)).collect();
        if let Some(tics) = groups.nth(1) {
            let tickets: Vec<Vec<u32>> = tics
                .lines()
                .skip(1)
                .map(|l| l.split(',').map(|n| n.parse::<u32>().unwrap()).collect())
                .collect();
            (fields, tickets)
        } else {
            panic!("Invalid input file format")
        }
    } else {
        panic!("Invalid input file format")
    }
}

#[aoc(day16, part1)]
fn part1(input: &(Vec<Field>, Vec<Vec<u32>>)) -> u32 {
    input
        .1
        .iter()
        .flat_map(|tic| {
            tic.iter().filter(|t| {
                for f in &input.0 {
                    if f.is_valid(**t) {
                        return false;
                    }
                }
                true
            })
        })
        .sum()
}

#[aoc(day16, part2)]
fn part2(input: &(Vec<Field>, Vec<Vec<u32>>)) -> u64 {
    // println!("all tickets: {}", input.1.len());
    let mut valid_tickets: Vec<Vec<u32>> = input
        .1
        .iter()
        .filter(|t| {
            t.iter().all(|val| {
                for f in &input.0 {
                    if f.is_valid(*val) {
                        return true;
                    }
                }
                false
            })
        })
        .cloned()
        .collect();
    let my_ticket = vec![
        107, 109, 163, 127, 167, 157, 139, 67, 131, 59, 151, 53, 73, 83, 61, 89, 71, 149, 79, 137,
    ];
    valid_tickets.push(my_ticket.clone());
    // println!("valid tickets: {}", valid_tickets.len());
    // for field in &input.0 {
    //     let possible_positions: Vec<i32> = (0..20)
    //         .filter(|pos| {
    //             for t in &valid_tickets {
    //                 if !field.is_valid(t[*pos as usize]) {
    //                     return false;
    //                 }
    //             }
    //             true
    //         })
    //         .collect();
    //     if possible_positions.len() == 1 {
    //         println!(
    //             "field name: {} num of valid positions: {}",
    //             field.name,
    //             possible_positions.len()
    //         );
    //         println!("{:?}", possible_positions);
    //     }
    // }
    // a hack to avoid having to re-write this and avoid factorial complexity of backtracking
    // solution; got this by running the commented out code above
    let mut acc: Vec<(&str, usize)> = vec![
        ("zone", 7),
        ("arrival station", 8),
        ("train", 1),
        ("arrival location", 4),
        ("seat", 3),
    ];
    if let Some(field_order) = match_fields_to_pos_recur(&input.0, &valid_tickets, &mut acc) {
        // println!("{:?}", field_order);
        field_order
            .iter()
            .filter(|(field, _)| field.starts_with("departure"))
            .map(|(_, i)| my_ticket[*i] as u64)
            .product()
    } else {
        panic!("Could not find solution")
    }
}

fn match_fields_to_pos<'a>(
    fields: &'a [Field],
    tickets: &[Vec<u32>],
) -> Option<Vec<(&'a str, usize)>> {
    match_fields_to_pos_recur(fields, tickets, &mut Vec::new())
}

fn match_fields_to_pos_recur<'a>(
    fields: &'a [Field],
    tickets: &[Vec<u32>],
    acc: &mut Vec<(&'a str, usize)>,
) -> Option<Vec<(&'a str, usize)>> {
    if fields.is_empty() {
        return Some(acc.clone());
    }
    //let field = &fields[0];
    // again, part of `the hack`; this would not be necessary on a smaller size problem (as we're
    // simply walking fields sequentially, one by one
    if let Some(field) = fields.iter().find(|f| {
        for (f2, _) in acc.iter() {
            if *f2 == f.name.as_str() {
                return false;
            }
        }
        true
    }) {
        let avail_positions: Vec<usize> = (0..tickets[0].len())
            .filter(|pos| !acc.iter().any(|val| val.1 == *pos))
            .collect();
        for pos in avail_positions {
            if is_valid_order_for_field(field, pos, tickets) {
                acc.push((&field.name, pos));
                if let Some(field_mapping) = match_fields_to_pos_recur(&fields[1..], tickets, acc) {
                    return Some(field_mapping);
                }
                acc.pop();
            }
        }
        None
    } else {
        Some(acc.clone())
    }
}

fn is_valid_order_for_field(f: &Field, pos: usize, tickets: &[Vec<u32>]) -> bool {
    for ticket in tickets {
        if !f.is_valid(ticket[pos]) {
            return false;
        }
    }
    true
}

#[derive(Debug)]
struct Field {
    name: String,
    valid_range_1: (u32, u32),
    valid_range_2: (u32, u32),
}

impl Field {
    fn new(f: &str) -> Field {
        if let Some(colon) = f.find(':') {
            let name: &str = &f[0..colon];
            let ranges: Vec<(u32, u32)> = f[colon + 2..]
                .split(" or ")
                .map(|s| {
                    if let Some(hyphen) = s.find('-') {
                        let valid_range: (u32, u32) = (
                            s[0..hyphen].parse().unwrap(),
                            s[hyphen + 1..].parse().unwrap(),
                        );
                        valid_range
                    } else {
                        panic!("Invalid field range")
                    }
                })
                .collect();
            Field {
                name: name.to_string(),
                valid_range_1: ranges[0],
                valid_range_2: ranges[1],
            }
        } else {
            panic!("Invalid field format")
        }
    }

    fn is_valid(&self, val: u32) -> bool {
        val >= self.valid_range_1.0 && val <= self.valid_range_1.1
            || val >= self.valid_range_2.0 && val <= self.valid_range_2.1
    }
}

#[cfg(test)]
mod tests {
    use super::{match_fields_to_pos, parse_input, part1, part2, Field};
    use std::fs;

    #[test]
    fn test_part1() {
        let input =
            fs::read_to_string("input/2020/day16_sample_input.txt").expect("Could not read file");
        let test_input = parse_input(input.as_str());
        assert_eq!(71, part1(&test_input));
    }

    #[test]
    fn playing_with_fields() {
        let f1 = Field::new("departure location: 49-627 or 650-970");
        assert_eq!("departure location", f1.name);
        assert_eq!((49, 627), f1.valid_range_1);
        println!("{:?}", f1);
        assert!(f1.is_valid(49));
        assert!(f1.is_valid(50));
        assert!(f1.is_valid(970));
        assert!(!f1.is_valid(971));
    }

    #[test]
    fn test_field_matching() {
        let input = fs::read_to_string("input/2020/day16_sample_input_part2.txt")
            .expect("Could not read file");
        let test_input = parse_input(input.as_str());
        if let Some(field_order) = match_fields_to_pos(&test_input.0, &test_input.1) {
            println!("{:?}", field_order);
            assert_eq!(vec![("class", 1), ("row", 0), ("seat", 2)], field_order);
        } else {
            assert!(false);
        }
    }

    #[test]
    fn test_part2() {
        let input = fs::read_to_string("input/2020/day16.txt").expect("Could not read file");
        let test_input = parse_input(input.as_str());
        assert_eq!(3173135507987, part2(&test_input));
    }
}
