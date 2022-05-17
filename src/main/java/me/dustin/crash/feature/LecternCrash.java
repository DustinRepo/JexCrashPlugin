package me.dustin.crash.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.SetScreenFilter;
import me.dustin.jex.event.misc.EventSetScreen;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

@Feature.Manifest(category = Feature.Category.MISC, description = "Sends a funny packet when you open a lectern")
public class LecternCrash extends Feature {

    public LecternCrash() {
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @EventPointer
    private final EventListener<EventSetScreen> eventSetScreenEventListener = new EventListener<>(eventSetScreen -> {
        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(Wrapper.INSTANCE.getLocalPlayer().currentScreenHandler.syncId, Wrapper.INSTANCE.getLocalPlayer().currentScreenHandler.getRevision(), 0, 0, SlotActionType.QUICK_MOVE, Wrapper.INSTANCE.getLocalPlayer().currentScreenHandler.getCursorStack().copy(), Int2ObjectMaps.emptyMap());
        NetworkHelper.INSTANCE.sendPacket(packet);
    }, new SetScreenFilter(LecternScreen.class));
}
