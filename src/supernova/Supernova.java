package supernova;

import arc.Events;
import arc.util.Log;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import supernova.content.*;
import supernova.world.VanillaSkins;

public class Supernova extends Mod {

    public Supernova() {
        Log.info("[Supernova] Constructed.");
        // Placeholder blocks borrow vanilla sprites once all textures are loaded.
        Events.on(ClientLoadEvent.class, e -> VanillaSkins.apply());
    }

    @Override
    public void loadContent() {
        // Order matters: later content references earlier content.
        SnTeams.load();
        SnItems.load();
        SnLiquids.load();
        SnUnits.load();
        SnBlocks.load();
        SnPlanets.load();
        SnTechTree.load();
        Log.info("[Supernova] Content loaded.");
    }
}
