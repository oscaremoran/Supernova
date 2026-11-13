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


XFORM = None  # (scale, centre_x, centre_y) while drawing scaled item icons


class ScaledDraw:
    """Wraps ImageDraw, scaling all coordinates about a centre point onto a 32px canvas."""

    def __init__(self, d, f, cx, cy):
        self.d, self.f, self.cx, self.cy = d, f, cx, cy

    def p(self, x, y):
        return (16 + (x - self.cx) * self.f, 16 + (y - self.cy) * self.f)

    def pts(self, pts):
        return [self.p(x, y) for x, y in pts]

    def box(self, b):
        x0, y0 = self.p(b[0], b[1])
        x1, y1 = self.p(b[2], b[3])
        return [x0, y0, x1, y1]

    def polygon(self, pts, **kw):
        self.d.polygon(self.pts(pts), **kw)

    def ellipse(self, b, **kw):
        self.d.ellipse(self.box(b), **kw)

    def rectangle(self, b, **kw):
        self.d.rectangle(self.box(b), **kw)

    def arc(self, b, start, end, width=1, **kw):
        self.d.arc(self.box(b), start, end, width=max(1, round(width * self.f)), **kw)

    def line(self, pts, width=1, **kw):
        self.d.line(self.pts(pts), width=max(1, round(width * self.f)), **kw)

    def point(self, pts, **kw):
        for x, y in self.pts(pts):
            self.d.rectangle([x, y, x + max(0, round(self.f) - 1), y + max(0, round(self.f) - 1)], **kw)


def new(size):
    im = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    if XFORM is not None:
        d = ScaledDraw(d, *XFORM)
    return im, d


ITEM_FILL = 30  # vanilla item icons fill about 30 of 32 pixels


def big_item(name, draw):
    """Draws an item icon, then redraws it scaled up so it fills the icon like vanilla items."""
    global XFORM
    XFORM = None
    draw()
    bbox = Image.open(os.path.join(ITEMS, name + ".png")).getbbox()
    if bbox:
        w, h = bbox[2] - bbox[0], bbox[3] - bbox[1]
        f = ITEM_FILL / max(w, h)
        XFORM = (f, (bbox[0] + bbox[2]) / 2, (bbox[1] + bbox[3]) / 2)
        draw()
    XFORM = None


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
    big_item("ferrite", lambda: item_chunks("ferrite", "8a7f78", [
        [(5, 14), (13, 6), (21, 9), (22, 19), (14, 24), (6, 21)],
        [(16, 18), (23, 13), (28, 17), (27, 26), (19, 28)],
    ]))
    big_item("cobaltine", lambda: item_chunks("cobaltine", "4f74c9", [
        [(7, 26), (10, 8), (15, 4), (17, 22), (12, 28)],
        [(15, 27), (19, 10), (24, 6), (26, 20), (21, 28)],
    ]))
    big_item("quartzite", lambda: item_chunks("quartzite", "e3c9cf", [
        [(4, 25), (8, 12), (12, 9), (13, 26)],
        [(11, 28), (14, 6), (18, 3), (21, 27)],
        [(19, 27), (23, 14), (27, 12), (28, 26)],
    ]))
    big_item("iridium", lambda: item_chunks("iridium", "d8dde6", [
        [(6, 18), (10, 10), (19, 7), (25, 12), (26, 21), (18, 27), (9, 25)],
    ], outline=hexc("4a4f5c")))
    big_item("ferrosteel", lambda: item_ingot("ferrosteel", "6f7682"))
    big_item("conductium", lambda: item_ingot("conductium", "e0b04a", stripe="4f74c9"))
    big_item("cryo-ice", lambda: item_cube("cryo-ice", "a8e6f5"))
    big_item("novium", lambda: item_gem("novium", "ff7a3d", "ffb36b"))
    big_item("singularity-composite", lambda: item_singularity("singularity-composite"))
    big_item("volatiles", lambda: liquid_icon("volatiles", "b8f0ff"))


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


UNITS = os.path.join(ROOT, "units")
HULL = hexc("8a8f9c")
HULL_LIGHT = hexc("b5bac6")
HULL_DARK = hexc("5a5f6c")


def poly_shaded(d, pts, base):
    """Fill with an outline and a lighter left half for a simple light-from-left look."""
    d.polygon(pts, fill=OUTLINE)
    cx = sum(p[0] for p in pts) / len(pts)
    inner = [(x + (1 if x < cx else -1), y) for x, y in pts]
    ys = [p[1] for p in pts]
    inner = [(x, y + (1 if y < (min(ys) + max(ys)) / 2 else -1)) for x, y in inner]
    d.polygon(inner, fill=base)
    left = [(x, y) if x <= cx else (cx, y) for x, y in inner]
    d.polygon(left, fill=shade(base, 1.18))


