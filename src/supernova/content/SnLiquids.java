package supernova.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public final class SnLiquids {
    /** Melted from cryo-ice. Propellant for orbital missions, and a fuel and coolant. */
    public static Liquid volatiles;

    private SnLiquids() {}

    public static void load() {
        volatiles = new Liquid("volatiles", Color.valueOf("b8f0ff")) {{
            flammability = 0.6f;
            explosiveness = 0.3f;
            temperature = 0.2f;
            heatCapacity = 0.6f;
            coolant = true;
            barColor = Color.valueOf("8fdcf2");
        }};
    }
}
