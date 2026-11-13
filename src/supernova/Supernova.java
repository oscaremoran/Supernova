package supernova;

import arc.util.Log;
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
        Log.info("[Supernova] Content loaded.");
    }
}
