import arc.*;
import arc.math.geom.Vec2;
import arc.mock.MockSettings;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.*;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.net.Net;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import supernova.Supernova;
import supernova.content.*;

/**
 * Headless balance simulator for Supernova. Runs real Mindustry simulation code (no window):
 * builds a flat arena, places turrets/walls, spawns Aliens that charge straight at them, and
 * reports who wins and how long it takes.
 *
 * Build & run (from repo root, after ./gradlew modJar):
 *   javac -cp "$GAME/desktop.jar:build/classes/java/main" -d build/sim tools/balance-sim/BalanceSim.java
 *   java  -cp "$GAME/desktop.jar:build/classes/java/main:build/sim" BalanceSim
 */
public class BalanceSim {
    static Team player = Team.sharded;
    static boolean debug;

    public static void main(String[] args) {
        boot();
        System.out.println("=== Supernova balance sim (60 ticks = 1 s) ===\n");
        if (args.length > 0 && args[0].equals("debug")) { debug = true; run("debug", 1, SnItems.ferrite, 0, SnAliens.hatchling, 1); return; }

        // --- Bolters vs each Alien, ferrite and ferrosteel ammo
        for (Item ammo : new Item[]{SnItems.ferrite, SnItems.ferrosteel}) {
            for (UnitType alien : new UnitType[]{SnAliens.hatchling, SnAliens.malign, SnAliens.leacher, SnAliens.ripper}) {
                for (int bolters : new int[]{1, 2}) {
                    run(bolters + " Bolter(" + ammo.name.replace("supernova-", "") + ") vs 1 " + alien.name, bolters, ammo, 0, alien, 1);
                }
            }
            System.out.println();
        }
        // --- Landing Site-style swarms vs 2 Bolters (ferrite), with and without walls
        for (int n : new int[]{2, 4, 6, 8}) run("2 Bolter(ferrite) vs " + n + " hatchling", 2, SnItems.ferrite, 0, SnAliens.hatchling, n);
        for (int n : new int[]{4, 8}) run("2 Bolter(ferrite)+walls vs " + n + " hatchling", 2, SnItems.ferrite, 1, SnAliens.hatchling, n);
        System.out.println();
        for (int n : new int[]{1, 2, 3}) run("2 Bolter(ferrosteel)+walls vs " + n + " leacher", 2, SnItems.ferrosteel, 1, SnAliens.leacher, n);
        for (int n : new int[]{1, 2}) run("3 Bolter(ferrosteel)+walls vs " + n + " ripper", 3, SnItems.ferrosteel, 1, SnAliens.ripper, n);
        System.out.println();
        structures();
        System.out.println();
        combat();
        System.out.println();
        infestation();
    }

    /** Hive spreading over time, and the Hatchling swarm dive. */
    static void infestation() {
        // Hive spread: one Hive in an empty arena; count Hives every 4 minutes for 13 minutes.
        arena(120, 80);
        Vars.world.tile(60, 40).setBlock(SnAlienBlocks.hive, SnTeams.aliens, 0);
        StringBuilder sb = new StringBuilder("Hive spread (count over time): ");
        int t = 0;
        for (int minute : new int[]{1, 5, 9, 13}) {
            for (; t < minute * 3600; t++) {
                step();
                Groups.unit.each(u -> u.controller(new IdleAI()));
            }
            int hives = Groups.build.count(b -> b.block == SnAlienBlocks.hive);
            sb.append(minute).append("min:").append(hives).append("  ");
        }
        System.out.println(sb + "(cluster cap 3)");
        Groups.clear();

        // Swarm dive: a Hatchling with the real SwarmAI, a lone drill 10 tiles away in the open.
        arena(60, 40);
        Vars.world.tile(20, 20).setBlock(SnBlocks.ferriteDrill, player, 0);
        Building drill = Vars.world.tile(20, 20).build;
        Unit u = SnAliens.hatchling.spawn(SnTeams.aliens, 33 * 8f, 20 * 8f);
        float startDst = u.dst(drill);
        int tick = 0;
        try {
            for (; tick < 60 * 40 && drill.isValid(); tick++) step();
            System.out.printf("Swarm dive: Hatchling started %.0f tiles from a lone drill; drill %s after %.1fs (closest approach %.0f tiles)%n",
                startDst / 8f, drill.isValid() ? "SURVIVED" : "DESTROYED", tick / 60f, u.dst(drill) / 8f);
        } catch (Throwable e) {
            System.out.println("Swarm dive: couldn't simulate (" + e + ")");
        }
        Groups.clear();
    }

