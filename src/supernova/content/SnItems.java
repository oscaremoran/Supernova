package supernova.content;

import arc.graphics.Color;
import mindustry.type.Item;

/**
 * Items from Aphelion and its asteroid belt. See docs/DESIGN.md section 4 for the tier table.
 * Content names are permanent once released: renaming one breaks saves and multiplayer.
 */
public final class SnItems {
    // Tier 1: surface ores
    public static Item ferrite, cobaltine, quartzite;
    // Tier 2
    public static Item ferrosteel, iridium, cryoIce;
    // Tier 3 (the "silicon" tier: first advanced electronics)
    public static Item conductium;

    private SnItems() {}

    public static void load() {
        ferrite = new Item("ferrite", Color.valueOf("8a7f78")) {{
            hardness = 1;
            cost = 0.6f;
        }};

        cobaltine = new Item("cobaltine", Color.valueOf("4f74c9")) {{
            hardness = 2;
            cost = 0.8f;
        }};

        quartzite = new Item("quartzite", Color.valueOf("e3c9cf")) {{
            hardness = 1;
            cost = 0.5f;
            lowPriority = true;
        }};

        ferrosteel = new Item("ferrosteel", Color.valueOf("6f7682")) {{
            cost = 1.2f;
        }};

        // Belt-only: delivered by Orbital Uplink missions (M2).
        iridium = new Item("iridium", Color.valueOf("d8dde6")) {{
            hardness = 4;
            cost = 1.5f;
        }};

        cryoIce = new Item("cryo-ice", Color.valueOf("a8e6f5")) {{
            hardness = 2;
            cost = 0.7f;
        }};

        conductium = new Item("conductium", Color.valueOf("e0b04a")) {{
            cost = 1.8f;
            charge = 0.15f;
        }};
    }
}
