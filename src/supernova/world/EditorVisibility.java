package supernova.world;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Planet;

/**
 * Supernova content is Aphelion-only, which would hide it in the map editor whenever a map's
 * planet isn't Aphelion. While the editor is open, this makes all Supernova content visible on
 * every planet, then restores Aphelion-only when the editor closes.
 */
public final class EditorVisibility {
    private static final ObjectMap<UnlockableContent, Seq<Planet>> saved = new ObjectMap<>();

    private EditorVisibility() {}

    /** Client only; call once UI exists (ClientLoadEvent). */
    public static void init() {
        if (Vars.ui == null || Vars.ui.editor == null) return;
        Vars.ui.editor.shown(() -> {
            reveal();
            Vars.ui.editor.rebuildBlockSelection();
        });
        Vars.ui.editor.hidden(EditorVisibility::restore);
    }

    private static void reveal() {
        if (!saved.isEmpty()) return;
        for (Seq<Content> list : Vars.content.getContentMap()) {
            for (Content c : list) {
                if (!(c instanceof UnlockableContent u) || u.minfo.mod == null || !u.name.startsWith("supernova-")) continue;
                if (u.shownPlanets.isEmpty()) continue;
                saved.put(u, u.shownPlanets.toSeq());
                u.shownPlanets.clear(); // empty = shown on every planet
            }
        }
    }

    private static void restore() {
        for (ObjectMap.Entry<UnlockableContent, Seq<Planet>> e : saved) {
            e.key.shownPlanets.clear();
            e.key.shownPlanets.addAll(e.value);
        }
        saved.clear();
    }
}
