package keystrokesmod.client.module.modules.other;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.EventTiming;
import keystrokesmod.client.event.impl.UpdateEvent;
import keystrokesmod.client.mixin.mixins.PlayerControllerMPAccessor;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SlotHandler extends Module {
    private enum Mode {
        DEFAULT,
        SILENT
    }
    private final ComboSetting mode = new ComboSetting("Mode", Mode.DEFAULT);
    private final SliderSetting switchBackDelay = new SliderSetting("Switch back delay (ms)", 100, 0, 1000, 10);
    private static @Nullable Integer currentSlot = null;
    private static long lastSetCurrentSlotTime = -1;
    public SlotHandler() {
        super("Slot Handler", ModuleCategory.other);
        this.registerSettings(mode, switchBackDelay, new DescriptionSetting("Above setting only works in Silent mode"));
    }

    public static int getCurrentSlot() {
        if (currentSlot != null)
            return currentSlot;
        return mc.thePlayer.inventory.currentItem;
    }

    public static @Nullable ItemStack getHeldItem() {
        final InventoryPlayer inventory = mc.thePlayer.inventory;
        if (currentSlot != null)
            return currentSlot < 9 && currentSlot >= 0 ? inventory.mainInventory[currentSlot] : null;
        return getRenderHeldItem();
    }

    public static @Nullable ItemStack getRenderHeldItem() {
        final InventoryPlayer inventory = mc.thePlayer.inventory;
        return inventory.currentItem < 9 && inventory.currentItem >= 0 ? inventory.mainInventory[inventory.currentItem] : null;
    }

    public static void setCurrentSlot(int slot) {
        if (slot != -1) {
            currentSlot = slot;
            lastSetCurrentSlotTime = System.currentTimeMillis();
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if(event.getTiming() == EventTiming.POST)
            return;
        switch ((Mode) mode.getMode()) {
            case DEFAULT:
                mc.thePlayer.inventory.currentItem = getCurrentSlot();
                currentSlot = null;
                break;
            case SILENT:
                if (currentSlot != null
                        && !((PlayerControllerMPAccessor) mc.playerController).isHittingBlock()
                        && System.currentTimeMillis() - lastSetCurrentSlotTime > switchBackDelay.getInput())
                    currentSlot = null;
                break;
        }
    }
}
