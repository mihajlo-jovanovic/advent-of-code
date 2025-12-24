####################################
# DOES NOT WORK!!! TAKES FOREVER
####################################

from collections import Set, List
from pathlib import Path
from hashlib import Hashable
from hashlib.hasher import Hasher


alias MAX_IDX = 2


@fieldwise_init
struct Coord(Hashable, EqualityComparable, Stringable, Copyable, Movable, ImplicitlyCopyable):
    var x: Int64
    var y: Int64

    fn __hash__[H: Hasher](self, mut hasher: H):
        hasher._update_with_simd(self.x * 1000 + self.y)

    fn __eq__(self, other: Self) -> Bool:
        return self.x == other.x and self.y == other.y

    fn __ne__(self, other: Self) -> Bool:
        return not self.__eq__(other)

    fn __str__(self) -> String:
        return String(self.x) + "," + String(self.y)


fn rotate_cw(coord: Coord) -> Coord:
    return Coord(coord.y, MAX_IDX - coord.x)


fn rotate_ccw(coord: Coord) -> Coord:
    return Coord(MAX_IDX - coord.y, coord.x)


fn flip_horiz(coord: Coord) -> Coord:
    return Coord(MAX_IDX - coord.x, coord.y)


fn flip_vert(coord: Coord) -> Coord:
    return Coord(coord.x, MAX_IDX - coord.y)


fn offset(coord: Coord, dx: Int64, dy: Int64) -> Coord:
    return Coord(coord.x + dx, coord.y + dy)


fn transform_shape(shape: Set[Coord], transform_id: Int) -> Set[Coord]:
    var result = Set[Coord]()
    for coord in shape:
        var new_coord: Coord
        if transform_id == 0:
            new_coord = coord
        elif transform_id == 1:
            new_coord = rotate_cw(coord)
        elif transform_id == 2:
            new_coord = rotate_ccw(coord)
        elif transform_id == 3:
            new_coord = rotate_cw(rotate_cw(coord))
        elif transform_id == 4:
            new_coord = rotate_cw(flip_horiz(coord))
        elif transform_id == 5:
            new_coord = rotate_ccw(flip_horiz(coord))
        elif transform_id == 6:
            new_coord = rotate_cw(rotate_cw(flip_horiz(coord)))
        elif transform_id == 7:
            new_coord = rotate_cw(flip_vert(coord))
        elif transform_id == 8:
            new_coord = rotate_ccw(flip_vert(coord))
        else:
            new_coord = rotate_cw(rotate_cw(flip_vert(coord)))
        result.add(new_coord)
    return result^


fn offset_shape(shape: Set[Coord], dx: Int64, dy: Int64) -> Set[Coord]:
    var result = Set[Coord]()
    for c in shape:
        result.add(offset(c, dx, dy))
    return result^


fn can_place(region: Set[Coord], shape: Set[Coord]) -> Bool:
    for c in shape:
        if c in region:
            return False
    return True


fn union_sets(a: Set[Coord], b: Set[Coord]) -> Set[Coord]:
    var result = Set[Coord]()
    for c in a:
        result.add(c)
    for c in b:
        result.add(c)
    return result^


fn parse_shape(lines: List[String]) -> Set[Coord]:
    var result = Set[Coord]()
    for y in range(len(lines)):
        var line = lines[y]
        for x in range(len(line)):
            if line[x] == "#":
                result.add(Coord(Int64(x), Int64(y)))
    return result^


@fieldwise_init
struct Region(Copyable, Movable):
    var max_x: Int
    var max_y: Int
    var quantities: List[Int]


struct State:
    var region: Set[Coord]
    var quantities: List[Int]
    var max_x: Int
    var max_y: Int

    fn __init__(out self, max_x: Int, max_y: Int, quantities: List[Int]):
        self.region = Set[Coord]()
        self.max_x = max_x
        self.max_y = max_y
        self.quantities = quantities.copy()

    fn is_done(self) -> Bool:
        for i in range(len(self.quantities)):
            if self.quantities[i] != 0:
                return False
        return True

    fn first_nonzero_index(self) -> Int:
        for i in range(len(self.quantities)):
            if self.quantities[i] > 0:
                return i
        return -1

    fn add_shape(mut self, shape: Set[Coord]):
        for c in shape:
            self.region.add(c)

    fn remove_shape(mut self, shape: Set[Coord]):
        for c in shape:
            self.region.discard(c)


fn get_all_placements(
    gift: Set[Coord], max_x: Int, max_y: Int
) -> List[Set[Coord]]:
    var result = List[Set[Coord]]()
    var seen = Set[String]()

    for transform_id in range(10):
        var transformed = transform_shape(gift, transform_id)
        for dx in range(max_x - 2):
            for dy in range(max_y - 2):
                var placed = offset_shape(transformed, Int64(dx), Int64(dy))
                var key = shape_to_key(placed)
                if key not in seen:
                    seen.add(key)
                    result.append(placed^)
    return result^


