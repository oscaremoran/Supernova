package supernova.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;

/** Visual effects for Supernova (Alien acid, Hive hatching). */
public final class SnFx {
    private SnFx() {}

    /** Alien death: a ring of acid droplets, a glowing splash, and a fading shockwave. */
    public static Effect acidBurst(Color glow, Color dark, float size) {
        return new Effect(40f, 100f, e -> {
            Draw.color(glow, dark, e.fin());
            Angles.randLenVectors(e.id, (int) (8 + size), size * 2.6f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, (1.6f + size * 0.12f) * e.fout());
            });
            Draw.color(glow, e.fout() * 0.6f);
            Fill.circle(e.x, e.y, size * 0.9f * e.fout(Interp.pow2Out));
            Lines.stroke(1.6f * e.fout());
            Draw.color(glow, e.fout());
            Lines.circle(e.x, e.y, size * 2.2f * e.finpow());
            Drawf.light(e.x, e.y, size * 4f * e.fout(), glow, 0.6f * e.fout());
            Draw.reset();
        });
    }

    /** Bullet impact: a small acid splash. */
    public static Effect acidHit(Color glow, Color dark) {
        return new Effect(18f, e -> {
            Draw.color(glow, dark, e.fin());
            Angles.randLenVectors(e.id, 5, 9f * e.finpow(), e.rotation + 180f, 70f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 1.5f * e.fout());
            });
            Draw.reset();
        });
    }

    /** Muzzle: a small puff of acid mist. */
    public static Effect acidShoot(Color glow) {
        return new Effect(14f, e -> {
            Draw.color(glow, Color.clear, e.fin());
            Angles.randLenVectors(e.id, 4, 6f * e.finpow(), e.rotation, 25f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 1.3f * e.fout());
            });
            Draw.reset();
        });
    }

    /** A Hive hatching or budding: a pulse ring with flying spores. */
    public static final Effect hiveHatch = new Effect(50f, 120f, e -> {
        Color glow = Color.valueOf("b98cff");
        Lines.stroke(2.5f * e.fout());
        Draw.color(glow, e.fout());
        Lines.circle(e.x, e.y, 6f + 26f * e.finpow());
        Draw.color(glow, Color.valueOf("6a3fd1"), e.fin());
        Angles.randLenVectors(e.id, 12, 30f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, 2f * e.fout()));
        Drawf.light(e.x, e.y, 60f * e.fout(), glow, 0.7f * e.fout());
        Draw.reset();
    });
}
