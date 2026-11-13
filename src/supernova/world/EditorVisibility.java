package supernova.world;

import arc.scene.Element;
import arc.scene.Group;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Tex;
import mindustry.type.Planet;
import mindustry.ui.Styles;
import supernova.content.SnTeams;

/**
 * Map editor tweaks:
 * <ul>
 *   <li>Supernova content is Aphelion-only, which would hide it whenever a map's planet isn't
 *   Aphelion. While the editor is open, it's shown on every planet; Aphelion-only is restored on close.</li>
 *   <li>Adds a seventh button to the editor's team picker for the Aliens (black) team.</li>
 * </ul>
 */
public final class EditorVisibility {
    private static final ObjectMap<UnlockableContent, Seq<Planet>> saved = new ObjectMap<>();
    private static boolean teamButtonAdded;

    private EditorVisibility() {}

    /** Client only; call once UI exists (ClientLoadEvent). */
    public static void init() {
        if (Vars.ui == null || Vars.ui.editor == null) return;
        Vars.ui.editor.shown(() -> {
            reveal();
            addAliensTeamButton();
            Vars.ui.editor.rebuildBlockSelection();
        });
        Vars.ui.editor.hidden(EditorVisibility::restore);
    }

    // ---------------------------------------------------------------- content visibility

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

    // ---------------------------------------------------------------- team picker

    /**
     * The editor builds one button per entry of the fixed-size {@link Team#baseTeams} array.
     * Find the last of those buttons (Blue) and add an Aliens button after it, in the same group.
     */
    private static void addAliensTeamButton() {
        if (teamButtonAdded) return;
        Team last = Team.baseTeams[Team.baseTeams.length - 1];
        ImageButton anchor = findTeamButton(Vars.ui.editor, last);
        if (anchor == null || !(anchor.parent instanceof Table table)) {
            Log.warn("[Supernova] Couldn't find the editor's team picker; Aliens team button not added.");
            teamButtonAdded = true;
            return;
        }

        Team aliens = SnTeams.aliens;
        ImageButton button = new ImageButton(Tex.whiteui, Styles.clearNoneTogglei);
        button.margin(4f);
        button.getImageCell().grow();
        button.getStyle().imageUpColor = aliens.color;
        button.clicked(() -> Vars.editor.drawTeam = aliens);
        button.update(() -> button.setChecked(Vars.editor.drawTeam == aliens));
        if (anchor.getButtonGroup() != null) anchor.getButtonGroup().add(button);

        var anchorCell = table.getCell(anchor);
        var cell = table.add(button);
        if (anchorCell != null) cell.size(anchorCell.prefWidth(), anchorCell.prefHeight());
        table.invalidateHierarchy();
        teamButtonAdded = true;
    }

    private static ImageButton findTeamButton(Element root, Team team) {
        if (root instanceof ImageButton b && b.getStyle() != null && b.getStyle().imageUpColor == team.color) return b;
        if (root instanceof Group g) {
            for (Element child : g.getChildren()) {
                ImageButton found = findTeamButton(child, team);
                if (found != null) return found;
            }
        }
        return null;
    }
}
