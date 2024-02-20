package io.github.densamisten.firstfootsteps.event;

import com.mojang.blaze3d.platform.ClipboardManager;
import io.github.densamisten.firstfootsteps.FirstFootsteps;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FirstFootsteps.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FirstFootstepsEvents {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        final ClipboardManager clipboardManager = new ClipboardManager();

        Player player = event.getEntity();
        Level world = player.level();

        BlockPos playerPos = player.getOnPos();

        BlockPos closestTreePos = findClosestTree(world, playerPos);

        if (closestTreePos != null) {
            // Do something with the closest tree position
            player.displayClientMessage(Component.literal("Closest tree found at: " + closestTreePos.getX() + ", " + closestTreePos.getY() + ", " + closestTreePos.getZ()), false);
            placeExperienceOrbsBetween(player, (ServerLevel) world, playerPos, closestTreePos);
        } else {
            player.displayClientMessage(Component.literal("No trees found nearby."), false);
        }
    }

    private static BlockPos findClosestTree(Level world, BlockPos pos) {
        // You can implement your tree search algorithm here
        // For simplicity, let's assume a simple search around the player's position
        int searchRadius = 10; // Define your search radius
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos currentPos = pos.offset(x, y, z);
                    if (world.getBlockState(currentPos).getBlock() == Blocks.OAK_LOG) {
                        return currentPos;
                    }
                }
            }
        }
        return null;
    }

    private static void placeExperienceOrbsBetween(Player player, ServerLevel world, BlockPos start, BlockPos end) {
        Vec3 startVec = new Vec3(start.getX(), start.getY(), start.getZ());
        Vec3 endVec = new Vec3(end.getX(), end.getY(), end.getZ());
        Vec3 direction = endVec.subtract(startVec).normalize();

        double distance = startVec.distanceTo(endVec);
        for (double i = 0; i < distance; i++) {
            double newX = startVec.x() + direction.x() * i;
            double newY = startVec.y() + direction.y() * i;
            double newZ = startVec.z() + direction.z() * i;
            BlockPos blockPos = new BlockPos((int) newX, (int) newY, (int) newZ);
            world.addFreshEntity(new ExperienceOrb(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1));
        }
    }
}
