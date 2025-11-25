from collections import deque
from dataclasses import dataclass
from typing import Dict, List, Optional, Set, Tuple, Union


@dataclass
class Unit:
    uid: int
    team: str  # 'G' or 'E'
    hp: int = 200


class GameBoard:
    def __init__(self, filename, elf_ap=3):
        self.unit_id_counter = 0
        # (row, col) -> 'G', 'E', '#', etc.
        self.grid: Dict[Tuple[int, int], Union[str, Unit]] = self._parse_grid(
            self._read_grid_from_file(filename)
        )
        self.round = 0
        self.elf_ap = elf_ap
        self.elf_died = False

    def _read_grid_from_file(self, filename):
        """Reads the grid string from a text file."""
        try:
            with open(filename, "r") as f:
                return f.read()
        except FileNotFoundError:
            print(f"Error: The file '{filename}' was not found.")
            return ""

    def _parse_grid(self, grid_str):
        """
        Parses a multi-line string grid into a dictionary.
        Key: (row, col) tuple
        Value: The character at that position
        """
        board = {}

        # strip() removes the leading/trailing newline from the triple quotes
        # splitlines() handles \n or \r\n automatically
        lines = grid_str.strip().splitlines()

        for r, line in enumerate(lines):
            for c, char in enumerate(line):
                if char in ("G", "E"):
                    board[(r, c)] = Unit(uid=self.unit_id_counter, team=char)
                    self.unit_id_counter += 1
                else:
                    # We store everything here.
                    # Optimization Tip: You could add "if char != '.':"
                    # to make this a sparse matrix (saving memory on huge maps).
                    board[(r, c)] = char

        return board

    def get_units_in_reading_order(self) -> List[Tuple[int, int]]:
        """
        Returns coordinates of all entities sorted by Row, then Column.
        """
        # Python's default sort on tuples works: (0, 5) comes before (1, 2)
        return sorted(
            [pos for pos, entity in self.grid.items() if isinstance(entity, Unit)]
        )

    def move_piece(
        self,
        board_state: Dict[Tuple[int, int], Union[str, Unit]],
        start: Tuple[int, int],
        end: Tuple[int, int],
    ):
        """
        Pure function: Returns a new board grid with a single piece moved from 'start' to 'end' position.
        """
        new_board = board_state.copy()
        unit_obj = new_board.get(start)
        if not unit_obj:
            return new_board
        new_board[start] = "."
        new_board[end] = unit_obj
        return new_board

    def attack(
        self,
        board_state: Dict[Tuple[int, int], Union[str, Unit]],
        attacker_pos: Tuple[int, int],
        target_pos: Tuple[int, int],
    ):
        """
        The unit deals damage equal to its attack power to the selected target,
        reducing its hit points by that amount. If this reduces its hit points to 0 or fewer,
        the selected target dies: its square becomes '.' and it takes no further turns.
        """
        new_board = board_state.copy()
        attacker = new_board.get(attacker_pos)
        target = new_board.get(target_pos)

        if not isinstance(attacker, Unit) or not isinstance(target, Unit):
            return new_board  # Should not happen if game logic is sound

        # Hardcoded attack power of 3 as per user's current request.
        attack_power = 3
        if attacker.team == "E":
            attack_power = self.elf_ap

        target.hp -= attack_power

        if target.hp <= 0:
            # print(f"eliminated: {target_pos}")
            if target.team == "E":
                print("--- Elf died :( ---")
                self.elf_died = True
            new_board[target_pos] = "."
        # else: the Unit object's hp is already updated, and since it's mutable
        # and already in new_board, no explicit re-assignment is needed.

        return new_board

    def print_board(self, board: Dict[Tuple[int, int], Union[str, Unit]]):
        """Prints the board state in a readable grid format."""
        if not board:
            print("Empty Board")
            return

        # Calculate dimensions based on keys
        rows = [pos[0] for pos in board.keys()]
        cols = [pos[1] for pos in board.keys()]
        max_r = max(rows)
        max_c = max(cols)

        print("\n--- Map State ---")
        for r in range(max_r + 1):
            line_chars = []
            for c in range(max_c + 1):
                entity = board.get((r, c), ".")
                if isinstance(entity, Unit):
                    line_chars.append(entity.team)
                else:
                    line_chars.append(str(entity))

            print("".join(line_chars))
        print()

    def in_range(
        self,
        board: Dict[Tuple[int, int], Union[str, Unit]],
        targets: Set[Tuple[int, int]],
    ) -> Optional[List[Tuple[int, int]]]:
        """
        Finds all open, adjacent squares which are in range: that are open ('.')
        and not blocked by walls or units.
        """
        offsets = [(-1, 0), (0, -1), (0, 1), (1, 0)]

        all_in_range = set()

        for target in targets:
            # print(f"target: {target}")
            result_list = [
                (target[0] + dx, target[1] + dy)
                for dx, dy in offsets
                if board.get((target[0] + dx, target[1] + dy)) == "."
            ]
            all_in_range.update(result_list)

        if not all_in_range:
            return None

        return list(all_in_range)

    def nearest(
        self,
        board: Dict[Tuple[int, int], Union[str, Unit]],
        start: Tuple[int, int],
        targets: Set[Tuple[int, int]],
    ) -> List[Tuple[int, int]]:
        """
        Performs a Single BFS to find which of the 'targets' are closest to 'start'.
        Returns a List of positions (in case of ties).
        Returns empty list if no targets are reachable.
        """
        if not targets:
            return []

        visited = {start}
        queue = deque([(start, 0)])

        min_dist_found = float("inf")
        nearest_targets = []

        offsets = [(-1, 0), (0, -1), (0, 1), (1, 0)]

        while queue:
            # print(f"dist: {min_dist_found}")
            curr, dist = queue.popleft()

            # Optimization: if we have moved beyond the distance of the closest target found,
            # we can stop searching. All subsequent targets will be further away.
            if dist > min_dist_found:
                break

            # Check neighbors
            for dr, dc in offsets:
                neighbor = (curr[0] + dr, curr[1] + dc)

                if neighbor in visited:
                    continue

                # Case A: found a target
                if neighbor in targets:
                    if dist + 1 <= min_dist_found:
                        min_dist_found = dist + 1
                        nearest_targets.append(neighbor)
                    visited.add(neighbor)
                    continue

                # Case B. It's an open path, keep searching
                if board.get(neighbor) == ".":
                    visited.add(neighbor)
                    queue.append((neighbor, dist + 1))

        return nearest_targets

    def get_selected_target(
        self,
        board: Dict[Tuple[int, int], Union[str, Unit]],
        potential_targets: List[Tuple[int, int]],
    ) -> Optional[Tuple[int, int]]:
        """
        Returns the selected target. Only called when we know we're in range of at least one target.
        """
        if not potential_targets:
            return None

        target_options = []
        for pos in potential_targets:
            unit = board.get(pos)
            if isinstance(unit, Unit):
                target_options.append((unit.hp, pos))

        # Sort by HP (lowest first), then by position in reading order (row, then col)
        target_options.sort()

        if target_options:
            # Return the position of the best target
            return target_options[0][1]

        return None

    def get_target_in_range(
        self,
        board: Dict[Tuple[int, int], Union[str, Unit]],
        unit_pos: Tuple[int, int],
        enemies: List[Tuple[int, int]],
    ) -> Optional[Tuple[int, int]]:
        offsets = [(-1, 0), (0, -1), (0, 1), (1, 0)]
        potential_targets = []
        for dr, dc in offsets:
            neighbor = (unit_pos[0] + dr, unit_pos[1] + dc)
            if neighbor in enemies:
                potential_targets.append(neighbor)
        return self.get_selected_target(board, potential_targets)

    def get_next_move(
        self,
        board: Dict[Tuple[int, int], Union[str, Unit]],
        start: Tuple[int, int],
        end: Tuple[int, int],
    ) -> List[Tuple[int, int]]:
        """
        Calculates the shortest path from 'start' to 'end' using Backwards BFS.
        """
        if start == end:
            return []

        queue = deque([(end, 0)])
        dist_map: Dict[Tuple[int, int], int] = {end: 0}
        offsets = [(-1, 0), (0, -1), (0, 1), (1, 0)]
        found_start = False

        while queue:
            curr, dist = queue.popleft()
            if curr == start:
                found_start = True
                break

            for dr, dc in offsets:
                neighbor = (curr[0] + dr, curr[1] + dc)
                is_walkable = (board.get(neighbor) == ".") or (neighbor == start)

                if neighbor not in dist_map and is_walkable:
                    dist_map[neighbor] = dist + 1
                    queue.append((neighbor, dist + 1))

        if not found_start:
            return []

        start_dist = dist_map[start]
        valid_steps = []
        for dr, dc in offsets:
            n = (start[0] + dr, start[1] + dc)
            if n in dist_map and dist_map[n] == start_dist - 1:
                valid_steps.append(n)

        return valid_steps

    def _reconstruct_path(self, came_from, current):
        path = []
        while current:
            path.append(current)
            current = came_from[current]
        return path[::-1]  # Reverse to get Start -> End

    def get_sum_of_all_hp(self, board: Dict[Tuple[int, int], Union[str, Unit]]):
        """
        Prints totla sum of hp for all units.
        """
        total_hp = 0
        for entity in board.values():
            if isinstance(entity, Unit):
                # print(f"hp: {entity.hp}")
                total_hp += entity.hp
        return total_hp

    def is_combat_over(self) -> bool:
        """
        Returns true if only one of the unit types is present on the board, False otherwise.
        """
        # Get all units currently on the board
        units_on_board = [
            entity for entity in self.grid.values() if isinstance(entity, Unit)
        ]

        # Determine the unique teams present
        unique_teams = {unit.team for unit in units_on_board}

        # Combat is over if there is 0 or 1 unique team left
        return len(unique_teams) <= 1

    def play_round(self):
        units = self.get_units_in_reading_order()
        current_board = self.grid

        # print(f"--- Round {self.round} Start ---")

        for unit_pos in units:
            unit = current_board.get(unit_pos)

            if not isinstance(unit, Unit):
                continue

            enemy_type = "E" if unit.team == "G" else "G"

            enemies = {
                pos
                for pos, entity in current_board.items()
                if isinstance(entity, Unit) and entity.team == enemy_type
            }
            if not enemies:
                # self.print_sum_of_all_hp(current_board)
                break
            # If there are potential targets adjacent, attack the best one
            selected_target = self.get_target_in_range(current_board, unit_pos, enemies)
            if selected_target:
                # Attack the selected target
                current_board = self.attack(current_board, unit_pos, selected_target)
                # The unit has acted (attacked), so move to the next unit
                continue
            in_range = self.in_range(current_board, enemies)
            nearest = self.nearest(current_board, unit_pos, in_range)
            if nearest:
                # print(f"nearest: {nearest}")
                nearest.sort()
                chosen = nearest[0]
                steps = self.get_next_move(current_board, unit_pos, chosen)
                if steps:
                    steps.sort()
                    next_pos = steps[0]
                    # print(f"Going to {next_pos}")
                    current_board = self.move_piece(current_board, unit_pos, next_pos)
                    selected_target = self.get_target_in_range(
                        current_board, next_pos, enemies
                    )
                    if selected_target:
                        # Attack the selected target
                        current_board = self.attack(
                            current_board, next_pos, selected_target
                        )
                else:
                    print(f"{unit.team} at {unit_pos} had not path to {chosen}")
            # else:
            # print(f"{unit.team} at {unit_pos} found no reachable targets")

        self.grid = current_board
        self.round += 1


def p1(filename: str):
    game_board = GameBoard(filename)

    while not game_board.is_combat_over():
        game_board.play_round()
    return (game_board.round - 1) * game_board.get_sum_of_all_hp(game_board.grid)


def main():
    print(f"Part 1: {p1('day15.txt')}")
    # Part 2
    for ap in range(4, 50):
        game_board = GameBoard("day15.txt", ap)
        print(f"--- Trying attack power {ap}... ---")

        while not game_board.is_combat_over():
            game_board.play_round()
            if game_board.elf_died:
                break

        if game_board.elf_died:
            continue
        else:
            print(
                f"Part 2: {
                    (game_board.round - 1)
                    * game_board.get_sum_of_all_hp(game_board.grid)
                }"
            )
            break


if __name__ == "__main__":
    main()
