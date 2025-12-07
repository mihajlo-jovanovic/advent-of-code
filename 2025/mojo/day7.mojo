from collections import Dict, Set, List
from hashlib.hasher import Hasher
from pathlib import Path


@fieldwise_init
struct Point(Copyable, Movable, Hashable, EqualityComparable, Stringable):
    var x: Int
    var y: Int
    
    fn __hash__[H: Hasher](self, mut hasher: H):
        hasher.update(self.x)
        hasher.update(self.y)
    
    fn __eq__(self, other: Point) -> Bool:
        return self.x == other.x and self.y == other.y
    
    fn __ne__(self, other: Point) -> Bool:
        return not self.__eq__(other)
    
    fn __str__(self) -> String:
        return "(" + String(self.x) + ", " + String(self.y) + ")"


@fieldwise_init
struct GridData(Movable):
    var grid: Dict[Point, String]
    var max_y: Int


fn parse_grid(filename: String) raises -> GridData:
    var data = GridData(Dict[Point, String](), 0)
    var content = Path(filename).read_text()
    var lines = content.split("\n")
    
    for y in range(len(lines)):
        var line = lines[y]
        for x in range(len(line)):
            var char = line[x]
            if char != ".":
                data.grid[Point(x, y)] = String(char)
        if y > data.max_y:
            data.max_y = y
    
    return data^


fn find_start(grid: Dict[Point, String]) raises -> Point:
    for key in grid.keys():
        if grid[key] == "S":
            return key.copy()
    raise Error("No start position found")


fn get_splitters(grid: Dict[Point, String]) -> Set[Point]:
    var splitters = Set[Point]()
    for key in grid.keys():
        try:
            if grid[key] == "^":
                splitters.add(key.copy())
        except:
            pass
    return splitters^


fn count_splits(grid: Dict[Point, String], max_y: Int) raises -> Int:
    var start = find_start(grid)
    var splitters = get_splitters(grid)
    
    var split_count = 0
    var active_beams = List[Int]()
    active_beams.append(start.x)
    var y = start.y
    
    while len(active_beams) > 0 and y <= max_y:
        y += 1
        if y > max_y:
            break
        
        var next_beams = List[Int]()
        var seen = Set[Int]()
        for i in range(len(active_beams)):
            var x = active_beams[i]
            if Point(x, y) in splitters:
                split_count += 1
                if x - 1 not in seen:
                    next_beams.append(x - 1)
                    seen.add(x - 1)
                if x + 1 not in seen:
                    next_beams.append(x + 1)
                    seen.add(x + 1)
            else:
                if x not in seen:
                    next_beams.append(x)
                    seen.add(x)
        
        active_beams = next_beams^
    
    return split_count


fn count_paths_memo(
    x: Int,
    y: Int,
    max_y: Int,
    splitters: Set[Point],
    mut memo: Dict[Point, Int],
) raises -> Int:
    var curr_x = x
    var curr_y = y
    
    while curr_y < max_y:
        curr_y += 1
        if Point(curr_x, curr_y) in splitters:
            var key = Point(curr_x, curr_y)
            if key in memo:
                return memo[key]
            var left_paths = count_paths_memo(curr_x - 1, curr_y, max_y, splitters, memo)
            var right_paths = count_paths_memo(curr_x + 1, curr_y, max_y, splitters, memo)
            var result = left_paths + right_paths
            memo[key.copy()] = result
            return result
    
    return 1


fn count_unique_paths(grid: Dict[Point, String], max_y: Int) raises -> Int:
    var start = find_start(grid)
    var splitters = get_splitters(grid)
    var memo = Dict[Point, Int]()
    return count_paths_memo(start.x, start.y, max_y, splitters, memo)


fn main() raises:
    var data = parse_grid("day7.txt")
    
    print("Grid parsed, max_y:", data.max_y)
    print("Split count:", count_splits(data.grid, data.max_y))
    print("Unique paths:", count_unique_paths(data.grid, data.max_y))
