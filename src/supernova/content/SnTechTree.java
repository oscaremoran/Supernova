package supernova.content;

import mindustry.type.ItemStack;

import static mindustry.content.TechTree.*;
import static supernova.content.SnBlocks.*;
import static supernova.content.SnItems.*;
import static supernova.content.SnUnits.*;

public final class SnTechTree {
    /** Aphelion research costs as a fraction of the default (which is based on build cost). */
    public static float researchCostScale = 0.3f;

    private SnTechTree() {}

    public static void load() {
        SnPlanets.aphelion.techTree = nodeRoot("aphelion", coreOutpost, () -> {
            node(ferriteConveyor, () -> {
                node(ferriteJunction, () -> {
                    node(ferriteRouter, () -> {
                        node(ferriteBridge);
                    });
                });
            });

            node(ferriteDrill, () -> {
                node(ferritePowerNode, () -> {
                    node(solarArray, () -> {
                        node(ferrosteelSmelter, () -> {
                            node(orbitalUplink, () -> {
                                node(iceMelter, () -> {
                                    node(volatileBurner);
                                });
                                node(conductiumFabricator);
                            });
                        });
                    });
                });
            });

            node(bolter, () -> {
                node(ferriteWall, () -> {
                    node(ferriteWallLarge);
                    node(ferrosteelWall, () -> {
                        node(ferrosteelWallLarge);
                    });
                });
            });

            node(droneBay, () -> {
                node(prospector, () -> {
                    node(surveyor);
                    node(hauler);
                });
            });

            nodeProduce(ferrite, () -> {
                nodeProduce(cobaltine, () -> {
                    nodeProduce(iridium, () -> {
                        nodeProduce(conductium, () -> {});
                    });
                });
                nodeProduce(quartzite, () -> {
                    nodeProduce(ferrosteel, () -> {});
                    nodeProduce(cryoIce, () -> {
                        nodeProduce(SnLiquids.volatiles, () -> {});
                    });
                });
            });
        });

        // Cheaper research across the whole Aphelion tree.
        SnPlanets.aphelion.techTree.each(node -> {
            if (node.requirements == null) return;
            ItemStack[] scaled = new ItemStack[node.requirements.length];
            for (int i = 0; i < scaled.length; i++) {
                ItemStack r = node.requirements[i];
                scaled[i] = new ItemStack(r.item, Math.max(1, Math.round(r.amount * researchCostScale)));
            }
            node.requirements = scaled;
        });
    }
}
