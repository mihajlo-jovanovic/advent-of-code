from collections import deque
from typing import Dict, List, Optional, Set, Tuple


class GameBoard:
    def __init__(self, filename):
        # (row, col) -> 'G', 'E', '#', etc.
        self.grid: Dict[Tuple[int, int], str] = self._parse_grid(
            self._read_grid_from_file(filename)
        )

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
                # We store everything here.
                # Optimization Tip: You could add "if char != '.':"
                # to make this a sparse matrix (saving memory on huge maps).
                board[(r, c)] = char

        return board

    def is_blocked(self, pos: Tuple[int, int]) -> bool:
        # Assume implicit open space if not in dict,
        # OR check if the char at pos is a Wall/Unit
        entity = self.grid.get(pos)
        return entity in ["#", "G", "E"]  # Example blockers

    # REQUIREMENT 1: Reading Order
    def get_units_in_reading_order(self) -> List[Tuple[int, int]]:
        """
        Returns coordinates of all entities sorted by Row, then Column.
        """
        # Python's default sort on tuples works: (0, 5) comes before (1, 2)
        return sorted([pos for pos in self.grid if self.grid[pos] not in ["#", "."]])

    def move_piece(
        self,
        board_state: Dict[Tuple[int, int], str],
        start: Tuple[int, int],
        end: Tuple[int, int],
    ):
        """
        Pure function: Returns a new board grid with a single piece moved from 'start' to 'end' position.
        """
        new_board = board_state.copy()
        unit_char = new_board.get(start)
        if not unit_char:
            return new_board
        new_board[start] = "."
        new_board[end] = unit_char
        return new_board

    # --- NEW: Pretty Print ---

    def print_board(self, board: Dict[Tuple[int, int], str]):
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
                # Default to '.' if key is missing (useful if you switch to sparse storage later)
                line_chars.append(board.get((r, c), "."))
            print("".join(line_chars))
        print()

    def in_range(
        self, board: Dict[Tuple[int, int], str], targets: Set[Tuple[int, int]]
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
        board: Dict[Tuple[int, int], str],
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
        self, board: Dict[Tuple[int, int], str], unit_pos: Tuple[int, int]
    ) -> Tuple[int, int]:
        """
        Returns the selected target. Only called when we know we're in range of at least one target.
        """
        pass

    def get_next_move(
        self,
        board: Dict[Tuple[int, int], str],
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
                is_walkable = (board.get(neighbor) ==
                               ".") or (neighbor == start)

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

    def play_round(self):
        units = self.get_units_in_reading_order()
        current_board = self.grid

        print("--- Round Start ---")

        for unit_pos in units:
            u_type = current_board.get(unit_pos)
            # print(f"Unit type: {u_type}")

            enemy_type = "E" if u_type == "G" else "G"
            # print(f"enemy type: {enemy_type}")

            enemies = {pos for pos, char in current_board.items()
                       if char == enemy_type}
            # print(f"enemies: {enemies}")
            # Check if 'unit_pos' is already next to one of the 'enemies' and if so continue
            offsets = [(-1, 0), (0, -1), (0, 1), (1, 0)]
            do_not_move = False
            for dr, dc in offsets:
                neighbor = (unit_pos[0] + dr, unit_pos[1] + dc)
                if neighbor in enemies:
                    do_not_move = True

            if do_not_move:
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
                    current_board = self.move_piece(
                        current_board, unit_pos, next_pos)
                else:
                    print(f"{u_type} at {unit_pos} had not path to {chosen}")
            else:
                print(f"{u_type} at {unit_pos} found no reachable targets")

        self.grid = current_board
        self.print_board(self.grid)


def main():
    filename = "sample3.txt"
    game_board = GameBoard(filename)
    # reading_order = game_board.get_units_in_reading_order()
    # print(reading_order)

    #     res = game_board.shortest_path((1, 1), {(3, 4)})
    #     print(f"shortest path: {res}")
    #     in_range = game_board.in_range({(3, 4)})
    #     print(f"in range: {in_range}")
    #     nearest = game_board.nearest(game_board.grid, (1, 2), in_range)
    #
    # print(f"nearest: {nearest}")
    #     chosen = sorted(nearest)[0]
    #     print(f"chosen: {chosen}")
    #     valid_steps = game_board.get_next_move(game_board.grid, (1, 1), (1, 3))
    #     print(f"valid steps: {valid_steps}")
    #
    # next_move = game_board.get_next_move(game_board.grid, (1, 2), (2, 4))
    # print(f"next move: {sorted(next_move)[0]}")
    for _ in range(10):
        game_board.play_round()


if __name__ == "__main__":
    main()