def unit_sprite(name, size, body, cell, details=None):
    im, d = new(size)
    for pts in body:
        poly_shaded(d, pts, HULL)
    if details:
        details(d)
    save(im, UNITS, name)
    cim, cd = new(size)
    for pts in cell:
        cd.polygon(pts, fill=(255, 255, 255, 255))
    save(cim, UNITS, name + "-cell")


def units():
    # Pioneer: compact delta-wing builder, 40px
    unit_sprite("pioneer", 40,
        [[(20, 3), (27, 16), (35, 30), (27, 33), (20, 28), (13, 33), (5, 30), (13, 16)]],
        [[(20, 12), (23, 20), (20, 24), (17, 20)]],
        lambda d: (d.rectangle([18, 28, 22, 33], fill=HULL_DARK), d.point([(20, 8)], fill=hexc("b8f0ff"))))
    # Prospector: small round drone with a drill claw, 32px
    def prospector_details(d):
        d.polygon([(14, 2), (18, 2), (17, 7), (15, 7)], fill=hexc("e0b04a"), outline=OUTLINE)
    unit_sprite("prospector", 32,
        [[(16, 6), (24, 10), (26, 18), (22, 26), (16, 28), (10, 26), (6, 18), (8, 10)],
         [(3, 14), (7, 12), (7, 22), (3, 20)], [(29, 14), (25, 12), (25, 22), (29, 20)]],
        [[(16, 13), (19, 16), (16, 21), (13, 16)]],
        prospector_details)
    # Surveyor: bigger mining drone with twin drill arms, 44px
    def surveyor_details(d):
        for x in (12, 28):
            d.polygon([(x, 2), (x + 4, 2), (x + 3, 9), (x + 1, 9)], fill=hexc("e0b04a"), outline=OUTLINE)
        d.ellipse([19, 17, 25, 23], fill=hexc("6cc9e0"), outline=OUTLINE)
    unit_sprite("surveyor", 44,
        [[(22, 6), (32, 10), (38, 22), (32, 36), (22, 40), (12, 36), (6, 22), (12, 10)],
         [(10, 8), (16, 8), (16, 14), (10, 14)], [(28, 8), (34, 8), (34, 14), (28, 14)]],
        [[(22, 26), (28, 30), (22, 35), (16, 30)]],
        surveyor_details)
    # Hauler: long cargo hauler with a container bay, 52px
    def hauler_details(d):
        d.rectangle([18, 18, 33, 36], fill=hexc("6f7682"), outline=OUTLINE)
        for y in (22, 27, 32):
            d.line([(19, y), (32, y)], fill=hexc("4a4f5c"))
        d.point([(25, 9)], fill=hexc("b8f0ff"))
    unit_sprite("hauler", 52,
        [[(26, 3), (34, 12), (36, 40), (44, 46), (34, 49), (26, 45), (18, 49), (8, 46), (16, 40), (18, 12)]],
        [[(22, 12), (30, 12), (31, 16), (21, 16)]],
        hauler_details)


def wall(name, tiles, base, light, dark):
    im, d = new(32 * tiles)
    s = 32 * tiles
    d.rectangle([0, 0, s - 1, s - 1], fill=OUTLINE)
    d.rectangle([1, 1, s - 2, s - 2], fill=hexc(base))
    d.line([(1, 1), (s - 2, 1)], fill=hexc(light), width=2)
    d.line([(1, 1), (1, s - 2)], fill=hexc(light), width=2)
    d.line([(2, s - 2), (s - 2, s - 2)], fill=hexc(dark), width=2)
    d.line([(s - 2, 2), (s - 2, s - 2)], fill=hexc(dark), width=2)
    # riveted panel seams
    step = 16
    for x in range(step, s, step):
        d.line([(x, 3), (x, s - 4)], fill=hexc(dark))
    for y in range(step, s, step):
        d.line([(3, y), (s - 4, y)], fill=hexc(dark))
    for x in range(6, s, step):
        for y in range(6, s, step):
            d.point([(x, y)], fill=hexc(light))
    save(im, BLOCKS, name)