    /** Player gunships vs Aliens (both sides charge), and the new turrets vs Hatchling swarms. */
    static void combat() {
        battle("2 Sentry vs 3 hatchling", SnUnits.sentry, 2, SnAliens.hatchling, 3);
        battle("2 Sentry vs 1 leacher", SnUnits.sentry, 2, SnAliens.leacher, 1);
        battle("2 Warden vs 1 leacher", SnUnits.warden, 2, SnAliens.leacher, 1);
        battle("2 Warden vs 2 malign", SnUnits.warden, 2, SnAliens.malign, 2);
        battle("1 Bastion vs 1 ripper", SnUnits.bastion, 1, SnAliens.ripper, 1);
        battle("2 Bastion vs 2 ripper", SnUnits.bastion, 2, SnAliens.ripper, 2);
        siege("2 Bastion vs Acid Spire + 2 Acid Pods", SnUnits.bastion, 2);
        siege("4 Warden vs Acid Spire + 2 Acid Pods", SnUnits.warden, 4);
        System.out.println();
        for (int n : new int[]{6, 10, 14}) turretVsSwarm("1 Igniter (volatiles) vs " + n + " hatchling", SnBlocks.igniter, n);
        for (int n : new int[]{6, 10, 14}) turretVsSwarm("1 Arc Coil (powered) vs " + n + " hatchling", SnBlocks.arcCoil, n);
        for (int n : new int[]{6, 10, 14}) turretVsSwarm("1 Bolter (ferrite) vs " + n + " hatchling", SnBlocks.bolter, n);
    }

    static void battle(String label, UnitType mine, int mineCount, UnitType alien, int alienCount) {
        arena(80, 40);
        Seq<Unit> a = new Seq<>(), b = new Seq<>();
        for (int i = 0; i < mineCount; i++) { Unit u = mine.spawn(player, 12 * 8f, (18 + i * 3) * 8f); u.controller(new ChargeAI()); a.add(u); }
        for (int i = 0; i < alienCount; i++) { Unit u = alien.spawn(SnTeams.aliens, 66 * 8f, (18 + i * 3) * 8f); u.controller(new ChargeAI()); b.add(u); }
        int tick = 0;
        for (; tick < 60 * 120; tick++) {
            step();
            if (a.allMatch(u -> u.dead() || !u.isAdded()) || b.allMatch(u -> u.dead() || !u.isAdded())) break;
        }
        int la = a.count(u -> !u.dead() && u.isAdded()), lb = b.count(u -> !u.dead() && u.isAdded());
        System.out.printf("%-48s %s  %5.1fs  mine left %d/%d   aliens left %d/%d%n", label,
            lb == 0 ? "PLAYER WINS" : la == 0 ? "ALIENS WIN " : "TIMEOUT    ", tick / 60f, la, mineCount, lb, alienCount);
        Groups.clear();
    }

    static void siege(String label, UnitType mine, int count) {
        arena(80, 40);
        Vars.world.tile(60, 20).setBlock(SnAlienBlocks.acidSpire, SnTeams.aliens, 0);
        Vars.world.tile(58, 15).setBlock(SnAlienBlocks.sporePod, SnTeams.aliens, 0);
        Vars.world.tile(58, 25).setBlock(SnAlienBlocks.sporePod, SnTeams.aliens, 0);
        Seq<Unit> a = new Seq<>();
        for (int i = 0; i < count; i++) { Unit u = mine.spawn(player, 12 * 8f, (16 + i * 3) * 8f); u.controller(new ChargeAI()); a.add(u); }
        int tick = 0;
        for (; tick < 60 * 150; tick++) {
            step();
            boolean baseDead = Groups.build.count(bb -> bb.team == SnTeams.aliens) == 0;
            if (baseDead || a.allMatch(u -> u.dead() || !u.isAdded())) break;
        }
        int left = a.count(u -> !u.dead() && u.isAdded());
        int structures = Groups.build.count(bb -> bb.team == SnTeams.aliens);
        System.out.printf("%-48s %s  %5.1fs  mine left %d/%d   alien structures left %d/3%n", label,
            structures == 0 ? "PLAYER WINS" : left == 0 ? "ALIENS WIN " : "TIMEOUT    ", tick / 60f, left, count, structures);
        Groups.clear();
    }

