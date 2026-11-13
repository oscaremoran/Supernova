"""Builds Supernova's ferrite conveyor frames from the game's conveyor layout.

The conveyor has 5 shapes (which sides feed in) x 4 animation frames, and the shapes must
match the game's connection logic exactly. This takes the vanilla frames' layout (rails, belt,
moving ribs) and repaints it in the Supernova palette.

Usage (needs the game's atlas extracted, plus a region list from the atlas):
    python3 tools/conveyor.py <sprites.png> <regions.txt>
where regions.txt has lines: "conveyor-I-J page x y w h rotate"
"""
import os
import sys
from PIL import Image

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "assets", "sprites", "blocks", "distribution")

# Vanilla gray levels (red channel) -> Supernova colors.
STOPS = [
    (74, (44, 40, 46)),     # belt: dark
    (110, (86, 112, 178)),  # ribs: cobalt
    (152, (160, 149, 141)), # rails: ferrite
]


def remap(px):
    r, g, b, a = px
    if a == 0:
        return px
    if r <= STOPS[0][0]:
        c = STOPS[0][1]
    elif r >= STOPS[-1][0]:
        c = STOPS[-1][1]
    else:
        for (k0, c0), (k1, c1) in zip(STOPS, STOPS[1:]):
            if k0 <= r <= k1:
                t = (r - k0) / (k1 - k0)
                c = tuple(round(c0[i] + (c1[i] - c0[i]) * t) for i in range(3))
                break
    return c + (a,)


def main(sheet_path, regions_path):
    Image.MAX_IMAGE_PIXELS = None
    sheet = Image.open(sheet_path).convert("RGBA")
    os.makedirs(OUT, exist_ok=True)
    for line in open(regions_path):
        name, _page, x, y, w, h, _rot = line.split()
        x, y, w, h = int(x), int(y), int(w), int(h)
        frame = sheet.crop((x, y, x + w, y + h))
        frame.putdata([remap(p) for p in frame.getdata()])
        frame.save(os.path.join(OUT, "ferrite-" + name + ".png"))
    print("conveyor frames written")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
