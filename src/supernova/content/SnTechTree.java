package supernova.content;

import static mindustry.content.TechTree.*;
import static supernova.content.SnBlocks.*;
import static supernova.content.SnItems.*;
import static supernova.content.SnUnits.*;

public final class SnTechTree {
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
    }
}
