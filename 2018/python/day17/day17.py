import re
import sys
import time

# Increase recursion limit for deep simulation grids
sys.setrecursionlimit(3000)


class WaterSimulation:
    def __init__(self, raw_input):
        self.grid = {}  # Sparse grid: (x,y) -> char
        self.y_min = float("inf")
        self.y_max = float("-inf")

        # 1. Parse the input
        self.parse_intervals(raw_input)

        # Constants
        self.SOURCE = (500, 0)  # Standard source for this coordinate range
        self.CLAY = "#"
        self.WATER_SETTLED = "~"  # Pools
        self.WATER_FLOWING = "|"  # Falling/Moving

    def parse_intervals(self, lines):
        pattern = re.compile(r"([xy])=(\d+)(?:\.\.(\d+))?")
        for line in lines:
            matches = pattern.findall(line)
            ranges = {"x": [], "y": []}
            for axis, start, end in matches:
                start, end = int(start), int(end) if end else int(start)
                ranges[axis] = range(start, end + 1)

            for x in ranges["x"]:
                for y in ranges["y"]:
                    self.grid[(x, y)] = "#"
                    self.y_min = min(self.y_min, y)
                    self.y_max = max(self.y_max, y)

    def run(self):
        # Start the recursive flow from the source
        self.flow(self.SOURCE[0], self.SOURCE[1])

    def flow(self, x, y):
        # Stop if we fall off the bottom of the world
        if y > self.y_max:
            return

        # Stop if we hit clay or existing settled water
        if (x, y) in self.grid and self.grid[(x, y)] in (self.CLAY, self.WATER_SETTLED):
            return

        # Mark current spot as flowing
        if (x, y) not in self.grid:
            self.grid[(x, y)] = self.WATER_FLOWING

        # --- STEP 1: GRAVITY ---
        # Try to move down first
        if (x, y + 1) not in self.grid or self.grid[(x, y + 1)] == self.WATER_FLOWING:
            self.flow(x, y + 1)

        # --- STEP 2: LATERAL SPREAD ---
        # If the cell BELOW is solid (Clay or Settled Water), we need to spread
        # Note: We check grid state again because the recursive call above
        # might have turned the space below into Settled Water.
        below = (x, y + 1)
        is_supported = below in self.grid and self.grid[below] in (
            self.CLAY,
            self.WATER_SETTLED,
        )

        if is_supported:
            # Find left and right boundaries
            left_bound, left_type = self.scan_boundary(x, y, -1)
            right_bound, right_type = self.scan_boundary(x, y, 1)

            # If bounded by walls on BOTH sides, it's a pool
            if left_type == "WALL" and right_type == "WALL":
                # Fill the range with Settled Water
                for fill_x in range(left_bound, right_bound + 1):
                    self.grid[(fill_x, y)] = self.WATER_SETTLED
            else:
                # It's a flowing stream (spills over)
                # Fill the range with Flowing Water
                for fill_x in range(left_bound, right_bound + 1):
                    self.grid[(fill_x, y)] = self.WATER_FLOWING

                # Recursively flow down from the spill points (DROP-OFFS)
                if left_type == "DROP":
                    self.flow(left_bound, y)  # This x is the spill point
                if right_type == "DROP":
                    self.flow(right_bound, y)  # This x is the spill point

    def scan_boundary(self, start_x, y, direction):
        """
        Moves left (-1) or right (+1) to find the edge.
        Returns: (x_coordinate_of_edge, type_of_edge)
        Type is 'WALL' (hit clay) or 'DROP' (floor fell out)
        """
        curr_x = start_x
        while True:
            # Check for Wall ahead
            next_x = curr_x + direction
            if (next_x, y) in self.grid and self.grid[(next_x, y)] == self.CLAY:
                return (curr_x, "WALL")  # Return the last liquid cell

            # Check for Floor Drop below current cell
            # The floor is valid if it's Clay or Settled Water
            floor = (curr_x, y + 1)
            if floor not in self.grid or self.grid[floor] not in (
                self.CLAY,
                self.WATER_SETTLED,
            ):
                return (curr_x, "DROP")  # This cell is the drop point

            curr_x += direction

    def visualize(self, padding=1):
        # Calculate bounds for printing
        xs = [k[0] for k in self.grid.keys()]
        min_x, max_x = min(xs) - padding, max(xs) + padding
        min_y, max_y = 0, self.y_max + padding  # Start from 0 to see the source path

        print(f"\n--- Simulation Result (y={min_y} to {max_y}) ---")
        for y in range(min_y, max_y + 1):
            row = f"{y:03d} "
            for x in range(min_x, max_x + 1):
                if (x, y) == self.SOURCE:
                    row += "+"  # Source
                elif (x, y) in self.grid:
                    row += self.grid[(x, y)]
                else:
                    row += "."
            print(row)

    def count_water_tiles(self):
        # Usually, these problems ask for water count within y_min and y_max
        count = 0
        for (x, y), val in self.grid.items():
            if self.y_min <= y <= self.y_max and val in ("~"):
                count += 1
        return count


# --- INPUT DATA ---
# input_data = [
#     "x=495, y=2..7",
#     "y=7, x=495..501",
#     "x=501, y=3..7",
#     "x=498, y=2..4",
#     "x=506, y=1..2",
#     "x=498, y=10..13",
#     "x=504, y=10..13",
#     "y=13, x=498..504",
# ]

# Read input data from a file
input_data = open("day17.txt").read().strip().split("\n")

# --- EXECUTION ---
sim = WaterSimulation(input_data)
stime = time.time_ns()
sim.run()
etime = time.time_ns()
sim.visualize()

print(f"\nTotal water tiles (settled + flowing): {sim.count_water_tiles()}")
print(f"--- took {(etime - stime) // 1000000} ms")
