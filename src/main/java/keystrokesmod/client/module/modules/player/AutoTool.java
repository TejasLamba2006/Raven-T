package keystrokesmod.client.module.modules.player;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.impl.Render2DEvent;
import keystrokesmod.client.event.impl.UpdateEvent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.combat.LeftClicker;
import keystrokesmod.client.module.modules.other.SlotHandler;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.BlockUtils;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.util.concurrent.ThreadLocalRandom;

public class AutoTool extends Module {
    private final SliderSetting hoverDelay;
    private final TickSetting rightDisable;
    private final TickSetting requireMouse;
    private final TickSetting swap;
    private final TickSetting sneakRequire;
    private int previousSlot = -1;
    private int ticksHovered;
    private BlockPos currentBlock;

    public AutoTool() {
        super("Auto Tool", ModuleCategory.player);

        this.registerSetting(hoverDelay = new SliderSetting("Hover delay", 0.0, 0.0, 20.0, 1.0));
        this.registerSetting(rightDisable = new TickSetting("Disable while right click", true));
        this.registerSetting(requireMouse = new TickSetting("Require mouse down", true));
        this.registerSetting(swap = new TickSetting("Swap to previous slot", true));
        this.registerSetting(sneakRequire = new TickSetting("Sneak require", false));
    }

    public void onDisable() {
        resetVariables();
    }

    public void setSlot(final int currentItem) {
        if (currentItem == -1) {
            return;
        }
        SlotHandler.setCurrentSlot(currentItem);
    }

    @Override
    public void onUpdate() {
        if (!Utils.Player.isPlayerInGame() || (rightDisable.isToggled() && Mouse.isButtonDown(1)) || !mc.thePlayer.capabilities.allowEdit) {
            resetVariables();
            return;
        }
        if (!Mouse.isButtonDown(0) && requireMouse.isToggled()) {
            resetSlot();
            return;
        }
        MovingObjectPosition over = mc.objectMouseOver;
        if (over == null || over.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || (sneakRequire.isToggled() && !mc.thePlayer.isSneaking())) {
            resetSlot();
            resetVariables();
            return;
        }
        if (over.getBlockPos().equals(currentBlock)) {
            ticksHovered++;
        }
        else {
            ticksHovered = 0;
        }
        currentBlock = over.getBlockPos();
        if (hoverDelay.getInput() == 0 || ticksHovered > hoverDelay.getInput()) {
            int slot = Utils.Player.getTool(BlockUtils.getBlock(currentBlock));
            if (slot == -1) {
                return;
            }
            if (previousSlot == -1) {
                previousSlot = SlotHandler.getCurrentSlot();
            }
            setSlot(slot);
        }
    }

    private void resetVariables() {
        ticksHovered = 0;
        resetSlot();
        previousSlot = -1;
    }

    private void resetSlot() {
        if (previousSlot == -1 || !swap.isToggled()) {
            return;
        }
        setSlot(previousSlot);
        previousSlot = -1;
    }
}
