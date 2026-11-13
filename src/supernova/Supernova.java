package supernova;

import arc.Events;
import arc.util.Log;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.mod.Mod;
import supernova.content.*;
import supernova.world.SectorNames;
import supernova.world.VanillaSkins;

public class Supernova extends Mod {

    public Supernova() {
        Log.info("[Supernova] Constructed.");
        // Placeholder blocks borrow vanilla sprites once all textures are loaded.
        Events.on(ClientLoadEvent.class, e -> {
            VanillaSkins.apply();
            SectorNames.apply(SnPlanets.aphelion);
        });
        // Sector info can be reloaded from disk; re-apply names (only fills in missing ones).
        Events.on(WorldLoadEvent.class, e -> SectorNames.apply(SnPlanets.aphelion));
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