    static void turretVsSwarm(String label, Block turret, int count) {
        arena(80, 40);
        Vars.state.rules.teams.get(player).cheat = true; // free power for powered turrets
        Tile t = Vars.world.tile(10, 20);
        t.setBlock(turret, player, 0);
        Building b = t.build;
        Seq<Unit> units = new Seq<>();
        for (int i = 0; i < count; i++) {
            Unit u = SnAliens.hatchling.spawn(SnTeams.aliens, (60 + (i % 3) * 2) * 8f, (17 + (i / 3) * 2 + (i % 3)) * 8f);
            u.controller(new ChargeAI());
            units.add(u);
        }
        int tick = 0;
        for (; tick < 60 * 120; tick++) {
            if (b.isValid()) {
                if (b.block.hasLiquids && b.liquids != null) b.handleLiquid(b, SnLiquids.volatiles, b.block.liquidCapacity - b.liquids.get(SnLiquids.volatiles));
                while (b.acceptItem(null, SnItems.ferrite)) b.handleItem(null, SnItems.ferrite);
            }
            step();
            if (!b.isValid() || units.allMatch(u -> u.dead() || !u.isAdded())) break;
        }
        int alive = units.count(u -> !u.dead() && u.isAdded());
        System.out.printf("%-48s %s  %5.1fs  turret HP %.0f   aliens left %d/%d%n", label,
            alive == 0 ? "PLAYER WINS" : !b.isValid() ? "ALIENS WIN " : "TIMEOUT    ", tick / 60f, b.isValid() ? b.health : 0f, alive, count);
        Groups.clear();
    }

    /** Alien structures: Hive spawn rate and cap, Chitin Wall regrowth, turrets vs Bolters. */
    static void structures() {
        // Hive: count Hatchlings over 3 minutes (cap is 3 alive nearby, one per 40 s).
        arena(60, 40);
        Tile hiveTile = Vars.world.tile(30, 20);
        hiveTile.setBlock(SnAlienBlocks.hive, SnTeams.aliens, 0);
        int[] at = {60 * 45, 60 * 85, 60 * 125, 60 * 180};
        StringBuilder sb = new StringBuilder("Hive (idle Hatchlings stay put): ");
        int t = 0;
        for (int mark : at) {
            for (; t < mark; t++) step();
            int n = Groups.unit.count(u -> u.type == SnAliens.hatchling);
            sb.append(String.format("%ds:%d  ", mark / 60, n));
            Groups.unit.each(u -> u.controller(new IdleAI()));
        }
        System.out.println(sb);
        Groups.clear();

        // Chitin Wall: damage to 50%, measure HP 10 s later.
        arena(20, 20);
        Tile wt = Vars.world.tile(10, 10);
        wt.setBlock(SnAlienBlocks.chitinWall, SnTeams.aliens, 0);
        Building wall = wt.build;
        wall.health = wall.maxHealth * 0.5f;
        float before = wall.health;
        for (int i = 0; i < 600; i++) step();
        System.out.printf("Chitin Wall regrowth: %.0f -> %.0f HP in 10 s (max %.0f)%n", before, wall.health, wall.maxHealth);
        Groups.clear();

        // Turret duels at their own max range.
        duel("Acid Pod vs 1 Bolter(ferrite)", SnAlienBlocks.sporePod, 1);
        duel("Acid Spire vs 1 Bolter(ferrite)", SnAlienBlocks.acidSpire, 1);
        duel("Acid Spire vs 2 Bolter(ferrite)", SnAlienBlocks.acidSpire, 2);
    }

