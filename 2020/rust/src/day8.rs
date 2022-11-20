#[aoc_generator(day8)]
fn generator_input(input: &str) -> Vec<(String, i32)> {
    input
        .lines()
        .map(|l| {
            let instruction: Vec<&str> = l.split(' ').collect();
            (
                instruction[0].to_string(),
                instruction[1].parse::<i32>().unwrap(),
            )
        })
        .collect()
}

#[aoc(day8, part1)]
fn part1(input: &[(String, i32)]) -> i32 {
    let mut acc: i32 = 0;
    let mut visited: Vec<i32> = Vec::new();
    let mut pos: i32 = 0;
    while (pos as usize) < input.len() {
        if visited.contains(&pos) {
            return acc;
        }
        visited.push(pos);
        let (op, arg) = &input[pos as usize];
        match &op[..] {
            "acc" => {
                pos += 1;
                acc += arg;
            }
            "jmp" => {
                pos += arg;
            }
            _ => pos += 1,
        }
    }
    acc
}

#[aoc(day8, part2)]
fn part2(input: &[(String, i32)]) -> i32 {
    for (i, p) in input.iter().enumerate() {
        if p.0 == "jmp" || p.0 == "nop" {
            let pr = change_create_new(input, i);
            if check_if_teminates(&pr) {
                return part1(&pr);
            }
        }
    }
    -1
}

fn change_create_new(vec: &[(String, i32)], i: usize) -> Vec<(String, i32)> {
    let mut newvec = vec.to_vec();
    let (op, arg) = &vec[i];
    match &op[..] {
        "jmp" => {
            newvec[i] = (String::from("nop"), *arg);
        }
        "nop" => {
            newvec[i] = (String::from("jmp"), *arg);
        }
        _ => panic!("Ops something went wrond"),
    }
    newvec
}

fn check_if_teminates(input: &[(String, i32)]) -> bool {
    let mut visited: Vec<i32> = Vec::new();
    let mut pos: i32 = 0;
    while (pos as usize) < input.len() {
        if visited.contains(&pos) {
            return false;
        }
        visited.push(pos);
        let (op, arg) = &input[pos as usize];
        match &op[..] {
            "jmp" => {
                pos += arg;
            }
            _ => pos += 1,
        }
    }
    true
}

#[test]
fn test_termination() {
    let input = generator_input(
        "nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6",
    );
    assert!(!check_if_teminates(&input));
    let correct = generator_input(
        "nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
nop -4
acc +6",
    );
    assert!(check_if_teminates(&correct));
    assert_eq!(8, part1(&correct));
}

#[test]
fn test_change_instruction() {
    let input = generator_input(
        "nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6",
    );
    let result = change_create_new(&input, 7);
    assert!(check_if_teminates(&result));
    assert_eq!(8, part1(&result));
}
