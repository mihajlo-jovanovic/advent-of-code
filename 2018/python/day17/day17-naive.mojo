from python import Python, PythonObject

struct WaterSimulation:
    var grid: PythonObject
    var y_min: Int
    var y_max: Int
    var CLAY: String
    var WATER_SETTLED: String
    var WATER_FLOWING: String
    var SOURCE_X: Int
    var SOURCE_Y: Int

    # Initialize with 'inout self'
    fn __init__(out self, walls_file: String) raises:
        self.grid = Python.dict()
        self.y_min = 1000000
        self.y_max = -1000000
        self.CLAY = "#"
        self.WATER_SETTLED = "~"
        self.WATER_FLOWING = "|"
        self.SOURCE_X = 500
        self.SOURCE_Y = 0

        var builtins = Python.import_module("builtins")
        
        print("Loading grid from " + walls_file + "...")
        var f = builtins.open(walls_file, "r")
        var content = String(f.read())
        f.close()
        
        var lines = content.strip().split("\n")
        
        for line in lines:
            # line is "x,y"
            var parts = line.split(",")
            
            # Cast to Mojo Int immediately
            var x = Int(parts[0])
            var y = Int(parts[1])
            
            # Create key
            var key_str = "(" + String(x) + ", " + String(y) + ")"
            var key = Python.evaluate(key_str)
            
            self.grid[key] = self.CLAY
            
            if y < self.y_min:
                self.y_min = y
            if y > self.y_max:
                self.y_max = y
        
        print("Grid loaded. Y Range: " + String(self.y_min) + " to " + String(self.y_max))

    fn run(self) raises:
        self.flow(self.SOURCE_X, self.SOURCE_Y)

    fn flow(self, x: Int, y: Int) raises:
        if y > self.y_max:
            return

        var key_str = "(" + String(x) + ", " + String(y) + ")"
        var key = Python.evaluate(key_str)
        
        # Check Exists
        if key in self.grid:
            var val = String(self.grid[key])
            if val == self.CLAY or val == self.WATER_SETTLED:
                return

        # Mark Flowing
        if key not in self.grid:
            self.grid[key] = self.WATER_FLOWING

        # --- GRAVITY ---
        var next_y = y + 1
        var down_key_str = "(" + String(x) + ", " + String(next_y) + ")"
        var down_key = Python.evaluate(down_key_str)
        
        var down_is_flowing = False
        if down_key in self.grid:
            if String(self.grid[down_key]) == self.WATER_FLOWING:
                down_is_flowing = True
        
        if down_key not in self.grid or down_is_flowing:
            self.flow(x, next_y)

        # --- SPREAD ---
        var is_supported = False
        if down_key in self.grid:
            var val = String(self.grid[down_key])
            if val == self.CLAY or val == self.WATER_SETTLED:
                is_supported = True

        if is_supported:
            var left_res = self.scan_boundary(x, y, -1)
            var right_res = self.scan_boundary(x, y, 1)

            # Explicit Int() casting on results
            var left_bound = Int(left_res[0])
            var left_type = String(left_res[1])
            var right_bound = Int(right_res[0])
            var right_type = String(right_res[1])

            if left_type == "WALL" and right_type == "WALL":
                for fill_x in range(left_bound, right_bound + 1):
                    var fill_key_str = "(" + String(fill_x) + ", " + String(y) + ")"
                    var fill_key = Python.evaluate(fill_key_str)
                    self.grid[fill_key] = self.WATER_SETTLED
            else:
                for fill_x in range(left_bound, right_bound + 1):
                    var fill_key_str = "(" + String(fill_x) + ", " + String(y) + ")"
                    var fill_key = Python.evaluate(fill_key_str)
                    self.grid[fill_key] = self.WATER_FLOWING
                
                if left_type == "DROP":
                    self.flow(left_bound, y)
                if right_type == "DROP":
                    self.flow(right_bound, y)

    fn scan_boundary(self, start_x: Int, y: Int, direction: Int) raises -> PythonObject:
        var curr_x = start_x
        
        while True:
            var next_x = curr_x + direction
            var next_key_str = "(" + String(next_x) + ", " + String(y) + ")"
            var next_key = Python.evaluate(next_key_str)
            
            if next_key in self.grid:
                if String(self.grid[next_key]) == self.CLAY:
                    var ret_str = "(" + String(curr_x) + ", 'WALL')"
                    return Python.evaluate(ret_str)
            
            var floor_key_str = "(" + String(curr_x) + ", " + String(y + 1) + ")"
            var floor_key = Python.evaluate(floor_key_str)
            
            var floor_solid = False
            if floor_key in self.grid:
                var val = String(self.grid[floor_key])
                if val == self.CLAY or val == self.WATER_SETTLED:
                    floor_solid = True
            
            if not floor_solid:
                var ret_str = "(" + String(curr_x) + ", 'DROP')"
                return Python.evaluate(ret_str)

            curr_x = next_x

    fn count_water_tiles(self) raises -> Int:
        var count = 0
        for key in self.grid:
            var val = String(self.grid[key])
            if val == self.WATER_FLOWING or val == self.WATER_SETTLED:
                # key[1] is Python int, convert to Mojo Int
                var y = Int(key[1])
                if y >= self.y_min and y <= self.y_max:
                    count += 1
        return count

fn main() raises:
    var sys = Python.import_module("sys")
    _ = sys.setrecursionlimit(5000)

    print("Initializing Simulation...")
    # Point to the processed file
    var sim = WaterSimulation("walls.txt")
    
    print("Running Flow...")
    sim.run()
    
    var total = sim.count_water_tiles()
    print("Total water tiles: " + String(total))
