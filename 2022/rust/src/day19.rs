use lazy_static::lazy_static;
use regex::Regex;
use std::cmp::max;
use std::collections::HashMap;

#[derive(Debug)]
struct Blueprint {
    id: u8,
    ore_cost: u8,
    clay_cost: u8,
    obsidian_cost: u8,
    obsidian_clay_cost: u8,
    geode_cost: u8,
    geode_obsidian_cost: u8,
}

impl Blueprint {
    pub fn new(
        id: u8,
        ore_cost: u8,
        clay_cost: u8,
        obsidian_cost: u8,
        obsidian_clay_cost: u8,
        geode_cost: u8,
        geode_obsidian_cost: u8,
    ) -> Self {
        Self {
            id,
            ore_cost,
            clay_cost,
            obsidian_cost,
            obsidian_clay_cost,
            geode_cost,
            geode_obsidian_cost,
        }
    }
}

fn encode_state(
    ore: u8,
    clay: u8,
    obsidian: u8,
    geodes: u8,
    ore_robots: u8,
    clay_robots: u8,
    obsidian_robots: u8,
    geode_robots: u8,
) -> u64 {
    (ore as u64) << 48
        | (clay as u64) << 32
        | (obsidian as u64) << 16
        | (geodes as u64)
        | (ore_robots as u64) << 56
        | (clay_robots as u64) << 40
        | (obsidian_robots as u64) << 24
        | (geode_robots as u64) << 8
}

fn decode_state(state: u64) -> (u8, u8, u8, u8, u8, u8, u8, u8) {
    let ore = (state >> 48) as u8;
    let clay = (state >> 32) as u8;
    let obsidian = (state >> 16) as u8;
    let geodes = state as u8;
    let ore_robots = (state >> 56) as u8;
    let clay_robots = (state >> 40) as u8;
    let obsidian_robots = (state >> 24) as u8;
    let geode_robots = (state >> 8) as u8;
    (
        ore,
        clay,
        obsidian,
        geodes,
        ore_robots,
        clay_robots,
        obsidian_robots,
        geode_robots,
    )
}

fn maximize_geodes(blueprint: &Blueprint, minutes: u8) -> u8 {
    let mut memo: HashMap<(u64, u8), u8> = HashMap::new();

    fn dp(state: u64, blueprint: &Blueprint, minutes: u8, memo: &mut HashMap<(u64, u8), u8>) -> u8 {
        if let Some(&geodes) = memo.get(&(state, minutes)) {
            return geodes;
        }
        let (ore, clay, obsidian, geodes, ore_robots, clay_robots, obsidian_robots, geode_robots) =
            decode_state(state);
        if minutes == 0 {
            return geodes;
        }
        let mut max_geodes = geodes;
        let mut new_state;

        // Build a geode robot.
        if ore >= blueprint.geode_cost
            && obsidian >= blueprint.geode_obsidian_cost
            && obsidian_robots > 0
            && minutes > 1
        {
            new_state = encode_state(
                ore - blueprint.geode_cost + ore_robots,
                clay + clay_robots,
                obsidian - blueprint.geode_obsidian_cost + obsidian_robots,
                geodes + geode_robots,
                ore_robots,
                clay_robots,
                obsidian_robots,
                geode_robots + 1,
            );
            max_geodes = max(max_geodes, dp(new_state, blueprint, minutes - 1, memo));
        }

        // Build an obsidian robot.
        if clay >= blueprint.obsidian_clay_cost
            && ore >= blueprint.obsidian_cost
            && clay_robots > 0
            && minutes > 2
        {
            new_state = encode_state(
                ore - blueprint.obsidian_cost + ore_robots,
                clay - blueprint.obsidian_clay_cost + clay_robots,
                obsidian + obsidian_robots,
                geodes + geode_robots,
                ore_robots,
                clay_robots,
                obsidian_robots + 1,
                geode_robots,
            );
            max_geodes = max(max_geodes, dp(new_state, blueprint, minutes - 1, memo));
        }

        // Build a clay robot.
        if ore >= blueprint.clay_cost {
            new_state = encode_state(
                ore - blueprint.clay_cost + ore_robots,
                clay + clay_robots,
                obsidian + obsidian_robots,
                geodes + geode_robots,
                ore_robots,
                clay_robots + 1,
                obsidian_robots,
                geode_robots,
            );
            max_geodes = max(max_geodes, dp(new_state, blueprint, minutes - 1, memo));
        }

        // Build an ore robot.
        if ore >= blueprint.ore_cost {
            new_state = encode_state(
                ore - blueprint.ore_cost + ore_robots,
                clay + clay_robots,
                obsidian + obsidian_robots,
                geodes + geode_robots,
                ore_robots + 1,
                clay_robots,
                obsidian_robots,
                geode_robots,
            );
            max_geodes = max(max_geodes, dp(new_state, blueprint, minutes - 1, memo));
        }

        memo.insert((state, minutes), max_geodes);
        new_state = encode_state(
            ore + ore_robots,
            clay + clay_robots,
            obsidian + obsidian_robots,
            geodes + geode_robots,
            ore_robots,
            clay_robots,
            obsidian_robots,
            geode_robots,
        );
        max_geodes = max(max_geodes, dp(new_state, blueprint, minutes - 1, memo));
        max_geodes
    }

    let initial_state = encode_state(0, 0, 0, 0, 1, 0, 0, 0);
    dp(initial_state, blueprint, minutes, &mut memo)
}

#[aoc_generator(day19)]
fn generator_input(input: &str) -> Vec<Blueprint> {
    input.lines().map(|line| {
        lazy_static! {
                static ref RE_BLUEPRINTS: Regex = Regex::new(
                    r"^Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.$"
                )
                .unwrap();
            }
        if let Some(caps) = RE_BLUEPRINTS.captures(line) {
            let id = caps[1].parse::<u8>().unwrap();
            let ore_cost = caps[2].parse::<u8>().unwrap();
            let clay_cost = caps[3].parse::<u8>().unwrap();
            let obsidian_cost = caps[4].parse::<u8>().unwrap();
            let obsidian_clay_cost = caps[5].parse::<u8>().unwrap();
            let geode_cost = caps[6].parse::<u8>().unwrap();
            let geode_obsidian_cost = caps[7].parse::<u8>().unwrap();
            Blueprint::new(id, ore_cost, clay_cost, obsidian_cost, obsidian_clay_cost, geode_cost, geode_obsidian_cost)
        } else {
            panic!("Input string did not match regular expression");
        }
    }
    ).collect()
}

#[aoc(day19, part1)]
fn part2(input: &[Blueprint]) -> usize {
    input
        .iter()
        .map(|b| {
            println!("Processing blueprint {}...", b.id);
            let max_geodes = maximize_geodes(b, 24);
            println!("res: {}", max_geodes);
            b.id as usize * max_geodes as usize
        })
        .sum()
}

#[test]
fn test_encode_decode_state() {
    let state = encode_state(0, 0, 0, 0, 1, 0, 0, 0);
    let (ore, clay, _, geodes, ore_robots, _, _, _) = decode_state(state);
    assert_eq!(ore, 0);
    assert_eq!(ore_robots, 1);
    assert_eq!(clay, 0);
    assert_eq!(geodes, 0);
}
