package fr.bananasmoothii.minestomentityrotationtests;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.DirectFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.timer.TaskSchedule;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        // Set the ChunkGenerator
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.STONE));


        // spawn entity
        Entity entity = new Entity(EntityType.ITEM_FRAME);
        ItemFrameMeta itemFrameMeta = (ItemFrameMeta) entity.getEntityMeta();
        itemFrameMeta.setInvisible(false);
        entity.setInstance(instanceContainer, new Pos(0.0, 41.0, 100.0));

        itemFrameMeta.setItem(ItemStack.builder(Material.FILLED_MAP).meta(MapMeta.class, meta -> meta.mapId(0)).build());

        // create map data
        var fb = new DirectFramebuffer();
        byte[] colors = fb.getColors();
        Arrays.fill(colors, MapColors.COLOR_GREEN.baseColor());
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                colors[x + y * 128] = MapColors.COLOR_RED.baseColor();
            }
        }
        MapDataPacket mapData = fb.preparePacket(0);

        itemFrameMeta.setOrientation(ItemFrameMeta.Orientation.NORTH);

        Entity pig = new Entity(EntityType.PIG);
        pig.setInstance(instanceContainer, new Pos(4.0, 41.0, 104.0, 48f, 20f));

        minecraftServer.start("0.0.0.0", 25565);

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 41, 0));
            player.setGameMode(GameMode.CREATIVE);

            MinecraftServer.getSchedulerManager().scheduleTask(
                    () -> {
                        player.sendMessage("Sending map data " + (System.currentTimeMillis() / 1000 % 60));
                        player.getPlayerConnection().sendPacket(mapData);
                    },
                    TaskSchedule.seconds(1), TaskSchedule.seconds(20));

        });

    }
}
