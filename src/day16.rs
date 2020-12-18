use std::collections::HashSet;

#[aoc_generator(day16)]
fn parse_input(input: &str) -> (Vec<u32>, HashSet<u32>) {
    let mut groups = input.split("\n\n");
    let fields = groups.next().unwrap();
    //let mut ranges:  = HashSet::new();
    let mut set: HashSet<u32> = HashSet::new();
    for l in fields.lines() {
        let mut beg: u32;
        let mut end: u32;
        for i in l[l.find(": ").unwrap() + 2..].split(" or ") {
            beg = i[0..i.find('-').unwrap()].parse().unwrap();
            end = i[i.find('-').unwrap() + 1..].parse().unwrap();
            for num in beg..end + 1 {
                set.insert(num);
            }
        }
    }

    // let ranges: Vec<u32> = fields.lines().flat_map(|l| {
    //     let mut beg: u32 = 0;
    //     let mut end: u32 = 0;
    //     for i in l[l.find(": ").unwrap()+2..].split(" or ") {
    //         //println!("{:?}", i);
    //         beg = i[0..i.find('-').unwrap()].parse().unwrap();
    //         end = i[i.find('-').unwrap()+1..].parse().unwrap();
    //         println!("{:?} {:?}", beg, end);
    //         //let rng = (beg..end);
    //         //ranges.extend(rng);
    //     }
    //     (beg..end+1)
    //     //1
    // }).collect();
    groups.next();
    let numbers = groups.next().unwrap().lines().skip(1);
    let mut tmp: Vec<u32> = Vec::new();
    for l in numbers {
        println!("{:?}", l);
        for n in l.split(',') {
            tmp.push(n.parse().unwrap());
        }
    }
    (tmp, set)
}

#[aoc(day16, part1)]
fn part1(input: &(Vec<u32>, HashSet<u32>)) -> u32 {
    println!("set is {:#?}", input.1);
    println!(
        "{:#?}",
        input
            .0
            .iter()
            .filter(|n| !input.1.contains(n))
            .cloned()
            .collect::<Vec<u32>>()
    );
    input.0.iter().filter(|n| !input.1.contains(n)).sum()
}

#[test]
fn test_part1() {
    let test_input = parse_input(
        "class: 1-3 or 5-7
row: 6-11 or 33-44
seat: 13-40 or 45-50

your ticket:
7,1,14

nearby tickets:
7,3,47
40,4,50
55,2,20
38,6,12",
    );
    assert_eq!(71, part1(&test_input));
}