    /** Places an Alien turret, then Bolters just inside the longer of the two ranges. */
    static void duel(String label, Block alienTurret, int bolters) {
        arena(80, 40);
        Tile at = Vars.world.tile(60, 20);
        at.setBlock(alienTurret, SnTeams.aliens, 0);
        float range = Math.max(((mindustry.world.blocks.defense.turrets.Turret) alienTurret).range, ((ItemTurret) SnBlocks.bolter).range) - 12f;
        int bx = 60 - (int) (range / 8f);
        Seq<Building> turrets = new Seq<>();
        for (int i = 0; i < bolters; i++) {
            Tile t = Vars.world.tile(bx, 20 - bolters + 1 + i * 2);
            t.setBlock(SnBlocks.bolter, player, 0);
            turrets.add(t.build);
        }
        Building enemy = at.build;
        int tick = 0;
        for (; tick < 60 * 120; tick++) {
            for (Building b : turrets) if (b.isValid()) while (b.acceptItem(null, SnItems.ferrite)) b.handleItem(null, SnItems.ferrite);
            step();
            if (!enemy.isValid() || turrets.allMatch(b -> !b.isValid())) break;
        }
        String result = !enemy.isValid() ? "PLAYER WINS" : turrets.allMatch(b -> !b.isValid()) ? "ALIENS WIN " : "TIMEOUT    ";
        System.out.printf("%-48s %s  %5.1fs  bolters left %d/%d   (alien range %.0f, bolter range %.0f)%n", label, result, tick / 60f,
            turrets.count(Building::isValid), bolters, ((mindustry.world.blocks.defense.turrets.Turret) alienTurret).range, ((ItemTurret) SnBlocks.bolter).range);
        Groups.clear();
    }

    static void arena(int w, int h) {
        Vars.world.beginMapLoad();
        Tiles tiles = Vars.world.resize(w, h);
        for (int x = 0; x < w; x++) for (int y = 0; y < h; y++) tiles.set(x, y, new Tile(x, y, Blocks.stone, Blocks.air, Blocks.air));
        Vars.state.rules = new mindustry.game.Rules();
        Vars.state.rules.waveTeam = SnTeams.aliens;
        Vars.state.rules.unitCapVariable = false; // no cores in the arena: don't let the unit cap delete units
        Vars.state.rules.unitCap = 999;
        Vars.world.endMapLoad();
        Groups.resize(-64f, -64f, w * 8f + 128f, h * 8f + 128f);
        Vars.state.set(GameState.State.playing);
    }

    static void step() {
        Time.delta = 1f;
        Time.time += 1f;
        Vars.state.teams.updateTeamStats();
        Groups.bullet.updatePhysics();
        Groups.unit.updatePhysics();
        Groups.unit.update();
        Groups.bullet.update();
        Groups.build.update();
        Groups.bullet.collide();
    }

    static class IdleAI extends AIController {
        @Override
        public void updateMovement() {}
    }

    static void boot() {
        Core.app = new Application() {
            final Seq<ApplicationListener> listeners = new Seq<>();
            public Seq<ApplicationListener> getListeners() { return listeners; }
            public ApplicationType getType() { return ApplicationType.headless; }
            public String getClipboardText() { return ""; }
            public void setClipboardText(String text) {}
            public void post(Runnable r) { r.run(); }
            public void exit() {}
        };
        Core.settings = new MockSettings();
        Core.files = new arc.backend.sdl.SdlFiles();
        Core.graphics = new arc.mock.MockGraphics();
        Core.audio = new arc.mock.MockAudio();
        Vars.headless = true;
        Vars.net = new Net(null);
        Vars.tree = new FileTree();
        Vars.init();
        Vars.content.createBaseContent();
        // Register as a mod so content gets the "supernova-" prefix, exactly as in the game.
        mindustry.mod.Mods.ModMeta meta = new mindustry.mod.Mods.ModMeta();
        meta.name = "supernova";
        Supernova main = new Supernova();
        mindustry.mod.Mods.LoadedMod mod = new mindustry.mod.Mods.LoadedMod(new arc.files.Fi("."), new arc.files.Fi("."), main, BalanceSim.class.getClassLoader(), meta);
        Vars.content.setCurrentMod(mod);
        main.loadContent();
        Vars.content.setCurrentMod(null);
        Vars.content.init();
        Vars.logic = new Logic();
        Vars.world = new World();
        Vars.state = new GameState();
    }

