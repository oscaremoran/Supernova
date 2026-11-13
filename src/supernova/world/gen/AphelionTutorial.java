package supernova.world.gen;

import arc.Core;
import mindustry.game.MapObjectives;
import mindustry.game.MapObjectives.*;
import mindustry.game.Rules;

import static supernova.content.SnBlocks.*;
import static supernova.content.SnItems.*;
import static supernova.content.SnUnits.*;

/**
 * Tutorial for Aphelion's first sector (Landing Site), built on the game's map objectives.
 * Each step unlocks after the previous one and shows in the objectives panel; tap a step for details.
 */
public final class AphelionTutorial {
    private AphelionTutorial() {}

    public static void apply(Rules rules) {
        MapObjectives o = rules.objectives;

        MapObjective drills = step(new BuildCountObjective(ferriteDrill, 3), "drills");
        MapObjective conveyors = step(new CoreItemObjective(ferrite, 600), "conveyors").parent(drills);
        MapObjective research = step(new ResearchObjective(bolter), "research").parent(conveyors);
        MapObjective defend = step(new BuildCountObjective(bolter, 2), "defend").parent(research);
        MapObjective power = step(new BuildCountObjective(solarArray, 2), "power").parent(defend);
        MapObjective smelt = step(new CoreItemObjective(ferrosteel, 60), "smelt").parent(power);
        MapObjective drones = step(new UnitCountObjective(prospector, 2), "drones").parent(smelt);
        MapObjective uplink = step(new CoreItemObjective(iridium, 30), "uplink").parent(drones);

        o.add(drills, conveyors, research, defend, power, smelt, drones, uplink);
    }

    private static MapObjective step(MapObjective objective, String key) {
        return objective.details(Core.bundle == null ? "" : Core.bundle.get("tutorial.supernova." + key, ""));
    }
}
