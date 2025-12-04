class MyGrid:
    def __init__(self, lines):
        self.grid = {}

        for y, ln in enumerate(lines):
            for x, char in enumerate(ln):
                self.grid[(x, y)] = char

    def count_neighbors(self, x, y):
        count = 0
        directions = [
            (-1, -1),
            (0, -1),
            (1, -1),
            (-1, 0),
            (1, 0),
            (-1, 1),
            (0, 1),
            (1, 1),
        ]

        for dx, dy in directions:
            pos = (x + dx, y + dy)
            if self.grid.get(pos) == "@":
                count += 1

        return count

    def rolls_that_can_be_removed(self):
        return [
            pos
            for pos in self.grid
            if self.grid.get(pos) == "@" and self.count_neighbors(pos[0], pos[1]) < 4
        ]

    def remove_rolls(self, to_remove):
        for pos in to_remove:
            self.grid[pos] = "."

    def p1(self):
        return len(self.rolls_that_can_be_removed())

    def p2(self):
        count = 0

        while True:
            self.print_grid()
            can_be_removed = self.rolls_that_can_be_removed()

            if not can_be_removed:
                break

            self.remove_rolls(can_be_removed)
            count += len(can_be_removed)

        return count

    def print_grid(self):
        """Prints the grid to standard out, formatting it nicely."""
        if not self.grid:
            print("Grid is empty.")
            return

        min_x = min(pos[0] for pos in self.grid)
        max_x = max(pos[0] for pos in self.grid)
        min_y = min(pos[1] for pos in self.grid)
        max_y = max(pos[1] for pos in self.grid)

        for y in range(min_y, max_y + 1):
            for x in range(min_x, max_x + 1):
                print(self.grid.get((x, y), " "), end="")
            print()  # Newline after each row


lines = open("sample.txt").read().strip().split("\n")
my_grid = MyGrid(lines)

# print(f"Position [2, 0]: {my_grid.grid.get(2, 0)}")
# print(f"Total neighbors: {my_grid.count_neighbors(1, 0)}")

print(f"Part 1: {my_grid.p1()}")
print(f"Part 2: {my_grid.p2()}")
