#[aoc_generator(day13, part1)]
fn parse_input(input: &str) -> (u32, Vec<u32>) {
    let mut ln = input.lines();
    (
        ln.next().unwrap().parse::<u32>().unwrap(),
        ln.next()
            .unwrap()
            .split(',')
            .filter(|id| !id.starts_with('x'))
            .map(|id| id.parse::<u32>().unwrap())
            .collect(),
    )
}

#[aoc_generator(day13, part2)]
fn parse_input_part2(input: &str) -> Vec<(u64, u8)> {
    let mut ln = input.lines();
    ln.next();
    ln.next()
        .unwrap()
        .split(',')
        .enumerate()
        .filter(|&(_, id)| id != "x")
        .map(|s| (s.1.parse::<u64>().unwrap(), s.0 as u8))
        .collect()
}

#[aoc(day13, part1)]
fn part1(input: &(u32, Vec<u32>)) -> usize {
    input
        .1
        .iter()
        .map(|i| offset(*i as u64, input.0 as u64))
        .min()
        .unwrap()
}

#[aoc(day13, part2)]
fn part2(input: &[(u64, u8)]) -> u64 {
    (1..)
        .filter(|i: &u64| i % input[0].0 == 0)
        .find(|i: &u64| validate(input, *i))
        .unwrap()
}

fn validate(input: &[(u64, u8)], ts: u64) -> bool {
    for i in input {
        if offset(i.0, ts) != i.1 as usize {
            return false;
        }
    }
    true
}

fn offset(bus_id: u64, ts: u64) -> usize {
    ((bus_id - ts % bus_id) % bus_id) as usize
}

#[test]
fn test_parsing() {
    let input = parse_input(
        "939
7,13,x,x,59,x,31,19",
    );
    println!("earliest: {:?}", input.0);
    for i in &input.1 {
        println!("{:?}", i - input.0 % i);
    }
}

#[test]
fn test_part2() {
    assert_eq!(3417, part2(&[(17, 0), (13, 2), (19, 3)]));
    assert_eq!(
        1068781,
        part2(&[(7, 0), (13, 1), (59, 4), (31, 6), (19, 7)])
    );
    assert_eq!(754018, part2(&[(67, 0), (7, 1), (59, 2), (61, 3)]));
    let input = parse_input_part2(
        "XYZ,
67,x,7,59,61",
    );
    assert_eq!(779210, part2(&input));
    // too slow!
    //     let input = parse_input_part2(
//         "XYZ,
// 1789,37,47,1889",
//     );
    //assert_eq!(1202161486, part2(&input));
}

#[test]
fn test_validate() {
    assert_eq!(0, offset(7, 1068781));
    assert_eq!(1, offset(13, 1068781));
    assert_eq!(4, offset(59, 1068781));
    assert_eq!(6, offset(31, 1068781));
    assert_eq!(7, offset(19, 1068781));
    assert!(validate(
        &[(7, 0), (13, 1), (59, 4), (31, 6), (19, 7)],
        1068781
    ))
}
