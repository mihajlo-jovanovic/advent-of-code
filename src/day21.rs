use itertools::Itertools;
use std::collections::BTreeMap;
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

#[aoc(day21, part2)]
fn part2(foods: &[(HashSet<String>, HashSet<String>)]) -> String {
    let allergens: Vec<&str> = foods
        .iter()
        .flat_map(|f| &f.1)
        .map(|a| a.as_str())
        .unique()
        .collect();
    // println!("unique allergens: {:#?}", allergens);
    let ingredients: Vec<&str> = foods
        .iter()
        .flat_map(|f| &f.0)
        .map(|a| a.as_str())
        .unique()
        .filter(|i| can_contain_any_allergens(i, foods))
        .collect();
    // println!("unique ingredients: {:#?}", ingredients);
    let mut f = Foods::new(
        foods
            .iter()
            .map(|f| {
                (
                    f.0.iter().map(|i| i.as_str()).collect(),
                    f.1.iter().map(|a| a.as_str()).collect(),
                )
            })
            .collect(),
        ingredients,
        allergens,
    );
    f.match_allergens_to_ingredients();
    // map is already sorted alphabetically by keys
    f.matches.values().join(",")
}

pub struct Foods<'a> {
    food_list: Vec<(HashSet<&'a str>, HashSet<&'a str>)>,
    ingredients: Vec<&'a str>,
    allergens: Vec<&'a str>,
    matches: BTreeMap<&'a str, &'a str>,
}

impl<'a> Foods<'a> {
    pub fn new(
        food_list: Vec<(HashSet<&'a str>, HashSet<&'a str>)>,
        ingredients: Vec<&'a str>,
        allergens: Vec<&'a str>,
    ) -> Foods<'a> {
        Foods {
            food_list,
            ingredients,
            allergens,
            matches: BTreeMap::new(),
        }
    }
    fn is_valid(&self, allergen: &str, ingredient: &str) -> bool {
        for f in &self.food_list {
            // if this allergen is contained in the allergen list for this food item, then the ingredient must also be present
            if f.1.contains(allergen) && !f.0.contains(ingredient) {
                return false;
            }
        }
        true
    }

    fn match_allergens_to_ingredients(&mut self) -> bool {
        self.match_allergens_to_ingedients_r(0)
    }

    fn match_allergens_to_ingedients_r(&mut self, allergen_idx: usize) -> bool {
        // println!(
        //     "allergens: {:#?}, ingredients: {:#?}, matches: {:#?}",
        //     &self.allergens, &self.ingredients, &self.matches
        // );
        if allergen_idx >= self.allergens.len() {
            return true;
        }
        for i in self.ingredients.clone() {
            if self.is_valid(self.allergens[allergen_idx], i) {
                self.matches.insert(self.allergens[allergen_idx], i);
                self.ingredients.remove(
                    self.ingredients
                        .iter()
                        .position(|x| *x == i)
                        .expect("Ingredient not found"),
                );
                if self.match_allergens_to_ingedients_r(allergen_idx + 1) {
                    return true;
                }
                //put stuff back where it was
                self.matches.remove(self.allergens[allergen_idx]);
                self.ingredients.push(i);
            }
        }
        false
    }
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

#[test]
fn test_part2_backtracking() {
    let foods = parse_input(
        "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)",
    );
    let mut f = Foods::new(
        foods
            .iter()
            .map(|f| {
                (
                    f.0.iter().map(|i| i.as_str()).collect(),
                    f.1.iter().map(|a| a.as_str()).collect(),
                )
            })
            .collect(),
        vec!["mxmxvkd", "sqjhc", "fvjkl"],
        vec!["dairy", "soy", "fish"],
    );
    assert!(!f.is_valid("fish", "fvjkl"));
    assert!(f.match_allergens_to_ingredients());
    assert_eq!(3, f.matches.len());
    assert_eq!(&"mxmxvkd", f.matches.get("dairy").unwrap());
}

#[test]
fn test_part2() {
    let foods = parse_input(
        "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)",
    );
    assert_eq!("mxmxvkd,sqjhc,fvjkl", part2(&foods));
}