def bolter():
    im, d = new(64)
    # turret head facing up: body + twin barrels
    d.rectangle([26, 4, 30, 30], fill=OUTLINE)
    d.rectangle([34, 4, 38, 30], fill=OUTLINE)
    d.rectangle([27, 5, 29, 29], fill=hexc("6f7682"))
    d.rectangle([35, 5, 37, 29], fill=hexc("6f7682"))
    poly_shaded(d, [(32, 18), (44, 24), (48, 40), (42, 52), (22, 52), (16, 40), (20, 24)], hexc("8a7f78"))
    d.rectangle([28, 34, 36, 44], fill=hexc("4f74c9"), outline=OUTLINE)
    save(im, BLOCKS, "bolter")


def solar_array():
    im, d, s = plate(2)
    for gx in range(2):
        for gy in range(2):
            x0, y0 = 6 + gx * 27, 6 + gy * 27
            d.rectangle([x0, y0, x0 + 24, y0 + 24], fill=OUTLINE)
            d.rectangle([x0 + 1, y0 + 1, x0 + 23, y0 + 23], fill=hexc("2b3d6b"))
            for k in range(1, 3):
                d.line([(x0 + 1, y0 + k * 8), (x0 + 23, y0 + k * 8)], fill=hexc("4f74c9"))
                d.line([(x0 + k * 8, y0 + 1), (x0 + k * 8, y0 + 23)], fill=hexc("4f74c9"))
            d.line([(x0 + 2, y0 + 2), (x0 + 8, y0 + 2)], fill=hexc("b8d0ff"))
    save(im, BLOCKS, "solar-array")


def burner():
    im, d, s = plate(2)
    d.ellipse([12, 12, 51, 51], fill=FRAME_DARK)
    d.ellipse([15, 15, 48, 48], fill=hexc("1d3a44"))
    d.ellipse([21, 21, 42, 42], fill=hexc("6cc9e0"))
    d.ellipse([26, 26, 37, 37], fill=hexc("e6fbff"))
    for x, y in ((8, 30), (52, 30), (30, 8), (30, 52)):
        d.rectangle([x - 3, y - 3, x + 3, y + 3], fill=hexc("e0b04a"), outline=OUTLINE)
    save(im, BLOCKS, "volatile-burner")


