use itertools::put_back;
use std::cell::RefCell;
use std::sync::{Arc, Weak};

use crate::day7::Inode::{Dir, File};

#[aoc_generator(day7)]
fn generator_input(input: &str) -> Vec<String> {
    input.lines().map(|line| line.to_owned()).collect()
}

#[aoc(day7, part1)]
fn part1(term_output: &[String]) -> usize {
    let fs = parse_tree(term_output);
    let mut dirs: Vec<usize> = Vec::new();
    calc_size(&fs.root, &mut dirs);
    dirs.iter().filter(|&sz| *sz <= 100000).sum()
}

#[aoc(day7, part2)]
fn part2(term_output: &[String]) -> usize {
    let fs = parse_tree(term_output);
    let mut dirs: Vec<usize> = Vec::new();
    calc_size(&fs.root, &mut dirs);
    dirs.sort();
    *dirs.iter().find(|&sz| *sz >= 1072511).unwrap()
}

fn calc_size(root: &NodeRef<Inode>, dirs: &mut Vec<usize>) -> usize {
    let mut total_sz: usize = 0;
    let children = root.children.borrow();
    for c in children.iter() {
        if let Inode::Dir(_, _) = &c.value {
            total_sz += calc_size(c, dirs);
        } else if let Inode::File(_, sz) = &c.value {
            total_sz += sz;
        }
    }
    dirs.push(total_sz);
    total_sz
}

// Tree impl from https://play.rust-lang.org/?version=stable&mode=debug&edition=2021&gist=f8db9ee621dc064051dab005a273e86c
#[derive(Debug, PartialEq)]
enum Inode {
    File(String, usize),
    Dir(String, usize),
}

type NodeRef<T> = Arc<Node<T>>;
type Parent<T> = RefCell<Weak<Node<T>>>;
type Children<T> = RefCell<Vec<NodeRef<T>>>;

#[derive(Debug)]
struct Node<T> {
    value: T,
    parent: Parent<T>,
    children: Children<T>,
}

struct Tree<T> {
    root: NodeRef<T>,
}

impl<T> Tree<T> {
    fn new(root: NodeRef<T>) -> Tree<T> {
        Tree { root }
    }
}

/// `child_node.parent` is set to weak reference to `parent_node`.
fn set_parent<T>(child: &NodeRef<T>, parent: &NodeRef<T>) {
    *child.parent.borrow_mut() = Arc::downgrade(parent);
}

fn add_child<T>(child: &NodeRef<T>, parent: &NodeRef<T>) {
    parent.children.borrow_mut().push(child.clone());
}

fn create_node<T>(value: T) -> NodeRef<T> {
    let node = Node {
        value,
        parent: RefCell::new(Weak::new()),  // Basically None.
        children: RefCell::new(Vec::new()), // Basically [].
    };
    Arc::new(node)
}

fn parse_tree(term_output: &[String]) -> Tree<Inode> {
    let mut pwd = create_node(Dir(String::from("/"), 0));
    let t = Tree::new(pwd.clone());
    let mut it = put_back(term_output.iter());
    it.next(); // get rid of cd /
    while let Some(i) = it.next() {
        //println!("Processing: {} for current dir {:?}", i, pwd.value);
        if i.starts_with("$ ls") {
            while let Some(tmp) = it.next() {
                if tmp.starts_with('$') {
                    // encountered next cmd - must've read all files...break
                    it.put_back(tmp);
                    break;
                }
                if tmp.starts_with("dir") {
                    let mut parts = tmp.split_whitespace();
                    parts.next();
                    let nm = String::from(parts.next().unwrap());
                    let f = Dir(nm, 0);
                    let dir: NodeRef<Inode> = create_node(f);
                    add_child(&dir, &pwd);
                    set_parent(&dir, &pwd);
                } else {
                    let mut parts = tmp.split_whitespace();
                    let sz: usize = parts.next().unwrap().parse().unwrap();
                    let nm = String::from(parts.next().unwrap());
                    let f = File(nm, sz);
                    let f: NodeRef<Inode> = create_node(f);
                    add_child(&f, &pwd);
                    set_parent(&f, &pwd);
                }
            }
        } else if i.starts_with("$ cd ..") {
            let current = pwd.parent.borrow().upgrade().unwrap();
            pwd = current;
        } else if i.starts_with("$ cd") {
            let nm = &i[5..];
            let pwd2 = pwd.clone();
            let bindings = pwd2.children.borrow();
            let current = bindings.iter().find(|&c| {
                if let Inode::Dir(nm2, _) = &c.value {
                    nm == nm2
                } else {
                    false
                }
            });
            pwd = current.unwrap().clone();
        } else {
            panic!("Invalid cmd!")
        }
    }
    t
}

#[test]
fn test_parse_tree() {
    let parsed = generator_input(
        "$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
",
    );
    let t = parse_tree(&parsed);
    assert!(matches!(t.root.value, Dir(..)));
    assert_eq!(Arc::strong_count(&t.root), 1); // `child_node` has 2 strong references.
    assert_eq!(Arc::weak_count(&t.root), 4);
    assert_eq!(t.root.children.borrow().len(), 4);
    let files = t.root.children.borrow();
    //find dir a
    let dir_a = files
        .iter()
        .find(|&f| {
            if let Inode::Dir(nm, _) = &f.value {
                nm == "a"
            } else {
                false
            }
        })
        .unwrap();
    assert_eq!(dir_a.value, Dir(String::from("a"), 0));
    assert_eq!(Arc::strong_count(dir_a), 1); // `child_node` has 2 strong references.
    assert_eq!(Arc::weak_count(&dir_a), 4);
    assert_eq!(dir_a.children.borrow().len(), 4);

    let files = dir_a.children.borrow();
    //find dir e
    let dir_e = files
        .iter()
        .find(|&f| {
            if let Inode::Dir(nm, _) = &f.value {
                nm == "e"
            } else {
                false
            }
        })
        .unwrap();
    assert_eq!(dir_e.value, Dir(String::from("e"), 0));
    assert_eq!(Arc::strong_count(dir_e), 1); // `child_node` has 2 strong references.
    assert_eq!(Arc::weak_count(&dir_e), 1);
    assert_eq!(dir_e.children.borrow().len(), 1);
    assert_eq!(
        dir_e.children.borrow().get(0).unwrap().value,
        File(String::from("i"), 584)
    );
}
