package supernova.content;

import arc.graphics.Color;
import mindustry.type.Item;

/** Items from Aphelion and its asteroid belt. */
public final class SnItems {
    /** Tier 1 surface ore: Aphelion's basic building metal. */
    public static Item ferrite;
    /** Tier 1 surface ore: used for electronics and conductors. */
    public static Item cobaltine;

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
    }
}