def drone_bay():
    im, d, s = plate(3)
    d.rectangle([12, 12, s - 13, s - 13], fill=FRAME_DARK)
    d.rectangle([14, 14, s - 15, s - 15], fill=hexc("2a272e"))
    for k in range(4):  # hazard stripes on the pad rim
        x = 18 + k * 16
        d.polygon([(x, 14), (x + 8, 14), (x + 4, 19), (x - 4, 19)], fill=hexc("e0b04a"))
        d.polygon([(x, s - 15), (x + 8, s - 15), (x + 4, s - 20), (x - 4, s - 20)], fill=hexc("e0b04a"))
    save(im, BLOCKS, "drone-bay")
    # output arrow (rotated by the game toward the output side)
    im, d = new(s)
    d.polygon([(s - 10, s // 2), (s - 20, s // 2 - 8), (s - 20, s // 2 + 8)], fill=hexc("e0b04a"), outline=OUTLINE)
    save(im, BLOCKS, "drone-bay-out")
    # top: corner clamps drawn over the unit being built
    im, d = new(s)
    for cx, cy in ((14, 14), (s - 15, 14), (14, s - 15), (s - 15, s - 15)):
        d.rectangle([cx - 5, cy - 5, cx + 5, cy + 5], fill=OUTLINE)
        d.rectangle([cx - 4, cy - 4, cx + 4, cy + 4], fill=FRAME_LIGHT)
    save(im, BLOCKS, "drone-bay-top")


def core_outpost():
    im, d, s = plate(3)
    c = s // 2
    d.rectangle([14, 14, s - 15, s - 15], fill=FRAME_DARK)
    d.polygon([(c, 18), (s - 18, c), (c, s - 18), (18, c)], fill=hexc("6f7682"), outline=OUTLINE)
    d.polygon([(c, 26), (s - 26, c), (c, s - 26), (26, c)], fill=hexc("4f74c9"))
    d.ellipse([c - 7, c - 7, c + 7, c + 7], fill=hexc("b8f0ff"), outline=OUTLINE)
    save(im, BLOCKS, "core-outpost")
    # team-colored trim (tinted by the game)
    im, d = new(s)
    for cx, cy in ((8, 8), (s - 9, 8), (8, s - 9), (s - 9, s - 9)):
        d.rectangle([cx - 3, cy - 3, cx + 3, cy + 3], fill=(255, 255, 255, 255))
    save(im, BLOCKS, "core-outpost-team")


DIST = os.path.join(BLOCKS, "distribution")
BELT = hexc("2c282e")
COBALT = hexc("5670b2")
COBALT_LIGHT = hexc("8fb0f0")


def small_frame():
    """32px ferrite housing used by the junction, router and bridge."""
    im, d = new(32)
    d.rectangle([0, 0, 31, 31], fill=OUTLINE)
    d.rectangle([1, 1, 30, 30], fill=FRAME)
    d.line([(1, 1), (30, 1)], fill=FRAME_LIGHT)
    d.line([(1, 1), (1, 30)], fill=FRAME_LIGHT)
    d.line([(1, 30), (30, 30)], fill=FRAME_DARK)
    d.line([(30, 1), (30, 30)], fill=FRAME_DARK)
    return im, d


def junction():
    im, d = small_frame()
    # two crossing belt channels
    d.rectangle([10, 1, 21, 30], fill=BELT)
    d.rectangle([1, 10, 30, 21], fill=BELT)
    # cobalt chevrons pointing through on each axis
    for (x, y, dx, dy) in ((15.5, 4, 0, 1), (15.5, 27, 0, -1), (4, 15.5, 1, 0), (27, 15.5, -1, 0)):
        if dx:
            d.polygon([(x, y - 3), (x + 3 * dx, y), (x, y + 3)], fill=COBALT)
        else:
            d.polygon([(x - 3, y), (x, y + 3 * dy), (x + 3, y)], fill=COBALT)
    # corner plates
    for cx, cy in ((5, 5), (26, 5), (5, 26), (26, 26)):
        d.rectangle([cx - 2, cy - 2, cx + 2, cy + 2], fill=FRAME_LIGHT, outline=FRAME_DARK)
    d.rectangle([13, 13, 18, 18], fill=COBALT_LIGHT, outline=OUTLINE)
    save(im, DIST, "ferrite-junction")


def router():
    im, d = small_frame()
    d.rectangle([5, 5, 26, 26], fill=BELT)
    # openings on all four sides
    for b in ([12, 1, 19, 5], [12, 26, 19, 30], [1, 12, 5, 19], [26, 12, 30, 19]):
        d.rectangle(b, fill=BELT)
    d.ellipse([9, 9, 22, 22], fill=OUTLINE)
    d.ellipse([10, 10, 21, 21], fill=COBALT)
    d.ellipse([13, 13, 18, 18], fill=COBALT_LIGHT)
    for a in range(4):
        ang = a * math.pi / 2 + math.pi / 4
        x, y = 15.5 + math.cos(ang) * 9, 15.5 + math.sin(ang) * 9
        d.point([(x, y)], fill=FRAME_LIGHT)
    save(im, DIST, "ferrite-router")


def bridge_base():
    im, d = small_frame()
    d.ellipse([6, 6, 25, 25], fill=OUTLINE)
    d.ellipse([7, 7, 24, 24], fill=BELT)
    d.ellipse([11, 11, 20, 20], fill=COBALT)
    d.ellipse([13, 13, 18, 18], fill=COBALT_LIGHT)
    for cx, cy in ((4, 4), (27, 4), (4, 27), (27, 27)):
        d.point([(cx, cy)], fill=FRAME_DARK)
    save(im, DIST, "ferrite-bridge")


def power_node():
    im, d = new(32)
    d.rectangle([3, 3, 28, 28], fill=OUTLINE)
    d.rectangle([4, 4, 27, 27], fill=FRAME)
    d.line([(4, 4), (27, 4)], fill=FRAME_LIGHT)
    d.line([(4, 4), (4, 27)], fill=FRAME_LIGHT)
    # cobaltine coil
    d.ellipse([8, 8, 23, 23], fill=OUTLINE)
    d.ellipse([9, 9, 22, 22], fill=COBALT)
    d.ellipse([11, 11, 20, 20], fill=hexc("2b2f3a"))
    d.ellipse([13, 13, 18, 18], fill=hexc("e0b04a"))
    d.point([(14, 14)], fill=(255, 255, 255, 255))
    save(im, BLOCKS, "ferrite-power-node")


def blocks():
    drill()
    smelter()
    melter()
    fabricator()
    forge()
    uplink()
    junction()
    router()
    bridge_base()
    power_node()
    wall("ferrite-wall", 1, "8a7f78", "b0a69e", "5e5550")
    wall("ferrite-wall-large", 2, "8a7f78", "b0a69e", "5e5550")
    wall("ferrosteel-wall", 1, "6f7682", "9aa2b0", "474c56")
    wall("ferrosteel-wall-large", 2, "6f7682", "9aa2b0", "474c56")
    bolter()
    solar_array()
    burner()
    drone_bay()
    core_outpost()


if __name__ == "__main__":
    items()
    ores()
    blocks()
    units()
    print("sprites generated")
