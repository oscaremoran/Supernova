"""Supernova sprite generator.

Regenerates every procedural sprite in assets/sprites. Run from the repo root:
    python3 tools/sprites.py

Sprites drawn by hand later should be removed from this script so they aren't overwritten.
"""
import math
import os
import random
from PIL import Image, ImageDraw

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "assets", "sprites")
ITEMS = os.path.join(ROOT, "items")
ENV = os.path.join(ROOT, "blocks", "environment")
BLOCKS = os.path.join(ROOT, "blocks")

OUTLINE = (40, 38, 44, 255)


def hexc(h, a=255):
    h = h.lstrip("#")
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (a,)


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + (c[3],)


def new(size):
    im = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    return im, ImageDraw.Draw(im)


def save(im, folder, name):
    os.makedirs(folder, exist_ok=True)
    im.save(os.path.join(folder, name + ".png"))


# ---------------------------------------------------------------- items

def faceted(d, poly, base, outline):
    """Polygon with an outline, a lit top-left facet and a shaded bottom-right facet."""
    d.polygon(poly, fill=outline)
    cx = sum(p[0] for p in poly) / len(poly)
    cy = sum(p[1] for p in poly) / len(poly)
    inner = [(x + (1 if x < cx else -1), y + (1 if y < cy else -1)) for x, y in poly]
    d.polygon(inner, fill=base)
    d.polygon([inner[0], inner[1], (cx, cy)], fill=shade(base, 1.28))
    d.polygon([inner[-2], inner[-1], (cx, cy)], fill=shade(base, 0.7))


def item_chunks(name, base, polys, outline=None):
    im, d = new(32)
    b = hexc(base)
    for p in polys:
        faceted(d, p, b, outline or shade(b, 0.38))
    save(im, ITEMS, name)


def item_ingot(name, base, stripe=None):
    """A trapezoid ingot/plate, used for refined alloys."""
    im, d = new(32)
    b = hexc(base)
    o = shade(b, 0.35)
    top = [(9, 9), (23, 9), (27, 16), (5, 16)]
    front = [(5, 16), (27, 16), (27, 23), (5, 23)]
    d.polygon([(8, 8), (24, 8), (28, 16), (28, 24), (4, 24), (4, 16)], fill=o)
    d.polygon(top, fill=shade(b, 1.3))
    d.polygon(front, fill=b)
    d.line([(6, 22), (26, 22)], fill=shade(b, 0.75))
    if stripe:
        s = hexc(stripe)
        d.rectangle([7, 18, 25, 19], fill=s)
        d.line([(11, 12), (21, 12)], fill=shade(s, 1.2))
    save(im, ITEMS, name)


def item_cube(name, base):
    """An isometric ice block."""
    im, d = new(32)
    b = hexc(base)
    o = shade(b, 0.45)
    d.polygon([(16, 3), (28, 9), (28, 23), (16, 29), (4, 23), (4, 9)], fill=o)
    d.polygon([(16, 5), (26, 10), (16, 15), (6, 10)], fill=shade(b, 1.15))
    d.polygon([(6, 11), (15, 16), (15, 27), (6, 22)], fill=b)
    d.polygon([(17, 16), (26, 11), (26, 22), (17, 27)], fill=shade(b, 0.8))
    d.line([(8, 13), (8, 18)], fill=(255, 255, 255, 200))
    d.point([(12, 9), (20, 9)], fill=(255, 255, 255, 230))
    save(im, ITEMS, name)


def item_gem(name, base, glow):
    """A glowing hexagonal gem with a soft halo."""
    im, d = new(32)
    g = hexc(glow, 70)
    d.ellipse([3, 3, 29, 29], fill=g)
    b = hexc(base)
    pts = [(16, 4), (25, 10), (25, 22), (16, 28), (7, 22), (7, 10)]
    d.polygon(pts, fill=shade(b, 0.35))
    inner = [(16, 6), (23, 11), (23, 21), (16, 26), (9, 21), (9, 11)]
    d.polygon(inner, fill=b)
    d.polygon([(16, 6), (23, 11), (16, 16), (9, 11)], fill=shade(b, 1.35))
    d.polygon([(16, 16), (23, 21), (16, 26)], fill=shade(b, 0.7))
    d.polygon([(13, 13), (16, 11), (19, 13), (16, 19)], fill=(255, 240, 210, 255))
    save(im, ITEMS, name)


