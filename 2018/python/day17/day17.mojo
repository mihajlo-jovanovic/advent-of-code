from python import Python, PythonObject
from collections import List

# Constants
alias EMPTY: Int8 = 0
alias CLAY: Int8 = 1
alias WATER_FLOWING: Int8 = 2
alias WATER_SETTLED: Int8 = 3

struct NativeWaterSim():
    var grid: List[Int8]
    var width: Int
    var height: Int
    var offset_x: Int
    var offset_y: Int
    var y_max: Int
    var y_min: Int
    var SOURCE_X: Int
    var SOURCE_Y: Int

    fn __init__(out self, walls_file: String) raises:
        self.SOURCE_X = 500
        self.SOURCE_Y = 0
        self.grid = List[Int8]()
        self.width = 0
        self.height = 0
        self.offset_x = 0
        self.offset_y = 0
        self.y_min = 10000000
        self.y_max = -10000000
        
        self.load_grid(walls_file)

    fn load_grid(mut self, walls_file: String) raises:
        var builtins = Python.import_module("builtins")
        print("Reading " + walls_file + "...")
        
        var f = builtins.open(walls_file, "r")
        var content = String(f.read())
        f.close()
        
        var lines = content.strip().split("\n")
        var coords = List[Int]() 
        
        var x_min = 10000000
        var x_max = -10000000

        for line in lines:
            var parts = line.split(",")
            var x = Int(parts[0])
            var y = Int(parts[1])
            
            coords.append(x)
            coords.append(y)
            
            if x < x_min: x_min = x
            if x > x_max: x_max = x
            if y < self.y_min: self.y_min = y
            if y > self.y_max: self.y_max = y

        var padding = 2
        self.offset_x = x_min - padding
        self.width = (x_max - x_min) + (padding * 2) + 1
        self.height = self.y_max + 1
        
        var total_cells = self.width * self.height
        print("Allocating Grid: " + String(self.width) + "x" + String(self.height) + " (" + String(total_cells) + " cells)")
        
        self.grid = List[Int8](capacity=total_cells)
        for i in range(total_cells):
            self.grid.append(EMPTY)
            
        for i in range(0, len(coords), 2):
            var cx = coords[i]
            var cy = coords[i+1]
            self.set(cx, cy, CLAY)
            
        print("Initialization Complete.")

    @always_inline
    fn get_idx(self, x: Int, y: Int) -> Int:
        return (y * self.width) + (x - self.offset_x)

    # 'get' does NOT need 'mut' because it only reads
    @always_inline
    fn get(self, x: Int, y: Int) -> Int8:
        return self.grid[self.get_idx(x, y)]

    @always_inline
    fn set(mut self, x: Int, y: Int, val: Int8):
        self.grid[self.get_idx(x, y)] = val

    fn run(mut self) raises:
        self.flow(self.SOURCE_X, self.SOURCE_Y)

    fn flow(mut self, x: Int, y: Int) raises:
        if y > self.y_max:
            return

        var val = self.get(x, y)
        if val == CLAY or val == WATER_SETTLED:
            return
        
        if val == EMPTY:
            self.set(x, y, WATER_FLOWING)
        
        # 1. GRAVITY
        var next_y = y + 1
        var down_val = self.get(x, next_y)
        
        if down_val == EMPTY:
            self.flow(x, next_y)
            down_val = self.get(x, next_y)

        # 2. SPREAD
        if down_val == CLAY or down_val == WATER_SETTLED:
            var left_stop = self.scan_left(x, y)
            var right_stop = self.scan_right(x, y)
            
            var l_bound = left_stop[0]
            var l_type = left_stop[1] 
            var r_bound = right_stop[0]
            var r_type = right_stop[1]

            if l_type == 1 and r_type == 1:
                for fx in range(l_bound, r_bound + 1):
                    self.set(fx, y, WATER_SETTLED)
            else:
                for fx in range(l_bound, r_bound + 1):
                    self.set(fx, y, WATER_FLOWING)
                
                if l_type == 0: self.flow(l_bound, y)
                if r_type == 0: self.flow(r_bound, y)

    # Helpers scan_left/right don't modify state, so they don't need 'mut'
    fn scan_left(self, start_x: Int, y: Int) -> List[Int]:
        var curr = start_x
        while True:
            var next_x = curr - 1
            if self.get(next_x, y) == CLAY:
                var res = List[Int](capacity=2)
                res.append(curr)
                res.append(1) # Wall
                return res^
            
            var floor_val = self.get(curr, y + 1)
            if floor_val != CLAY and floor_val != WATER_SETTLED:
                var res = List[Int](capacity=2)
                res.append(curr)
                res.append(0) # Drop
                return res^
                
            curr = next_x
            
    fn scan_right(self, start_x: Int, y: Int) -> List[Int]:
        var curr = start_x
        while True:
            var next_x = curr + 1
            if self.get(next_x, y) == CLAY:
                var res = List[Int](capacity=2)
                res.append(curr)
                res.append(1)
                return res^
            
            var floor_val = self.get(curr, y + 1)
            if floor_val != CLAY and floor_val != WATER_SETTLED:
                var res = List[Int](capacity=2)
                res.append(curr)
                res.append(0)
                return res^
                
            curr = next_x

    fn count_water_tiles(self) -> Int:
        var total = 0
        for i in range(len(self.grid)):
            var v = self.grid[i]
            # Map index back to Y to check bounds
            var y = i // self.width
            
            if y >= self.y_min and y <= self.y_max:
                if v == WATER_SETTLED:
                    total += 1
        return total

fn main() raises:
    var sys = Python.import_module("sys")
    _ = sys.setrecursionlimit(5000)

    # Initialize
    var sim = NativeWaterSim("walls.txt")
    
    print("Running Simulation...")
    sim.run()
    
    var total = sim.count_water_tiles()
    print("----------------------------")
    print("Total water tiles: " + String(total))
    print("----------------------------")
