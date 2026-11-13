package supernova;

import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.mod.Mod;
import supernova.content.*;
import supernova.world.SectorNames;
import supernova.world.gen.AphelionTutorial;

public class Supernova extends Mod {

    public Supernova() {
        Log.info("[Supernova] Constructed.");
        Events.on(ClientLoadEvent.class, e -> SectorNames.apply(SnPlanets.aphelion));
        // Sector info can be reloaded from disk; re-apply names (only fills in missing ones).
        Events.on(WorldLoadEvent.class, e -> {
            SectorNames.apply(SnPlanets.aphelion);
            // Older Aphelion saves may still have vanilla units in their waves; swap in the Aliens.
            if (!Vars.net.client() && Vars.state.rules.sector != null && Vars.state.rules.sector.planet == SnPlanets.aphelion
                && SnAliens.hasVanillaUnits(Vars.state.rules.spawns)) {
                SnAliens.convertWaves(Vars.state.rules.spawns);
            }
            // Saves of the start sector made before the tutorial existed get it added (host/singleplayer only).
            if (!Vars.net.client() && Vars.state.isCampaign() && Vars.state.rules.sector != null
                && Vars.state.rules.sector == SnPlanets.aphelion.getStartSector()
                && Vars.state.rules.objectives.all.isEmpty()) {
                AphelionTutorial.apply(Vars.state.rules);
            }
        });
    }

    @Override
    public void loadContent() {
        // Order matters: later content references earlier content.
        SnTeams.load();
        SnItems.load();
        SnLiquids.load();
        SnUnits.load();
        SnAliens.load();
        SnBlocks.load();
        SnPlanets.load();
        SnTechTree.load();
        Log.info("[Supernova] Content loaded.");
    }
}
