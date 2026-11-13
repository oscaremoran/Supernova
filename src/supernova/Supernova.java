package supernova;

import arc.util.Log;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import supernova.content.SnItems;
import supernova.content.SnTeams;

public class Supernova extends Mod {

    public Supernova() {
        Log.info("[Supernova] Constructed.");
    }

    @Override
    public void loadContent() {
        // Teams are registered first so later content can reference them.
        SnTeams.load();
        SnItems.load();

        hideVanillaPlanets();
        Log.info("[Supernova] Content loaded.");
    }

    /** Tantros is an unfinished vanilla planet; hide it so it doesn't clutter the planet screen. */
    private static void hideVanillaPlanets() {
        Planets.tantros.visible = false;
        Planets.tantros.accessible = false;
        Planets.tantros.drawOrbit = false;
    }
}
