package supernova.ai;

import mindustry.Vars;
import mindustry.ai.types.GroundAI;
import mindustry.gen.Building;
import mindustry.world.Tile;

/**
 * Bug swarm behaviour: marches toward the core like normal ground units, but breaks off to
 * swarm any exposed player building within reach that it has a clear line to.
 * Runs only where AI runs (server / singleplayer).
 */
public class SwarmAI extends GroundAI {
    /** How far (world units) a bug will break off its path to attack a building. */
    public static float diveRange = 14f * 8f;

    protected Building prey;

    @Override
    public void updateMovement() {
        if (timer.get(timerTarget3, 30f)) prey = findPrey();

        if (prey != null && prey.isValid() && unit.within(prey, diveRange * 1.4f)) {
            moveTo(prey, unit.type.range * 0.75f);
            unit.lookAt(prey);
            faceTarget();
        } else {
            prey = null;
            super.updateMovement();
        }
    }

    protected Building findPrey() {
        Building best = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, diveRange, b -> true);
        if (best == null) return null;
        // Only dive if nothing solid is in the way (no pathfinding off the flowfield).
        boolean blocked = Vars.world.raycast(unit.tileX(), unit.tileY(), best.tileX(), best.tileY(), (x, y) -> {
            Tile t = Vars.world.tile(x, y);
            return t != null && t.solid() && t.build != best;
        });
        return blocked ? null : best;
    }
}
