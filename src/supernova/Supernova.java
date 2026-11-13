package supernova;

import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.type.Item;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.mod.Mod;
import supernova.content.*;
import supernova.world.EditorVisibility;
import supernova.world.SectorNames;
import supernova.world.gen.AphelionTutorial;

public class Supernova extends Mod {

    public Supernova() {
        Log.info("[Supernova] Constructed.");
        Events.on(ClientLoadEvent.class, e -> {
            SectorNames.apply(SnPlanets.aphelion);
            EditorVisibility.init();
        });
        // Sector info can be reloaded from disk; re-apply names (only fills in missing ones).
        Events.on(WorldLoadEvent.class, e -> {
            SectorNames.apply(SnPlanets.aphelion);
            // Remove non-Supernova items (e.g. the vanilla 100-copper loadout in older saves).
            if (!Vars.net.client() && Vars.state.rules.sector != null && Vars.state.rules.sector.planet == SnPlanets.aphelion) {
                for (CoreBuild core : Vars.state.rules.defaultTeam.cores()) {
                    for (Item item : Vars.content.items()) {
                        if (!item.name.startsWith("supernova-") && core.items.has(item)) core.items.set(item, 0);
                    }
                }
            }
            // Older Aphelion saves may have vanilla (or since-removed) units in their waves; swap in current Aliens.
            if (!Vars.net.client() && Vars.state.rules.sector != null && Vars.state.rules.sector.planet == SnPlanets.aphelion
                && SnAliens.needsConversion(Vars.state.rules.spawns)) {
                SnAliens.convertWaves(Vars.state.rules.spawns);
            }
            // Landing Site tutorial: added on first load, and replaced in saves with an older version (host/singleplayer only).
            if (!Vars.net.client() && Vars.state.isCampaign() && Vars.state.rules.sector != null
                && Vars.state.rules.sector == SnPlanets.aphelion.getStartSector()
                && AphelionTutorial.outdated(Vars.state.rules)) {
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
        SnAlienBlocks.load();
        SnPlanets.load();
        SnSectors.load();
        SnTechTree.load();
        Log.info("[Supernova] Content loaded.");
    }
}
