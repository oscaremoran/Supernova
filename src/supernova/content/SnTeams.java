package supernova.content;

import arc.graphics.Color;
import mindustry.game.Team;

/** Custom teams. Team IDs are synced over the network, so never change one once released. */
public final class SnTeams {
    /** Team slot used for the Aliens. Vanilla uses 0-6; we take an uncommon free slot. */
    public static final int ALIENS_ID = 77;

    /** The Aliens: Aphelion's hostile faction. Black team color with a dim purple rim. */
    public static Team aliens;

    private SnTeams() {}

    public static void load() {
        aliens = Team.get(ALIENS_ID);
        aliens.name = "aliens";
        aliens.color.set(Color.valueOf("141414"));
        // Palette: main, light edge, dark shade. Pure black would disappear against
        // dark terrain, so a faint purple tint on the lighter shades keeps outlines readable.
        aliens.setPalette(Color.valueOf("141414"), Color.valueOf("3a3344"), Color.valueOf("000000"));
    }
}