    /** One fight in a fresh arena. Returns nothing; prints a line. */
    static void run(String label, int bolters, Item ammo, int wallRows, UnitType alien, int count) {
        int w = 80, h = 40;
        Vars.world.beginMapLoad();
        Tiles tiles = Vars.world.resize(w, h);
        for (int x = 0; x < w; x++) for (int y = 0; y < h; y++) tiles.set(x, y, new Tile(x, y, Blocks.stone, Blocks.air, Blocks.air));
        Vars.state.rules = new mindustry.game.Rules();
        Vars.state.rules.waveTeam = SnTeams.aliens;
        Vars.world.endMapLoad();
        Groups.resize(-64f, -64f, w * 8f + 128f, h * 8f + 128f); // spatial index used for targeting
        Vars.state.set(GameState.State.playing);

        // Bolters in a column on the left, fully stocked with the chosen ammo.
        Seq<Building> turrets = new Seq<>();
        int cy = h / 2;
        for (int i = 0; i < bolters; i++) {
            Tile t = tiles.get(10, cy - bolters + 1 + i * 2);
            t.setBlock(SnBlocks.bolter, player, 0);
            ItemTurret.ItemTurretBuild b = (ItemTurret.ItemTurretBuild) t.build;
            for (int k = 0; k < 400 && b.acceptItem(null, ammo); k++) b.handleItem(null, ammo);
            turrets.add(b);
        }
        // Optional ferrite wall line in front.
        if (wallRows > 0) for (int y = cy - 6; y <= cy + 6; y++) tiles.get(15, y).setBlock(SnBlocks.ferriteWall, player, 0);

        // Aliens start out of range on the right and charge.
        Seq<Unit> units = new Seq<>();
        for (int i = 0; i < count; i++) {
            Unit u = alien.spawn(SnTeams.aliens, (60 + (i % 3) * 2) * 8f, (cy - 3 + (i / 3) * 3 + (i % 3)) * 8f);
            u.controller(new ChargeAI());
            units.add(u);
        }

        int maxTicks = 60 * 120, tick = 0;
        Time.delta = 1f;
        for (; tick < maxTicks; tick++) {
            Time.delta = 1f;
            Time.time += 1f;
            // Keep ammo topped up, as if a conveyor were feeding each Bolter.
            for (Building b : turrets) if (b.isValid()) while (b.acceptItem(null, ammo)) b.handleItem(null, ammo);
            Vars.state.teams.updateTeamStats(); // per-team unit indexes used by turret targeting
            // Same order as the game's Groups.update(), per entity group.
            Groups.bullet.updatePhysics();
            Groups.unit.updatePhysics();
            Groups.unit.update();
            Groups.bullet.update();
            Groups.build.update();
            Groups.bullet.collide();
            if (debug && tick % 120 == 0) {
                Unit u = units.first(); Building b = turrets.first();
                System.out.printf("calls=" + calls + " all=" + Groups.all.size() + " vel=" + units.first().vel + " t=%d unitsInGroup=%d u=(%.0f,%.0f) added=%b ctrl=%s hp=%.0f target=%s | turret hp=%.0f ammo=%d isShooting=%b gamestate=%s%n",
                    tick, Groups.unit.size(), u.x, u.y, u.isAdded(), u.controller().getClass().getSimpleName(), u.health,
                    "-", b.health, ((ItemTurret.ItemTurretBuild)b).totalAmmo, ((ItemTurret.ItemTurretBuild)b).isShooting(), Vars.state.getState());
            }
            boolean aliensDead = units.allMatch(u -> u.dead() || !u.isAdded());
            boolean turretsDead = turrets.allMatch(b -> b.dead() || !b.isValid());
            if (aliensDead || turretsDead) break;
        }
        int alive = units.count(u -> !u.dead() && u.isAdded());
        int turretsLeft = turrets.count(b -> !b.dead() && b.isValid());
        float turretHp = 0;
        for (Building b : turrets) if (b.isValid()) turretHp += b.health;
        String result = alive == 0 ? "PLAYER WINS" : turretsLeft == 0 ? "ALIENS WIN " : "TIMEOUT    ";
        System.out.printf("%-48s %s  %5.1fs  bolters left %d/%d (%.0f HP)  aliens left %d/%d%n",
            label, result, tick / 60f, turretsLeft, bolters, turretHp, alive, count);
        Groups.clear();
    }

    /** Walk straight at the nearest player building and fight; no pathfinding needed in an open arena. */
    static int calls;
    static class ChargeAI extends AIController {
        @Override
        public void updateMovement() {
            calls++;
            mindustry.gen.Teamc target = null;
            float best = Float.MAX_VALUE;
            for (Building b : Groups.build) {
                if (b.team == unit.team) continue;
                float d = b.dst2(unit);
                if (d < best) { best = d; target = b; }
            }
            for (Unit u : Groups.unit) {
                if (u.team == unit.team || u.dead()) continue;
                float d = u.dst2(unit);
                if (d < best) { best = d; target = u; }
            }
            if (target != null) {
                moveTo(target, unit.type.range * 0.7f);
                unit.lookAt(target);
            }
        }
    }
}
