use std::collections::HashMap;

#[derive(Debug)]
struct MemoryGame {
    curr: u32,
    turn: u32,
    state: HashMap<u32, (u32, u32)>,
}

impl Iterator for MemoryGame {
    type Item = u32;

    fn next(&mut self) -> Option<u32> {
        let cache_entry = self.state.get(&self.curr).unwrap();
        self.curr = cache_entry.1;
        self.turn += 1;
        let age = self.turn - self.state.entry(self.curr).or_insert((self.turn, 0)).0;
        self.state.insert(self.curr, (self.turn, age));
        Some(self.curr)
    }
}

fn memory_game(seed: &[u32]) -> MemoryGame {
    let mut state: HashMap<u32, (u32, u32)> = HashMap::new();
    for (i, num) in seed.iter().enumerate() {
        state.insert(*num, ((i + 1) as u32, 0));
    }
    MemoryGame {
        curr: seed[seed.len() - 1],
        turn: state.len() as u32,
        state: state,
    }
}

#[test]
fn test_iter() {
    let game = memory_game(&[5, 1, 9, 18, 13, 8, 0]);
    assert_eq!(436, game.skip(2012).next().unwrap());
}
