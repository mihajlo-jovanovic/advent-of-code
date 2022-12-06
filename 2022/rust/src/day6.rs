use aoc_runner_derive::{aoc, aoc_generator};
use itertools::Itertools;

#[aoc_generator(day6)]
fn generator_input(input: &str) -> Vec<char> {
    input.chars().collect()
}

#[aoc(day6, part1)]
fn part1(signal: &[char]) -> usize {
    find_start_of_packet_pos(signal, 4)
}

#[aoc(day6, part2)]
fn part2(signal: &[char]) -> usize {
    find_start_of_packet_pos(signal, 14)
}

fn find_start_of_packet_pos(packet: &[char], window_size: usize) -> usize {
    packet
        .windows(window_size)
        .enumerate()
        .find_map(|(i, w)| {
            if w.iter().unique().count() == window_size {
                Some(i)
            } else {
                None
            }
        })
        .unwrap()
        + window_size
}

#[test]
fn test_generator() {
    assert_eq!(5, generator_input("abcde").len());
}

#[test]
fn test_find() {
    assert_eq!(
        7,
        find_start_of_packet_pos(&generator_input("mjqjpqmgbljsphdztnvjfqwrcgsmlb"), 4)
    );
    assert_eq!(
        5,
        find_start_of_packet_pos(&generator_input("bvwbjplbgvbhsrlpgdmjqwftvncz"), 4)
    );
    assert_eq!(
        6,
        find_start_of_packet_pos(&generator_input("nppdvjthqldpwncqszvftbrmjlhg"), 4)
    );
    assert_eq!(
        10,
        find_start_of_packet_pos(&generator_input("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"), 4)
    );
    assert_eq!(
        11,
        find_start_of_packet_pos(&generator_input("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"), 4)
    );
    assert_eq!(
        19,
        find_start_of_packet_pos(&generator_input("mjqjpqmgbljsphdztnvjfqwrcgsmlb"), 14)
    );
    assert_eq!(
        23,
        find_start_of_packet_pos(&generator_input("bvwbjplbgvbhsrlpgdmjqwftvncz"), 14)
    );
    assert_eq!(
        23,
        find_start_of_packet_pos(&generator_input("nppdvjthqldpwncqszvftbrmjlhg"), 14)
    );
    assert_eq!(
        29,
        find_start_of_packet_pos(&generator_input("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"), 14)
    );
    assert_eq!(
        26,
        find_start_of_packet_pos(&generator_input("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"), 14)
    );
}
