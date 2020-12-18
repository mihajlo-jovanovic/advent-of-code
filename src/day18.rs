#[aoc_generator(day18)]
fn parse_input(input: &str) -> Vec<String> {
    input.lines().map(|l| l.to_string()).collect()
}

#[aoc(day18, part1)]
fn part1(input: &[String]) -> u64 {
    input.iter().map(|ex| eval(ex)).sum()
}

fn eval(ex: &str) -> u64 {
    eval_helper(&resolve_pars(ex))
}

fn eval_helper(ex: &str) -> u64 {
    let tokens: Vec<&str> = ex.split(' ').collect();
    let op: u64 = tokens[0].parse::<u64>().unwrap();

    if tokens.len() == 1 {
        return op;
    }

    let op2: u64 = tokens[2].parse::<u64>().unwrap();
    let mut new_ex;
    match tokens[1] {
        "+" => new_ex = (op + op2).to_string(),
        "*" => new_ex = (op * op2).to_string(),
        _ => panic!("Unknown operator: {}", tokens[1]),
    }

    if tokens.len() > 3 {
        new_ex.push_str(" ");
        new_ex.push_str(&tokens[3..].join(" "));
    }
    eval_helper(&new_ex)
}

fn resolve_pars(ex: &str) -> String {
    if !ex.contains(')') {
        return ex.to_string();
    }
    let end: usize = ex.find(')').unwrap();
    let beg: usize = ex
        .chars()
        .enumerate()
        .filter(|(i, c)| *c == '(' && i < &end)
        .map(|(i, _)| i)
        .max()
        .unwrap();
    let mut new_ex = ex[..beg].to_string();
    new_ex.push_str(&eval_helper(&ex[beg + 1..end]).to_string());
    if end == ex.len() - 1 {
        new_ex
    } else {
        new_ex.push_str(&ex[end + 1..]);
        resolve_pars(&new_ex)
    }
}

#[test]
fn test_eval() {
    assert_eq!(71, eval("1 + 2 * 3 + 4 * 5 + 6"));
    assert_eq!(6, eval("2 * 3"));
    assert_eq!(51, eval("1 + (2 * 3) + (4 * (5 + 6))"));
    assert_eq!(26, eval("2 * 3 + (4 * 5)"));
    assert_eq!(437, eval("5 + (8 * 3 + 9 + 3 * 4 * 3)"));
    assert_eq!(12240, eval("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))"));
    assert_eq!(
        13632,
        eval("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2")
    );
}

#[test]
fn test_resolve_pars() {
    let ex = "1 + (2 * 3)";
    assert_eq!("1 + 6", resolve_pars(ex));
    assert_eq!("1 + 6 + 44", resolve_pars(&"1 + (2 * 3) + (4 * (5 + 6))"));
}