fn shape_to_key(shape: Set[Coord]) -> String:
    var coords = List[Coord]()
    for c in shape:
        coords.append(c)

    for i in range(len(coords)):
        for j in range(i + 1, len(coords)):
            if coords[j].x < coords[i].x or (
                coords[j].x == coords[i].x and coords[j].y < coords[i].y
            ):
                var tmp = coords[i].copy()
                coords[i] = coords[j].copy()
                coords[j] = tmp

    var key = String("")
    for i in range(len(coords)):
        key += String(coords[i]) + ";"
    return key


fn backtrack(
    mut state: State,
    gifts: List[Set[Coord]],
    placement_cache: List[List[Set[Coord]]],
) -> Bool:
    if state.is_done():
        return True

    var idx = state.first_nonzero_index()
    if idx == -1:
        return True

    ref placements = placement_cache[idx]

    for i in range(len(placements)):
        ref shape = placements[i]
        if can_place(state.region, shape):
            state.add_shape(shape)
            state.quantities[idx] -= 1
            if backtrack(state, gifts, placement_cache):
                return True
            state.remove_shape(shape)
            state.quantities[idx] += 1
    return False


fn is_feasible(gifts: List[Set[Coord]], region: Region) -> Bool:
    var total_area = region.max_x * region.max_y
    var required_area = 0
    for i in range(len(region.quantities)):
        required_area += region.quantities[i] * len(gifts[i])
    return total_area > required_area


fn reduce_by_packing(region: Region) -> Region:
    var q0 = region.quantities[0]
    var q1 = region.quantities[1]
    var q2 = region.quantities[2]
    var q3 = region.quantities[3]
    var q4 = region.quantities[4]
    var q5 = region.quantities[5]

    var m1 = min(q0, q4)
    var m2 = min(q1, q5)
    var m3 = q3 // 2

    var total_4_by_4 = m1 + m2 + m3
    var total_per_row = region.max_y // 4

    var rows_needed: Int
    if total_per_row > 0:
        if total_4_by_4 % total_per_row == 0:
            rows_needed = total_4_by_4 // total_per_row
        else:
            rows_needed = total_4_by_4 // total_per_row + 1
    else:
        rows_needed = 0

    var new_max_x = region.max_x - rows_needed * 4

    var new_quantities = List[Int]()
    new_quantities.append(q0 - m1)
    new_quantities.append(q1 - m2)
    new_quantities.append(q2)
    new_quantities.append(q3 - 2 * m3)
    new_quantities.append(q4 - m1)
    new_quantities.append(q5 - m2)

    return Region(new_max_x, region.max_y, new_quantities^)


fn parse_gifts(filepath: String) raises -> List[Set[Coord]]:
    var content = Path(filepath).read_text()
    var parts = content.split("\n\n")

    var gifts = List[Set[Coord]]()

    for i in range(6):
        var section = parts[i]
        var lines = section.split("\n")
        var shape_lines = List[String]()
        for j in range(1, len(lines)):
            if len(lines[j]) > 0:
                shape_lines.append(String(lines[j]))
        gifts.append(parse_shape(shape_lines^))

    return gifts^


fn parse_regions(filepath: String) raises -> List[Region]:
    var content = Path(filepath).read_text()
    var parts = content.split("\n\n")

    var regions = List[Region]()
    var region_lines = parts[6].split("\n")
    for i in range(len(region_lines)):
        var line = region_lines[i]
        if len(line) == 0:
            continue
        var region_parts = line.split(": ")
        var dims_str = region_parts[0]
        var dims = dims_str.split("x")
        var max_x = Int(dims[0])
        var max_y = Int(dims[1])

        var quantities = List[Int]()
        var qty_parts = region_parts[1].split(" ")
        for j in range(len(qty_parts)):
            var q = qty_parts[j]
            if len(q) > 0:
                quantities.append(Int(q))

        regions.append(Region(max_x, max_y, quantities^))

    return regions^


fn solve(gifts: List[Set[Coord]], regions: List[Region]) raises -> Int:
    var count = 0

    for i in range(len(regions)):
        ref region = regions[i]
        if not is_feasible(gifts, region):
            continue

        var reduced = reduce_by_packing(region)
        var state = State(reduced.max_x, reduced.max_y, reduced.quantities)

        var placement_cache = List[List[Set[Coord]]]()
        for j in range(len(gifts)):
            placement_cache.append(
                get_all_placements(gifts[j], reduced.max_x, reduced.max_y)
            )

        if backtrack(state, gifts, placement_cache):
            count += 1

    return count


fn main() raises:
    var gifts = parse_gifts("resources/sample.txt")
    var regions = parse_regions("resources/sample.txt")
    var answer = solve(gifts, regions)
    print("Part 1:", answer)
