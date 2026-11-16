package supernova.ai;

import mindustry.ai.types.GroundAI;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

/**
 * Predator behaviour: normal ground movement, but when choosing what to shoot, prefers the most
 * wounded enemy unit in range (the Leacher heals off its kills).
 */
public class PredatorAI extends GroundAI {
    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        Unit[] best = {null};
        float[] bestFrac = {Float.MAX_VALUE};
        Units.nearbyEnemies(unit.team, x, y, range, u -> {
            if (u.dead() || !(u.isFlying() ? air : ground)) return;
            float frac = u.health / u.maxHealth;
            if (frac < bestFrac[0]) {
                bestFrac[0] = frac;
                best[0] = u;
            }
        });
        if (best[0] != null && bestFrac[0] < 0.999f) return best[0];
        return super.findTarget(x, y, range, air, ground);
    }
}
