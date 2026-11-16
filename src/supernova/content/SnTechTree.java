package supernova.content;

import arc.struct.Seq;
import mindustry.game.Objectives.SectorComplete;
import mindustry.type.ItemStack;
import mindustry.type.SectorPreset;

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
                        node(overflowGate, () -> node(underflowGate));
                        node(container, () -> node(unloader));
                    });
                });
            });

            node(ferriteDrill, () -> {
                node(ferritePowerNode, () -> {
                    node(battery, () -> node(largePowerNode));
                    node(mender);
                    node(solarArray, () -> {
                        node(ferrosteelSmelter, () -> {
                            node(pressureDrill);
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
                node(igniter, () -> node(arcCoil));
                node(ferriteWall, () -> {
                    node(ferriteWallLarge);
                    node(ferrosteelWall, () -> {
                        node(ferrosteelWallLarge);
                    });
                });
            });

            node(foundry, () -> {
                node(sentry, () -> {
                    node(warden, () -> node(bastion));
                });
            });

            node(droneBay, () -> {
                node(prospector, () -> {
                    node(surveyor);
                    node(hauler);
                });
            });

            // Story sectors (only those whose maps exist).
            sectorChain(SnSectors.available(), 0);

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

    /** Each story sector unlocks after completing the previous one. */
    private static void sectorChain(Seq<SectorPreset> sectors, int i) {
        if (i >= sectors.size) return;
        SectorPreset s = sectors.get(i);
        if (i == 0) {
            node(s, () -> sectorChain(sectors, i + 1));
        } else {
            node(s, Seq.with(new SectorComplete(sectors.get(i - 1))), () -> sectorChain(sectors, i + 1));
        }
    }
}
