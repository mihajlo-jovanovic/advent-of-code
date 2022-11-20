#[aoc_generator(day22)]
fn parse_input(input: &str) -> (Vec<u8>, Vec<u8>) {
    let mut parts = input.split("\n\n");
    let p1 = parts.next().unwrap();
    let p2 = parts.next().unwrap();
    (
        p1.lines()
            .filter(|l| l.parse::<u8>().is_ok())
            .map(|l| l.parse::<u8>().unwrap())
            .collect(),
        p2.lines()
            .filter(|l| l.parse::<u8>().is_ok())
            .map(|l| l.parse::<u8>().unwrap())
            .collect(),
    )
}

#[aoc(day22, part1)]
fn part1(state: &(Vec<u8>, Vec<u8>)) -> u32 {
    let winning_deck = play_round(&state.0, &state.1);
    let mut cp = winning_deck;
    cp.reverse();
    cp.iter()
        .enumerate()
        .map(|(i, c)| (i + 1) * (*c as usize))
        .map(|x| x as u32)
        .sum()
}

#[aoc(day22, part2)]
fn part2(state: &(Vec<u8>, Vec<u8>)) -> u32 {
    let winning_deck = play_game_recur(&state.0, &state.1).1;
    let mut cp = winning_deck;
    cp.reverse();
    cp.iter()
        .enumerate()
        .map(|(i, c)| (i + 1) * (*c as usize))
        .map(|x| x as u32)
        .sum()
}

fn play_round(deck1_ref: &[u8], deck2_ref: &[u8]) -> Vec<u8> {
    let mut deck1 = deck1_ref.to_vec();
    let mut deck2 = deck2_ref.to_vec();
    while !deck1.is_empty() && !deck2.is_empty() {
        let p1 = deck1.remove(0);
        let p2 = deck2.remove(0);
        if p1 > p2 {
            deck1.push(p1);
            deck1.push(p2);
        } else {
            deck2.push(p2);
            deck2.push(p1);
        }
    }
    if deck1.is_empty() {
        deck2
    } else {
        deck1
    }
}

// true if player 1 wins, false otherwise; second element of tuple is the winning deck
fn play_game_recur(deck1_ref: &[u8], deck2_ref: &[u8]) -> (bool, Vec<u8>) {
    let mut deck1 = deck1_ref.to_vec();
    let mut deck2 = deck2_ref.to_vec();
    let mut player_one_won;
    let mut previous_states_deck1: Vec<Vec<u8>> = Vec::new();
    let mut previous_states_deck2: Vec<Vec<u8>> = Vec::new();
    while !deck1.is_empty() && !deck2.is_empty() {
        //check for already seen state
        if previous_states_deck1.contains(&deck1) && previous_states_deck2.contains(&deck2) {
            return (true, deck1);
        } else {
            previous_states_deck1.push(deck1.clone());
            previous_states_deck2.push(deck2.clone());
        }
        let p1 = deck1.remove(0);
        let p2 = deck2.remove(0);
        //check for recursive sub-game
        if deck1.len() as u8 >= p1 && deck2.len() as u8 >= p2 {
            player_one_won = play_game_recur(&deck1[0..p1 as usize], &deck2[0..p2 as usize]).0;
        } else {
            player_one_won = p1 > p2;
        }
        if player_one_won {
            deck1.push(p1);
            deck1.push(p2);
        } else {
            deck2.push(p2);
            deck2.push(p1);
        }
    }
    if deck1.is_empty() {
        (false, deck2)
    } else {
        (true, deck1)
    }
}

#[test]
fn test_part1() {
    let input = "Player 1:
9
2
6
3
1

Player 2:
5
8
4
7
10";
    let parsed = parse_input(input);
    assert_eq!(306, part1(&parsed));
}

#[test]
fn test_part2() {
    let input = "Player 1:
9
2
6
3
1

Player 2:
5
8
4
7
10";
    let parsed = parse_input(input);
    assert_eq!(
        vec![7, 5, 6, 2, 4, 1, 10, 8, 9, 3],
        play_game_recur(&parsed.0, &parsed.1).1
    );
    assert_eq!(291, part2(&parsed));
}

#[test]
fn test_infinite_loop() {
    let input = "Player 1:
43
19

Player 2:
2
29
14";
    let parsed = parse_input(input);
    println!("{:#?}", play_game_recur(&parsed.0, &parsed.1));
}
