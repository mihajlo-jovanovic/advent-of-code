use itertools::Itertools;
use std::collections::HashSet;
use std::iter::FromIterator;

#[aoc_generator(day21)]
fn parse_input(input: &str) -> Vec<(HashSet<String>, HashSet<String>)> {
    input
        .lines()
        .map(|l| {
            let mut parts = l.split(" (contains ");
            let ingredients = parts.next().unwrap();
            let allergens = parts.next().unwrap();
            (
                HashSet::from_iter(ingredients.split(' ').map(|s| s.to_owned())),
                HashSet::from_iter(
                    allergens[0..allergens.len() - 1]
                        .split(", ")
                        .map(|s| s.to_owned()),
                ),
            )
        })
        .collect()
}

#[aoc(day21, part1)]
fn part1(foods: &[(HashSet<String>, HashSet<String>)]) -> u32 {
    println!("total foods: {}", foods.len());
    println!(
        "total unique ingredients: {:#?}",
        foods
            .iter()
            .flat_map(|f| { &f.0 })
            .unique()
            .cloned()
            .count()
    );
    let mut count: u32 = 0;
    for f in foods {
        for i in &f.0 {
            if !can_contain_any_allergens(&i, &foods) {
                count += 1;
            }
        }
    }
    count
}

fn can_contain_any_allergens(
    ingredient: &str,
    foods: &[(HashSet<String>, HashSet<String>)],
) -> bool {
    let allergens_unique: HashSet<String> = HashSet::from_iter(
        foods
            .iter()
            .filter(|f| f.0.contains(ingredient))
            .flat_map(|f| &f.1)
            .unique()
            .cloned(),
    );
    for allergen in allergens_unique {
        //find all foods that list this allergen; ingredient must be present in at least one
        for f in foods.iter().filter(|f| f.1.contains(&allergen)) {
            if f.0.contains(ingredient)
                && foods
                    .iter()
                    .filter(|f| f.0.contains(ingredient) && f.1.contains(&allergen))
                    .count()
                    == foods.iter().filter(|f| f.1.contains(&allergen)).count()
            {
                return true;
            }
        }
    }
    false
}

#[test]
fn test_parsing() {
    let foods = parse_input(
        "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)",
    );
    println!("{:#?}", foods);
    assert_eq!(4, foods.len());
    assert!(foods.get(0).unwrap().0.contains("mxmxvkd"));
    assert!(!foods.get(0).unwrap().0.contains("trh"));
    assert!(foods.get(0).unwrap().1.contains("dairy"));
}

#[test]
fn test_part1() {
    let foods = parse_input(
        "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)",
    );
    assert_eq!(5, part1(&foods));
}

#[test]
fn test_can_contain_any_allergens() {
    let foods = parse_input(
        "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
    trh fvjkl sbzzf mxmxvkd (contains dairy)
    sqjhc fvjkl (contains soy)
    sqjhc mxmxvkd sbzzf (contains fish)",
    );
    assert!(can_contain_any_allergens(&"mxmxvkd", &foods));
    assert!(!can_contain_any_allergens(&"kfcds", &foods));
    assert!(can_contain_any_allergens(&"sqjhc", &foods));
    assert!(!can_contain_any_allergens(&"nhms", &foods));
    assert!(!can_contain_any_allergens(&"trh", &foods));
    assert!(can_contain_any_allergens(&"fvjkl", &foods));
    assert!(!can_contain_any_allergens(&"sbzzf", &foods));
}
