use std::collections::HashMap;
use std::str::FromStr;

#[aoc_generator(day4)]
fn generator_input(input: &str) -> Vec<HashMap<String, String>> {
    input
        .split("\n\n")
        .map(|p| {
            let tmp = p.replace('\n', " ");
            let mut passport_data: HashMap<String, String> = HashMap::new();
            for kv in tmp.split(' ') {
                let tokens = kv.split(':').collect::<Vec<&str>>();
                passport_data.insert(tokens[0].to_string(), tokens[1].to_string());
            }
            passport_data
        })
        .collect()
}

#[aoc(day4, part1)]
fn part1(input: &[HashMap<String, String>]) -> usize {
    input
        .iter()
        .filter(|p| p.keys().len() == 8 || (p.keys().len() == 7 && !p.contains_key("cid")))
        .count()
}

#[aoc(day4, part2)]
fn part2(input: &[HashMap<String, String>]) -> usize {
    input
        .iter()
        .filter(|p| p.keys().len() == 8 || (p.keys().len() == 7 && !p.contains_key("cid")))
        .filter(|p| is_valid_passport(p))
        .count()
}

fn is_valid_passport(passport: &HashMap<String, String>) -> bool {
    for (k, v) in passport {
        if k == "byr" && !is_valid_year(&v, 1920, 2002) {
            return false;
        }
        if k == "iyr" && !is_valid_year(&v, 2010, 2020) {
            return false;
        }
        if k == "eyr" && !is_valid_year(&v, 2020, 2030) {
            return false;
        }
        if k == "hgt" && !is_valid_hgt(&v) {
            return false;
        }
        if k == "hcl" && !is_valid_hcl(&v) {
            return false;
        }
        if k == "ecl" && !is_valid_ecl(&v) {
            return false;
        }
        if k == "pid" && !is_valid_pid(&v) {
            return false;
        }
    }
    true
}

fn is_valid_year(yr: &str, min: u16, max: u16) -> bool {
    match yr.parse::<u16>() {
        Ok(y) => y >= min && y <= max,
        Err(_) => false,
    }
}

fn is_valid_hgt(hgt: &str) -> bool {
    let len = hgt.len();
    let num: String = hgt.chars().take(len - 2).collect();
    if let Ok(n) = num.parse::<u8>() {
        let unit: String = hgt.chars().skip(len - 2).collect();
        return (unit == "in" && n > 58 && n < 77) || (unit == "cm" && n > 149 && n < 194);
    }
    false
}

#[derive(Debug, PartialEq)]
struct RGB {
    r: u8,
    g: u8,
    b: u8,
}

impl FromStr for RGB {
    type Err = std::num::ParseIntError;

    // Parses a color hex code of the form '#rRgGbB..' into an
    // instance of 'RGB'
    fn from_str(hex_code: &str) -> Result<Self, Self::Err> {
        // u8::from_str_radix(src: &str, radix: u32) converts a string
        // slice in a given base to u8
        let r: u8 = u8::from_str_radix(&hex_code[1..3], 16)?;
        let g: u8 = u8::from_str_radix(&hex_code[3..5], 16)?;
        let b: u8 = u8::from_str_radix(&hex_code[5..7], 16)?;

        Ok(RGB { r, g, b })
    }
}

fn is_valid_hcl(hcl: &str) -> bool {
    hcl.len() == 7 && RGB::from_str(hcl).is_ok()
}

fn is_valid_ecl(ecl: &str) -> bool {
    matches!(ecl, "amb" | "blu" | "brn" | "gry" | "grn" | "hzl" | "oth")
}

fn is_valid_pid(pid: &str) -> bool {
    pid.len() == 9 && pid.parse::<u32>().is_ok()
}

#[test]
fn test_generator() {
    let passports = generator_input(
        "ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm

iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
hcl:#cfa07d byr:1929

hcl:#ae17e1 iyr:2013
eyr:2024
ecl:brn pid:760753108 byr:1931
hgt:179cm

hcl:#cfa07d eyr:2025 pid:166559648
iyr:2011 ecl:brn hgt:59in",
    );
    assert_eq!(4, passports.len());
}

#[test]
fn test_year_validation() {
    assert!(is_valid_year("2002", 1920, 2002));
    assert_eq!(false, is_valid_year("2003", 1920, 2002));
}

#[test]
fn test_hgt_validation() {
    assert_eq!(false, is_valid_hgt("77cm"));
    assert!(is_valid_hgt("150cm"));
    assert!(is_valid_hgt("76in"));
    assert_eq!(false, is_valid_hgt("77in"));
}

#[test]
fn test_hair_color_validation() {
    assert!(is_valid_hcl("#ffffff"));
    assert!(is_valid_hcl("#abcdef"));
    assert!(is_valid_hcl("#123abc"));
    assert_eq!(false, is_valid_hcl("#123abz"));
    assert_eq!(false, is_valid_hcl("123abc"));
}

#[test]
fn test_eye_color_validation() {
    assert!(is_valid_ecl("brn"));
    assert_eq!(false, is_valid_ecl("wat"));
}

#[test]
fn test_pid_validation() {
    assert!(is_valid_pid("000000001"));
    assert_eq!(false, is_valid_pid("0123456789"));
    assert_eq!(false, is_valid_pid("00000000G"));
}
