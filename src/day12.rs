#[aoc_generator(day12)]
fn parse_input(input: &str) -> Vec<(char, i32)> {
    input
        .lines()
        .map(|l| (l[..1].parse().unwrap(), l[1..].parse().unwrap()))
        .collect()
}

#[aoc(day12, part1)]
fn part1(input: &[(char, i32)]) -> i32 {
    let mut direction: char = 'E';
    let rel: Vec<(char, i32)> = input
        .iter()
        .filter(|(c, _)| matches!(c, 'R' | 'L' | 'F' | 'B'))
        .map(|(c, n)| match c {
            'R' => {
                direction = rotate(direction, *n as u16, *c);
                ('N', 0)
            }
            'L' => {
                direction = rotate(direction, *n as u16, *c);
                ('N', 0)
            }
            'F' => (direction, *n),
            'B' => match direction {
                'E' => ('W', *n),
                'W' => ('E', *n),
                'S' => ('N', *n),
                'N' => ('S', *n),
                _ => panic!(""),
            },
            _ => panic!("Not suppose to happen"),
        })
        .chain(
            input
                .iter()
                .filter(|(c, _)| matches!(c, 'N' | 'S' | 'E' | 'W'))
                .cloned(),
        )
        .collect();
    manhattan_dist(&rel)
}

fn rotate(dir: char, deg: u16, action: char) -> char {
    match (action, dir) {
        ('R', 'E') if deg == 90 => 'S',
        ('R', 'E') if deg == 180 => 'W',
        ('R', 'E') if deg == 270 => 'N',
        ('L', 'E') if deg == 90 => 'N',
        ('L', 'E') if deg == 180 => 'W',
        ('L', 'E') if deg == 270 => 'S',

        ('R', 'W') if deg == 90 => 'N',
        ('R', 'W') if deg == 180 => 'E',
        ('R', 'W') if deg == 270 => 'S',
        ('L', 'W') if deg == 90 => 'S',
        ('L', 'W') if deg == 180 => 'E',
        ('L', 'W') if deg == 270 => 'N',

        ('R', 'S') if deg == 90 => 'W',
        ('R', 'S') if deg == 180 => 'N',
        ('R', 'S') if deg == 270 => 'E',
        ('L', 'S') if deg == 90 => 'E',
        ('L', 'S') if deg == 180 => 'N',
        ('L', 'S') if deg == 270 => 'W',

        ('R', 'N') if deg == 90 => 'E',
        ('R', 'N') if deg == 180 => 'S',
        ('R', 'N') if deg == 270 => 'W',
        ('L', 'N') if deg == 90 => 'W',
        ('L', 'N') if deg == 180 => 'S',
        ('L', 'N') if deg == 270 => 'E',
        _ => 'W',
    }
}

fn manhattan_dist(input: &[(char, i32)]) -> i32 {
    let res = input
        .iter()
        .map(move |i| match i.0 {
            'N' => (i.1, 0),
            'S' => (-i.1, 0),
            'E' => (0, i.1),
            'W' => (0, -i.1),
            _ => (0, 0),
        })
        .fold((0, 0), |acc, x| (acc.0 + x.0, acc.1 + x.1));
    res.0.abs() + res.1.abs()
}

#[test]
fn test_manhattan_distance() {
    let test_input = [('F', 10), ('N', 3), ('F', 7), ('R', 90), ('F', 11)];
    assert_eq!(25, part1(&test_input));
}

#[test]
fn test_rotation() {
    assert_eq!('S', rotate('E', 90, 'R'));
}
