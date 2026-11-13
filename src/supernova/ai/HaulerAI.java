package supernova.ai;

import mindustry.Vars;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.type.Item;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import supernova.world.OrbitalUplink.OrbitalUplinkBuild;

/**
 * Shuttles cargo from Orbital Uplinks to the nearest core.
 *
 * <p>AI only runs on the server (or in singleplayer). Item movement goes through
 * {@link Call#takeItems} and {@link Call#transferItemTo}, which are synced to clients.
 */
public class HaulerAI extends AIController {
    /** How far a hauler looks for uplinks with cargo, in world units. */
    public static float searchRange = 60f * 8f;

    protected Building pickup;

    @Override
    public void updateMovement() {
        if (unit.hasItem()) {
            deliver();
        } else {
            collect();
        }
        faceMovement();
    }

    protected void deliver() {
        CoreBuild core = unit.closestCore();
        if (core == null) return;

        moveTo(core, Vars.itemTransferRange * 0.6f);
        if (unit.within(core, Vars.itemTransferRange)) {
            if (core.acceptStack(unit.item(), unit.stack.amount, unit) > 0) {
                Call.transferItemTo(unit, unit.item(), unit.stack.amount, unit.x, unit.y, core);
            }
            unit.clearItem();
        }
    }

    protected void collect() {
        if (pickup == null || !pickup.isValid() || pickup.items.total() == 0 || timer.get(timerTarget2, 120f)) {
            pickup = Vars.indexer.findTile(unit.team, unit.x, unit.y, searchRange,
                b -> b instanceof OrbitalUplinkBuild && b.items.total() > 0);
        }

        if (pickup == null) {
            // Nothing to haul: wait near the core.
            CoreBuild core = unit.closestCore();
            if (core != null) moveTo(core, 6f * 8f);
            return;
        }

        moveTo(pickup, Vars.itemTransferRange * 0.6f);
        if (unit.within(pickup, Vars.itemTransferRange)) {
            Item item = pickup.items.first();
            if (item != null) {
                int amount = Math.min(pickup.items.get(item), unit.type.itemCapacity);
                if (amount > 0) Call.takeItems(pickup, item, amount, unit);
            }
        }
    }
}
