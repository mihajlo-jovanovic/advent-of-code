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
    use super::{parse_input, part1, Field};
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
}
