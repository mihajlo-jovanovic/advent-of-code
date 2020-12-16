use std::collections::HashMap;

#[aoc_generator(day14, part1)]
fn parse_input_part1(input: &str) -> HashMap<u64, u64> {
    let mut mem: HashMap<u64, u64> = HashMap::new();
    let mut bitmask: (u64, u64) = (0, 0);
    for l in input.lines() {
        if &l[..4] == "mask" {
            bitmask = parse_bitmask(&l[7..]);
        } else {
            let k = l[4..l.find(']').unwrap()].parse::<u64>().unwrap();
            let v = l[l.find('=').unwrap() + 2..].parse::<u64>().unwrap();
            mem.insert(k, apply_bitmask(v, bitmask));
        }
    }
    mem
}

#[aoc_generator(day14, part2)]
fn parse_input_part2(input: &str) -> HashMap<u64, u64> {
    let mut mem: HashMap<u64, u64> = HashMap::new();
    let mut bitmask: &str = "";
    for l in input.lines() {
        if &l[..4] == "mask" {
            bitmask = &l[7..];
        } else {
            let k = l[4..l.find(']').unwrap()].parse::<u64>().unwrap();
            let v = l[l.find('=').unwrap() + 2..].parse::<u64>().unwrap();
            let addresses = memory_addresses(k, bitmask);
            for addr in addresses {
                mem.insert(addr, v);
            }
        }
    }
    mem
}

fn apply_bitmask(val: u64, mask: (u64, u64)) -> u64 {
    (val & mask.0) | mask.1
}

fn memory_addresses(addr: u64, mask: &str) -> Vec<u64> {
    let mut addresses = Vec::new();
    let floating_bits: Vec<u64> = mask
        .match_indices('X')
        .map(|(x, _)| 2u64.pow(35u32 - x as u32))
        .collect();
    let bitmask = u64::from_str_radix(&mask.replace("X", "0"), 2).unwrap();
    let base_addr: u64 =
        addr & u64::from_str_radix(&mask.replace("0", "1").replace("X", "0"), 2).unwrap() | bitmask;
    for i in powerset(&floating_bits)
        .iter()
        .map(move |s| s.iter().sum::<u64>())
    {
        addresses.push(base_addr + i);
    }
    addresses
}

// 'borrowed' from https://stackoverflow.com/questions/40718975/how-to-get-every-subset-of-a-vector-in-rust
fn powerset<T>(s: &[T]) -> Vec<Vec<T>>
where
    T: Clone,
{
    (0..2usize.pow(s.len() as u32))
        .map(|i| {
            s.iter()
                .enumerate()
                .filter(|&(t, _)| (i >> t) % 2 == 1)
                .map(|(_, element)| element.clone())
                .collect()
        })
        .collect()
}

#[aoc(day14, part1)]
fn part1(input: &HashMap<u64, u64>) -> u64 {
    input.values().sum()
}

#[aoc(day14, part2)]
fn part2(input: &HashMap<u64, u64>) -> u64 {
    input.values().sum()
}

fn parse_bitmask(bitmask: &str) -> (u64, u64) {
    (
        u64::from_str_radix(&bitmask.replace("1", "0").replace("X", "1"), 2).unwrap(),
        u64::from_str_radix(&bitmask.replace("X", "0"), 2).unwrap(),
    )
}

#[cfg(test)]
mod tests {
    use super::{
        apply_bitmask, memory_addresses, parse_bitmask, parse_input_part1, parse_input_part2,
        part1, part2,
    };
    use std::collections::HashSet;
    use std::iter::FromIterator;

    #[test]
    fn test_bitmask() {
        let b = 0b1011;
        assert_eq!(11, b);
        let bitmask = parse_bitmask("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X");
        assert_eq!(73, apply_bitmask(b, bitmask));
        assert_eq!(101, apply_bitmask(101, bitmask));
        assert_eq!(64, apply_bitmask(0, bitmask));
    }

    #[test]
    fn test_part1() {
        assert_eq!(
            165,
            part1(&parse_input_part1(
                "mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
mem[8] = 11
mem[7] = 101
mem[8] = 0"
            ))
        );
    }

    #[test]
    fn test_part2() {
        assert_eq!(
            208,
            part2(&parse_input_part2(
                "mask = 000000000000000000000000000000X1001X
mem[42] = 100
mask = 00000000000000000000000000000000X0XX
mem[26] = 1"
            ))
        );
    }

    #[test]
    fn test_memory_addresses() {
        let addresses = memory_addresses(26, "00000000000000000000000000000000X0XX");
        //println!("{:#?}", addresses);
        let expected: HashSet<u64> = [16, 17, 18, 19, 24, 25, 26, 27].iter().cloned().collect();
        assert_eq!(expected, HashSet::from_iter(addresses));

        let addresses = memory_addresses(42, "000000000000000000000000000000X1001X");
        let expected: HashSet<u64> = [26, 27, 58, 59].iter().cloned().collect();
        assert_eq!(expected, HashSet::from_iter(addresses));
    }
}