def item_singularity(name):
    im, d = new(32)
    d.ellipse([4, 4, 28, 28], fill=hexc("7d5cff", 60))
    d.ellipse([8, 8, 24, 24], fill=hexc("1a1230"))
    d.ellipse([10, 10, 22, 22], fill=hexc("3b2d5c"))
    d.ellipse([13, 13, 19, 19], fill=hexc("0a0614"))
    # accretion ring
    d.arc([2, 11, 30, 21], 200, 340, fill=hexc("c9b6ff"), width=2)
    d.arc([2, 11, 30, 21], 20, 160, fill=hexc("9a7cf0"), width=2)
    d.point([(12, 11)], fill=(255, 255, 255, 255))
    save(im, ITEMS, name)


def liquid_icon(name, base):
    """A droplet icon for a liquid."""
    im, d = new(32)
    b = hexc(base)
    o = shade(b, 0.45)
    d.polygon([(16, 3), (24, 15), (8, 15)], fill=o)
    d.ellipse([7, 11, 25, 29], fill=o)
    d.polygon([(16, 5), (23, 16), (9, 16)], fill=b)
    d.ellipse([9, 13, 23, 27], fill=b)
    d.ellipse([11, 15, 16, 21], fill=shade(b, 1.25))
    d.point([(13, 17)], fill=(255, 255, 255, 255))
    save(im, ITEMS, name)


def items():
    item_chunks("ferrite", "8a7f78", [
        [(5, 14), (13, 6), (21, 9), (22, 19), (14, 24), (6, 21)],
        [(16, 18), (23, 13), (28, 17), (27, 26), (19, 28)],
    ])
    item_chunks("cobaltine", "4f74c9", [
        [(7, 26), (10, 8), (15, 4), (17, 22), (12, 28)],
        [(15, 27), (19, 10), (24, 6), (26, 20), (21, 28)],
    ])
    item_chunks("quartzite", "e3c9cf", [
        [(4, 25), (8, 12), (12, 9), (13, 26)],
        [(11, 28), (14, 6), (18, 3), (21, 27)],
        [(19, 27), (23, 14), (27, 12), (28, 26)],
    ])
    item_chunks("iridium", "d8dde6", [
        [(6, 18), (10, 10), (19, 7), (25, 12), (26, 21), (18, 27), (9, 25)],
    ], outline=hexc("4a4f5c"))
    item_ingot("ferrosteel", "6f7682")
    item_ingot("conductium", "e0b04a", stripe="4f74c9")
    item_cube("cryo-ice", "a8e6f5")
    item_gem("novium", "ff7a3d", "ffb36b")
    item_singularity("singularity-composite")
    liquid_icon("volatiles", "b8f0ff")


# ---------------------------------------------------------------- ores

