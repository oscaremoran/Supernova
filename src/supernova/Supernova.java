package supernova;

import arc.util.Log;
import mindustry.content.Planets;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import supernova.content.SnBlocks;
import supernova.content.SnItems;
import supernova.content.SnLiquids;
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
        SnLiquids.load();
        SnBlocks.load();

        hideVanillaPlanets();
        Log.info("[Supernova] Content loaded.");
    }

    /**
     * Removes the unfinished vanilla planet Tantros from the game as far as is safely possible.
     * It stays in the content registry: removing it would shift content IDs and break
     * saves and multiplayer sync.
     */
    private static void hideVanillaPlanets() {
        Planet tantros = Planets.tantros;
        if (tantros.parent != null) tantros.parent.children.remove(tantros); // gone from the star system
        tantros.visible = false;        // planet screen
        tantros.accessible = false;     // launch and landing
        tantros.drawOrbit = false;
        tantros.alwaysUnlocked = false;
        tantros.hideDatabase = true;    // core database
        tantros.hideDetails = true;
    }
}
