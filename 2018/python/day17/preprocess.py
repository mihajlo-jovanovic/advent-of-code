import re


def export_grid(input_file, output_file):
    print(f"Reading {input_file}...")
    with open(input_file, "r") as f:
        lines = f.readlines()

    walls = set()
    pattern = re.compile(r"([xy])=(\d+)(?:\.\.(\d+))?")

    print("Parsing intervals...")
    for line in lines:
        matches = pattern.findall(line)
        for axis, start, end in matches:
            start_val = int(start)
            end_val = int(end) if end else start_val

            x_range = range(0)
            y_range = range(0)

            if axis == "x":
                x_range = range(start_val, end_val + 1)
                # The other axis wasn't captured in this specific regex group structure
                # effectively, so we need to parse the line carefully.
                # Let's use the exact parsing logic that works:
                pass

    # Simplified Robust Parser for the specific format
    # Format: x=495, y=2..7
    final_walls = []

    for line in lines:
        # Split by comma
        parts = line.strip().split(", ")
        # Parse first part
        axis1, val1 = parts[0].split("=")
        # Parse second part
        axis2, val2 = parts[1].split("=")

        # Helper to get range
        def get_range(val_str):
            if ".." in val_str:
                s, e = map(int, val_str.split(".."))
                return range(s, e + 1)
            else:
                return range(int(val_str), int(val_str) + 1)

        r1 = get_range(val1)
        r2 = get_range(val2)

        if axis1 == "x":
            xs, ys = r1, r2
        else:
            xs, ys = r2, r1

        for x in xs:
            for y in ys:
                final_walls.append(f"{x},{y}")

    print(f"Writing {len(final_walls)} wall blocks to {output_file}...")
    with open(output_file, "w") as f:
        f.write("\n".join(final_walls))
    print("Done.")


if __name__ == "__main__":
    # Ensure day17.txt exists
    export_grid("day17.txt", "walls.txt")
