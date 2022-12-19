use array2d::Array2D;
use petgraph::algo::dijkstra;
use petgraph::graph::NodeIndex;
use petgraph::{Directed, Graph};

#[aoc_generator(day12)]
fn generator_input(input: &str) -> Array2D<char> {
    let heightmap: Vec<char> = input.lines().flat_map(|line| line.chars()).collect();
    let num_of_rows = input.lines().count();
    Array2D::from_row_major(
        &heightmap,
        input.lines().count(),
        heightmap.len() / num_of_rows,
    )
}

fn build_graph(heightmap: &Array2D<char>) -> Graph<(), ()> {
    let mut graph: Graph<(), (), Directed> = Graph::new();
    let mut nodes = vec![];
    for _ in 0..heightmap.num_rows() {
        for _ in 0..heightmap.num_columns() {
            nodes.push(graph.add_node(()));
        }
    }
    let mut edges: Vec<(NodeIndex, NodeIndex)> = vec![];
    for i in 0..heightmap.num_rows() {
        for j in 0..heightmap.num_columns() {
            let idx = i * heightmap.num_columns() + j;
            let n = nodes.get(idx).unwrap();
            let h = heightmap[(i, j)];
            let u = heightmap.get(i - 1, j);
            let d = heightmap.get(i + 1, j);
            let l = heightmap.get(i, j + 1);
            let r = heightmap.get(i, j - 1);

            if u.is_some() && can_climb(h, *u.unwrap()) {
                let u_n = nodes.get(idx - heightmap.num_columns()).unwrap();
                edges.push((*n, *u_n));
            }
            if d.is_some() && can_climb(h, *d.unwrap()) {
                let d_n = nodes.get(idx + heightmap.num_columns()).unwrap();
                edges.push((*n, *d_n));
            }
            if l.is_some() && can_climb(h, *l.unwrap()) {
                let l_n = nodes.get(idx + 1).unwrap();
                edges.push((*n, *l_n));
            }
            if r.is_some() && can_climb(h, *r.unwrap()) {
                let r_n = nodes.get(idx - 1).unwrap();
                edges.push((*n, *r_n));
            }
        }
    }
    graph.extend_with_edges(&edges);
    graph
}

#[aoc(day12, part1)]
fn part1(heightmap: &Array2D<char>) -> usize {
    let graph = build_graph(heightmap);
    let mut start: Option<NodeIndex> = None;
    let mut end: Option<NodeIndex> = None;
    for i in 0..heightmap.num_rows() {
        for j in 0..heightmap.num_columns() {
            let idx = i * heightmap.num_columns() + j;
            let h = heightmap[(i, j)];
            if h == 'S' {
                start = lookup_node(&graph, idx);
            } else if h == 'E' {
                end = lookup_node(&graph, idx);
            }
        }
    }
    let res = dijkstra(&graph, start.unwrap(), None, |_| 1);
    *res.get(&end.unwrap()).unwrap() as usize
}

#[aoc(day12, part2)]
fn part2(heightmap: &Array2D<char>) -> usize {
    let graph = build_graph(heightmap);
    let mut start: Option<NodeIndex> = None;
    let mut end: Option<NodeIndex> = None;
    let mut options: Vec<NodeIndex> = vec![];
    for i in 0..heightmap.num_rows() {
        for j in 0..heightmap.num_columns() {
            let idx = i * heightmap.num_columns() + j;
            let h = heightmap[(i, j)];
            match h {
                'S' => {
                    start = lookup_node(&graph, idx);
                }
                'E' => {
                    end = lookup_node(&graph, idx);
                }
                'a' => {
                    options.push(lookup_node(&graph, idx).unwrap());
                }
                _ => {}
            }
        }
    }
    options.push(start.unwrap());
    options
        .iter()
        .map(|&n| {
            let res = dijkstra(&graph, n, None, |_| 1);
            *res.get(&end.unwrap()).unwrap_or(&usize::MAX)
        })
        .min()
        .unwrap()
}

fn lookup_node(graph: &Graph<(), ()>, idx: usize) -> Option<NodeIndex> {
    graph.node_indices().find(|n| n.index() == idx)
}

fn convert_start_end(c: char) -> char {
    match c {
        'S' => 'a',
        'E' => 'z',
        _ => c,
    }
}

fn can_climb(src: char, dest: char) -> bool {
    convert_start_end(src) as u8 >= convert_start_end(dest) as u8 - 1
}

#[test]
#[ignore]
fn test_parse() {
    let input = "Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi";
    let parsed = generator_input(input);
    assert_eq!(parsed[(0, 0)], 'S');
    assert_eq!(parsed[(2, 5)], 'E');
    assert_eq!(parsed[(4, 7)], 'i');
    assert_eq!(31, part1(&parsed));
    assert_eq!(29, part2(&parsed));
}

#[test]
fn test_can_climb() {
    assert!(can_climb('a', 'b'));
    assert!(!can_climb('a', 'c'));
    assert!(can_climb('y', 'z'));
    assert!(can_climb('z', 'a'));
    assert!(!can_climb('x', 'z'));
    assert!(!can_climb('v', 'z'));
    assert!(can_climb('z', 'z'));
}
