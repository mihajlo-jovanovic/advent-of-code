use std::collections::HashSet;
use std::str::FromStr;

const ORE: usize = 0;
const CLAY: usize = 1;
const OBS: usize = 2;
const GEODE: usize = 3;
const MAX_STATES: usize = 1000; // seems to also work with as little as 90 except for blueprint #2 from sample input

#[derive(Debug)]
struct Blueprint {
    id: usize,
    prices: [[usize; 4]; 4],
}

#[derive(Debug, PartialEq, Eq)]
struct ParseBlueprintError;

impl FromStr for Blueprint {
    type Err = ParseBlueprintError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let v: Vec<_> = s
            .replace(':', "")
            .split_whitespace()
            .filter_map(|t| t.parse::<usize>().ok())
            .collect();
        Ok(Self {
            id: v[0],
            prices: [
                [v[1], 0, 0, 0],
                [v[2], 0, 0, 0],
                [v[3], v[4], 0, 0],
                [v[5], 0, v[6], 0],
            ],
        })
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
    (ore as u64)
        | (clay as u64) << 16
        | (obsidian as u64) << 32
        | (geodes as u64) << 48
        | (ore_robots as u64) << 8
        | (clay_robots as u64) << 24
        | (obsidian_robots as u64) << 40
        | (geode_robots as u64) << 56
}

fn decode_state(state: u64) -> (u8, u8, u8, u8, u8, u8, u8, u8) {
    let ore = state as u8;
    let clay = (state >> 16) as u8;
    let obsidian = (state >> 32) as u8;
    let geode_robots = (state >> 56) as u8;
    let ore_robots = (state >> 8) as u8;
    let clay_robots = (state >> 24) as u8;
    let obsidian_robots = (state >> 40) as u8;
    let geode = (state >> 48) as u8;
    (
        ore,
        clay,
        obsidian,
        geode,
        ore_robots,
        clay_robots,
        obsidian_robots,
        geode_robots,
    )
}

fn keep_best_states(states: &mut HashSet<u64>) {
    if states.len() > MAX_STATES {
        let mut sorted: Vec<u64> = states.iter().copied().collect();
        sorted.sort();
        sorted.reverse();
        sorted.drain(MAX_STATES..);
        states.retain(|x| sorted.contains(x));
    }
}

fn next_states(states: &mut HashSet<u64>, prices: &[[usize; 4]; 4]) {
    let new_states: HashSet<u64> = states
        .iter()
        .flat_map(|s| {
            let (
                ore,
                clay,
                obsidian,
                geodes,
                ore_robots,
                clay_robots,
                obsidian_robots,
                geode_robots,
            ) = decode_state(*s);
            let mut tmp: Vec<u64> = vec![];

            if ore >= prices[GEODE][ORE] as u8 && obsidian >= prices[GEODE][OBS] as u8 {
                //println!("Can build geode robot!");
                tmp.push(encode_state(
                    ore - prices[GEODE][ORE] as u8 + ore_robots,
                    clay + clay_robots,
                    obsidian - prices[GEODE][OBS] as u8 + obsidian_robots,
                    geodes + geode_robots,
                    ore_robots,
                    clay_robots,
                    obsidian_robots,
                    geode_robots + 1,
                ));
            }
            if ore >= prices[OBS][ORE] as u8 && clay >= prices[OBS][CLAY] as u8 {
                //println!("Can build obsidian robot!");
                tmp.push(encode_state(
                    ore - prices[OBS][ORE] as u8 + ore_robots,
                    clay - prices[OBS][CLAY] as u8 + clay_robots,
                    obsidian + obsidian_robots,
                    geodes + geode_robots,
                    ore_robots,
                    clay_robots,
                    obsidian_robots + 1,
                    geode_robots,
                ));
            }
            if ore >= prices[CLAY][ORE] as u8 {
                //println!("Can build clay robot!")
                tmp.push(encode_state(
                    ore - prices[CLAY][ORE] as u8 + ore_robots,
                    clay + clay_robots,
                    obsidian + obsidian_robots,
                    geodes + geode_robots,
                    ore_robots,
                    clay_robots + 1,
                    obsidian_robots,
                    geode_robots,
                ));
            }
            if ore >= prices[ORE][ORE] as u8 {
                //println!("Can build ore robot!")
                tmp.push(encode_state(
                    ore - prices[ORE][ORE] as u8 + ore_robots,
                    clay + clay_robots,
                    obsidian + obsidian_robots,
                    geodes + geode_robots,
                    ore_robots + 1,
                    clay_robots,
                    obsidian_robots,
                    geode_robots,
                ));
            }
            let new_state = encode_state(
                ore + ore_robots,
                clay + clay_robots,
                obsidian + obsidian_robots,
                geodes + geode_robots,
                ore_robots,
                clay_robots,
                obsidian_robots,
                geode_robots,
            );
            tmp.push(new_state);
            tmp
        })
        .collect();
    states.extend(new_states);
}

#[aoc_generator(day19)]
fn generator_input(input: &str) -> Vec<Blueprint> {
    let blueprints: Vec<Blueprint> = input.lines().map(|line| line.parse().unwrap()).collect();
    blueprints
}

fn max_geodes(mins: u8, b: &Blueprint) -> u8 {
    let start_state = encode_state(0, 0, 0, 0, 1, 0, 0, 0);
    let mut states: HashSet<u64> = vec![start_state].into_iter().collect();
    for _ in 0..mins {
        next_states(&mut states, &b.prices);
        keep_best_states(&mut states);
    }
    states
        .iter()
        .map(|x| {
            let (_, _, _, geodes, _, _, _, _) = decode_state(*x);
            geodes
        })
        .max()
        .unwrap()
}

#[aoc(day19, part1)]
fn part1(blueprints: &[Blueprint]) -> usize {
    blueprints
        .iter()
        .map(|b| b.id * max_geodes(24, b) as usize)
        .sum()
}

#[aoc(day19, part2)]
fn part2(blueprints: &[Blueprint]) -> usize {
    // println!("{}", input.len());
    // println!("{:?}", input[0]);
    blueprints
        .iter()
        .take(3)
        .map(|b| max_geodes(32, b) as usize)
        .product()
}

#[test]
fn test_max_geodes() {
    let input = "Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.\n\
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.";
    let blueprints = generator_input(input);
    assert_eq!(blueprints.len(), 2);
    assert_eq!(max_geodes(24, &blueprints[0]), 9);
    assert_eq!(max_geodes(24, &blueprints[1]), 12);
    assert_eq!(max_geodes(32, &blueprints[0]), 56);
    assert_eq!(max_geodes(32, &blueprints[1]), 62);
}

#[test]
fn test_encode_decode_state() {
    let state = encode_state(0, 0, 0, 0, 1, 0, 0, 0);
    let (ore, clay, _, geodes, ore_robots, _, _, _) = decode_state(state);
    assert_eq!(ore, 0);
    assert_eq!(ore_robots, 1);
    assert_eq!(clay, 0);
    assert_eq!(geodes, 0);
    let state2 = encode_state(0, 0, 0, 0, 0, 0, 0, 1);
    assert!(state2 > state);
    let state3 = encode_state(0, 0, 0, 1, 0, 0, 0, 0);
    assert!(state2 > state3);
    let state4 = encode_state(0, 0, 0, 0, 0, 0, 1, 0);
    let state5 = encode_state(0, 10, 0, 0, 3, 2, 0, 0);
    assert!(state4 > state);
    assert!(state4 > state5);
}
