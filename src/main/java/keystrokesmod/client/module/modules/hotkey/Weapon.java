package keystrokesmod.client.module.modules.hotkey;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.other.SlotHandler;
import keystrokesmod.client.utils.Utils;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

public class Weapon extends Module {
    public Weapon() {
        super("Weapon", ModuleCategory.hotkey);
    }

    @Override
    public void onEnable() {
        if (!Utils.Player.isPlayerInGame())
            return;

        int index = -1;
        double damage = -1;

        for (int slot = 0; slot <= 8; slot++) {
            ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);
            if (itemInSlot == null)
                continue;
            for (AttributeModifier mooommHelp : itemInSlot.getAttributeModifiers().values()) {
                if (mooommHelp.getAmount() > damage) {
                    damage = mooommHelp.getAmount();
                    index = slot;
                }
            }

        }
        if (index > -1 && damage > -1) {
            if (SlotHandler.getCurrentSlot() != index) {
                SlotHandler.setCurrentSlot(index);
            }
        }
        this.disable();
    }
}
