#[aoc_generator(day5)]
pub fn input_generator(input: &str) -> Vec<isize> {
    input
        .lines()
        .map(|line| line.parse::<isize>().expect("cannot parse number"))
        .collect()
}

#[aoc(day5, part1)]
fn part1(l: &[isize]) -> usize {
    let l2: &mut [isize; 5] = &mut [42; 5];
    l2.copy_from_slice(&l[..5]);
    let mut i = 0;
    let mut num_of_steps = 0;
    loop {
        num_of_steps += 1;
        i = jump(l2, i);
        if !(0..=4).contains(&i) {
            break;
        }
    }
    num_of_steps
}

fn jump(l: &mut [isize; 5], pos: isize) -> isize {
    let offset = l[pos as usize];
    l[pos as usize] += 1;
    pos + offset
}

fn jump2(l: &mut [isize; 1037], pos: isize) -> isize {
    let offset = l[pos as usize];
    if offset >= 3 {
        l[pos as usize] -= 1;
    } else {
        l[pos as usize] += 1;
    }
    pos + offset
}
#[aoc(day5, part2)]
fn part2(l: &[isize]) -> usize {
    let l2: &mut [isize; 1037] = &mut [42; 1037];
    l2.copy_from_slice(&l[..1037]);
    let mut i = 0;
    let mut num_of_steps = 0;
    loop {
        num_of_steps += 1;
        i = jump2(l2, i);
        if !(0..=1036).contains(&i) {
            break;
        }
    }
    num_of_steps
}

#[test]
fn test_jump() {
    let input = &mut [0, 3, 0, 1, -3];
    assert_eq!(jump(input, 0), 0);
    assert_eq!(input[0], 1);
    // write test case
    assert_eq!(jump(input, 0), 1);
    assert_eq!(input[0], 2);
    assert_eq!(jump(input, 1), 4);
    assert_eq!(input[1], 4);
    assert_eq!(jump(input, 4), 1);
    assert_eq!(input[4], -2);
    assert_eq!(jump(input, 1), 5);
    assert_eq!(part1([0, 3, 0, 1, -3].as_ref()), 5);
}
