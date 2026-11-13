package supernova.world;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import supernova.content.SnTeams;
import mindustry.type.Planet;

/**
 * Map editor tweaks, active only while the editor is open:
 * <ul>
 *   <li>Supernova content is Aphelion-only, which would hide it whenever a map's planet isn't
 *   Aphelion. While editing, it's shown on every planet.</li>
 *   <li>The editor's team picker lists the fixed {@link Team#baseTeams} array. While editing, the
 *   rarely used Blue slot is swapped for the Aliens (black) team.</li>
 * </ul>
 * Everything is restored when the editor closes.
 */
public final class EditorVisibility {
    private static final ObjectMap<UnlockableContent, Seq<Planet>> saved = new ObjectMap<>();

    private EditorVisibility() {}

    /** Client only; call once UI exists (ClientLoadEvent). */
    public static void init() {
        if (Vars.ui == null || Vars.ui.editor == null) return;
        Vars.ui.editor.shown(() -> {
            reveal();
            swapTeam(Team.blue, SnTeams.aliens);
            Vars.ui.editor.rebuildBlockSelection();
        });
        Vars.ui.editor.hidden(() -> {
            restore();
            swapTeam(SnTeams.aliens, Team.blue);
        });
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

    private static void swapTeam(Team from, Team to) {
        for (int i = 0; i < Team.baseTeams.length; i++) {
            if (Team.baseTeams[i] == from) {
                Team.baseTeams[i] = to;
                return;
            }
        }
    }
}
