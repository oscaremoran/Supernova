package supernova.world;

import mindustry.world.blocks.defense.Wall;

/** An Alien wall that slowly regrows when damaged. */
public class ChitinWall extends Wall {
    /** Fraction of max health regained per second. */
    public float regen = 0.01f;

    public ChitinWall(String name) {
        super(name);
        update = true;
        buildType = ChitinWallBuild::new;
    }

    public class ChitinWallBuild extends WallBuild {
        @Override
        public void updateTile() {
            if (damaged() && !dead) heal(maxHealth * regen / 60f * delta());
        }
    }
}
