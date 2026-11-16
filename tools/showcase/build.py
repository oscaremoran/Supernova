"""Rebuilds docs/showcase/index.html with the current sprites embedded.

Run from the repo root:  python3 tools/showcase/build.py
"""
import base64
import json

R = "assets/sprites/"
FILES = {
    "hatchling": "units/hatchling.png", "malign": "units/malign.png", "leacher": "units/leacher.png", "ripper": "units/ripper.png",
    "sentry": "units/sentry.png", "warden": "units/warden.png", "bastion": "units/bastion.png", "pioneer": "units/pioneer.png",
    "hive": "blocks/hive.png", "acidPod": "blocks/spore-pod.png", "acidSpire": "blocks/acid-spire.png", "chitin": "blocks/chitin-wall-large.png",
    "coreBrood": "blocks/core-brood.png", "bolter": "blocks/bolter.png", "igniter": "blocks/igniter.png", "arcCoil": "blocks/arc-coil.png",
    "drill": "blocks/ferrite-drill.png", "drillRot": "blocks/ferrite-drill-rotator.png", "drillTop": "blocks/ferrite-drill-top.png",
    "coreOutpost": "blocks/core-outpost.png", "foundry": "blocks/foundry.png", "wall": "blocks/ferrite-wall.png",
}


def uri(path):
    return "data:image/png;base64," + base64.b64encode(open(path, "rb").read()).decode()


sprites = {k: uri(R + v) for k, v in FILES.items()}
sprites["map"] = uri("docs/maps/landing-site.png")
template = open("tools/showcase/template.html").read()
open("docs/showcase/index.html", "w").write(template.replace("__SPRITES__", json.dumps(sprites)))
print("wrote docs/showcase/index.html")