def ore(name, base, seed):
    b = hexc(base)
    for v in range(1, 4):
        rng = random.Random(seed * 10 + v)
        im, d = new(32)
        for _ in range(rng.randint(3, 5)):
            r = rng.randint(3, 5)
            x = rng.randint(4 + r, 28 - r)
            y = rng.randint(4 + r, 28 - r)
            d.ellipse([x - r - 1, y - r - 1, x + r + 1, y + r + 1], fill=shade(b, 0.4))
            d.ellipse([x - r, y - r, x + r, y + r], fill=b)
            d.ellipse([x - r + 1, y - r + 1, x, y], fill=shade(b, 1.3))
            d.point([(x + r // 2, y + r // 2)], fill=shade(b, 0.7))
        save(im, ENV, f"{name}{v}")


def ores():
    ore("ore-ferrite", "8a7f78", 1)
    ore("ore-cobaltine", "4f74c9", 2)
    ore("ore-quartzite", "e3c9cf", 3)


# ---------------------------------------------------------------- blocks

FRAME = hexc("5b5560")       # ferrite plating
FRAME_DARK = hexc("3d3842")
FRAME_LIGHT = hexc("7c7581")


def plate(size_tiles):
    """Base building plate: outline, bevelled frame and inner panel."""
    s = size_tiles * 32
    im, d = new(s)
    d.rectangle([0, 0, s - 1, s - 1], fill=OUTLINE)
    d.rectangle([1, 1, s - 2, s - 2], fill=FRAME)
    d.line([(1, 1), (s - 2, 1)], fill=FRAME_LIGHT, width=2)
    d.line([(1, 1), (1, s - 2)], fill=FRAME_LIGHT, width=2)
    d.line([(1, s - 2), (s - 2, s - 2)], fill=FRAME_DARK, width=2)
    d.line([(s - 2, 1), (s - 2, s - 2)], fill=FRAME_DARK, width=2)
    # corner bolts
    for cx, cy in ((5, 5), (s - 6, 5), (5, s - 6), (s - 6, s - 6)):
        d.rectangle([cx - 1, cy - 1, cx + 1, cy + 1], fill=FRAME_DARK)
        d.point([(cx - 1, cy - 1)], fill=FRAME_LIGHT)
    return im, d, s


def drill():
    im, d, s = plate(2)
    c = s // 2
    d.ellipse([c - 22, c - 22, c + 22, c + 22], fill=FRAME_DARK)
    d.ellipse([c - 20, c - 20, c + 20, c + 20], fill=hexc("2a272e"))
    save(im, BLOCKS, "ferrite-drill")

    # rotator: 3-blade drill head (spun by the game)
    im, d = new(s)
    for k in range(3):
        a = k * 2 * math.pi / 3
        pts = []
        for da, r in ((-0.6, 5), (-0.3, 19), (0.05, 20), (0.6, 5)):
            pts.append((c + math.cos(a + da) * r, c + math.sin(a + da) * r))
        d.polygon(pts, fill=OUTLINE)
        inner = [(c + (x - c) * 0.88, c + (y - c) * 0.88) for x, y in pts]
        d.polygon(inner, fill=hexc("9a8f87"))
    d.ellipse([c - 6, c - 6, c + 6, c + 6], fill=hexc("b0a69e"), outline=OUTLINE)
    save(im, BLOCKS, "ferrite-drill-rotator")

    # top: cap over the hub
    im, d = new(s)
    d.ellipse([c - 4, c - 4, c + 4, c + 4], fill=FRAME_LIGHT, outline=OUTLINE)
    d.point([(c - 1, c - 1)], fill=(255, 255, 255, 200))
    save(im, BLOCKS, "ferrite-drill-top")


def smelter():
    im, d, s = plate(2)
    # furnace mouth with ember glow
    d.rectangle([14, 14, 49, 49], fill=FRAME_DARK)
    d.rectangle([16, 16, 47, 47], fill=hexc("2a1a14"))
    d.ellipse([20, 20, 43, 43], fill=hexc("b0451c"))
    d.ellipse([25, 25, 38, 38], fill=hexc("ff8a3d"))
    d.ellipse([29, 29, 34, 34], fill=hexc("ffd08a"))
    # vent slits
    for y in (8, 54):
        for x in range(18, 46, 6):
            d.rectangle([x, y, x + 3, y + 1], fill=FRAME_DARK)
    save(im, BLOCKS, "ferrosteel-smelter")


def melter():
    im, d, s = plate(2)
    # glass tank of volatiles over an ice hopper
    d.rectangle([12, 10, 51, 53], fill=FRAME_DARK)
    d.rectangle([14, 12, 49, 51], fill=hexc("1d3a44"))
    d.rectangle([14, 30, 49, 51], fill=hexc("6cc9e0"))
    d.rectangle([14, 30, 49, 32], fill=hexc("b8f0ff"))
    for bx, by in ((20, 40), (34, 36), (42, 45), (27, 47)):
        d.ellipse([bx - 2, by - 2, bx + 2, by + 2], outline=hexc("e6fbff"))
    d.line([(16, 14), (16, 28)], fill=(255, 255, 255, 120))
    d.rectangle([24, 16, 39, 25], fill=hexc("a8e6f5"), outline=hexc("4a7f8c"))
    save(im, BLOCKS, "ice-melter")


def fabricator():
    im, d, s = plate(2)
    d.rectangle([10, 10, 53, 53], fill=FRAME_DARK)
    d.rectangle([12, 12, 51, 51], fill=hexc("2b2f3a"))
    # circuit traces
    gold = hexc("e0b04a")
    for y in (18, 26, 38, 46):
        d.line([(14, y), (26, y), (30, y + 4)], fill=gold)
    for x in (36, 44):
        d.line([(x, 14), (x, 49)], fill=hexc("4f74c9"))
    d.rectangle([24, 24, 39, 39], fill=gold, outline=OUTLINE)
    d.rectangle([28, 28, 35, 35], fill=hexc("fff0b8"))
    save(im, BLOCKS, "conductium-fabricator")


def forge():
    im, d, s = plate(3)
    c = s // 2
    d.ellipse([c - 36, c - 36, c + 36, c + 36], fill=FRAME_DARK)
    d.ellipse([c - 33, c - 33, c + 33, c + 33], fill=hexc("140e22"))
    d.ellipse([c - 26, c - 26, c + 26, c + 26], fill=hexc("7d5cff", 90))
    d.ellipse([c - 16, c - 16, c + 16, c + 16], fill=hexc("3b2d5c"))
    d.ellipse([c - 8, c - 8, c + 8, c + 8], fill=hexc("0a0614"))
    d.arc([c - 30, c - 10, c + 30, c + 10], 0, 360, fill=hexc("c9b6ff"), width=2)
    # containment pylons
    for px, py in ((c, 10), (c, s - 11), (10, c), (s - 11, c)):
        d.rectangle([px - 4, py - 4, px + 4, py + 4], fill=hexc("ff7a3d"), outline=OUTLINE)
    save(im, BLOCKS, "singularity-forge")


def uplink():
    im, d, s = plate(3)
    c = s // 2
    # launch pad ring
    d.ellipse([c - 38, c - 38, c + 38, c + 38], fill=FRAME_DARK)
    d.ellipse([c - 35, c - 35, c + 35, c + 35], fill=hexc("34303a"))
    # hazard chevrons around the pad
    for k in range(8):
        a = k * math.pi / 4 + math.pi / 8
        x, y = c + math.cos(a) * 30, c + math.sin(a) * 30
        d.ellipse([x - 3, y - 3, x + 3, y + 3], fill=hexc("e0b04a") if k % 2 == 0 else hexc("2a272e"))
    # landing cradle
    d.ellipse([c - 20, c - 20, c + 20, c + 20], fill=hexc("2a272e"))
    d.ellipse([c - 18, c - 18, c + 18, c + 18], outline=hexc("6cc9e0"), width=2)
    for a in (0, math.pi / 2, math.pi, 3 * math.pi / 2):
        x1, y1 = c + math.cos(a) * 18, c + math.sin(a) * 18
        x2, y2 = c + math.cos(a) * 26, c + math.sin(a) * 26
        d.line([(x1, y1), (x2, y2)], fill=FRAME_LIGHT, width=3)
    # side propellant tanks
    for tx in (8, s - 20):
        d.rectangle([tx, 8, tx + 11, 24], fill=OUTLINE)
        d.rectangle([tx + 1, 9, tx + 10, 23], fill=hexc("6cc9e0"))
        d.line([(tx + 2, 10), (tx + 2, 21)], fill=hexc("e6fbff"))
    save(im, BLOCKS, "orbital-uplink")

    # mining drone pod, drawn rising and landing over the pad
    im, d = new(40)
    o = OUTLINE
    d.polygon([(20, 2), (29, 14), (29, 30), (11, 30), (11, 14)], fill=o)
    d.polygon([(20, 4), (27, 15), (27, 29), (13, 29), (13, 15)], fill=hexc("b0a69e"))
    d.polygon([(20, 4), (27, 15), (20, 15)], fill=hexc("d4ccc6"))
    d.rectangle([13, 22, 27, 24], fill=hexc("e0b04a"))
    d.ellipse([16, 9, 24, 17], fill=hexc("2b2f3a"), outline=o)
    d.point([(18, 11)], fill=hexc("b8f0ff"))
    # legs
    d.polygon([(11, 24), (5, 36), (8, 37), (14, 28)], fill=o)
    d.polygon([(29, 24), (35, 36), (32, 37), (26, 28)], fill=o)
    d.rectangle([16, 30, 24, 34], fill=o)
    d.rectangle([17, 31, 23, 33], fill=hexc("ff7a3d"))
    save(im, BLOCKS, "orbital-uplink-pod")


def blocks():
    drill()
    smelter()
    melter()
    fabricator()
    forge()
    uplink()


if __name__ == "__main__":
    items()
    ores()
    blocks()
    print("sprites generated")
