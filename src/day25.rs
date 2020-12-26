#[aoc_generator(day25)]
fn parse_input(input: &str) -> (u64, u64) {
    let mut lines = input.lines();
    (
        lines.next().unwrap().parse::<u64>().unwrap(),
        lines.next().unwrap().parse::<u64>().unwrap(),
    )
}

#[aoc(day25, part1)]
fn part1(input: &(u64, u64)) -> u64 {
    let subject_num = 7;
    xform_subject_num(input.1, calc_loop_sz(input.0, subject_num))
}

fn calc_loop_sz(pk: u64, subject_num: u64) -> u32 {
    let mut val: u64 = 1;
    let mut i = 0;
    loop {
        i += 1;
        val *= subject_num;
        val %= 20201227;
        if val == pk {
            return i;
        }
    }
}

fn xform_subject_num(subject_num: u64, loop_sz: u32) -> u64 {
    let mut val: u64 = 1;
    for _ in 0..loop_sz {
        val *= subject_num;
        val %= 20201227;
    }
    val
}

#[test]
fn test_xfrom_subject_num() {
    assert_eq!(5764801, xform_subject_num(7, 8));
    assert_eq!(17807724, xform_subject_num(7, 11));
    assert_eq!(14897079, xform_subject_num(17807724, 8));
    assert_eq!(14897079, xform_subject_num(5764801, 11));
}

#[test]
fn test_calc_loop_sz() {
    assert_eq!(8, calc_loop_sz(5764801, 7));
    assert_eq!(11, calc_loop_sz(17807724, 7));
    assert_eq!(14897079, part1(&(5764801, 17807724)));
}
