package keystrokesmod.client.module.modules.render;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.impl.ForgeEvent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.player.BedAura;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.BlockUtils;
import keystrokesmod.client.utils.Reflection;
import keystrokesmod.client.utils.Utils;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class BreakProgress extends Module {
    private ComboSetting mode;
    private TickSetting manual;

    //private TickSetting bedAura;
    private enum Modes {
        Percentage,
        Second,
        Decimal
    }

    private double progress;
    private BlockPos block;
    private String progressStr;

    public BreakProgress() {
        super("BreakProgress", ModuleCategory.render);
        this.registerSetting(mode = new ComboSetting("Mode", Modes.Percentage));
        this.registerSetting(manual = new TickSetting("Show manual", true));
        //this.registerSetting(bedAura = new TickSetting("Show BedAura", true)); as bed aura needs to be rewritten, this is disabled
    }

    @Subscribe
    public void onForgeEvent(ForgeEvent fe) {
        if (fe.getEvent() instanceof RenderWorldLastEvent) {
            if (!Utils.Player.isPlayerInGame() || this.progress == 0.0f || this.block == null) {
                return;
            }
            final double n = this.block.getX() + 0.5 - mc.getRenderManager().viewerPosX;
            final double n2 = this.block.getY() + 0.5 - mc.getRenderManager().viewerPosY;
            final double n3 = this.block.getZ() + 0.5 - mc.getRenderManager().viewerPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) n, (float) n2, (float) n3);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-0.02266667f, -0.02266667f, -0.02266667f);
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            mc.fontRendererObj.drawString(this.progressStr, (float) (-mc.fontRendererObj.getStringWidth(this.progressStr) / 2), -3.0f, -1, true);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }

    private void setProgress() {
        switch ((Modes) mode.getMode()) {
            case Percentage: {
                this.progressStr = (int) (100.0 * (this.progress / 1.0)) + "%";
                break;
            }
            case Second: {
                double timeLeft = Utils.random.rnd((double) ((1.0f - this.progress) / BlockUtils.getBlockHardness(BlockUtils.getBlock(this.block), mc.thePlayer.getHeldItem(), false, false)) / 20.0, 1);
                this.progressStr = timeLeft == 0 ? "0" : timeLeft + "s";
                break;
            }
            case Decimal: {
                this.progressStr = String.valueOf(Utils.random.rnd(this.progress, 2));
                break;
            }
        }
    }

    public void onUpdate() {
        if (mc.thePlayer.capabilities.isCreativeMode || !mc.thePlayer.capabilities.allowEdit) {
            this.resetVariables();
            return;
        }
//        if (bedAura.isToggled() && Raven.moduleManager.getModuleByClazz(BedAura.class) != null && Raven.moduleManager.getModuleByClazz(BedAura.class).isEnabled() && BedAura.breakProgress != 0.0f && BedAura.currentBlock != null && !(BlockUtils.getBlock(BedAura.currentBlock) instanceof BlockBed)) {
//            this.progress = Math.min(1.0, BedAura.breakProgress);
//            this.block = BedAura.currentBlock;
//            if (this.block == null) {
//                return;
//            }
//            this.setProgress();
//            return;
//        } as bed aura needs to be rewritten, this is disabled
        if (!manual.isToggled() || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            this.resetVariables();
            return;
        }
        try {
            this.progress = Reflection.curBlockDamageMP.getFloat(mc.playerController);
            if (this.progress == 0.0f) {
                this.resetVariables();
                return;
            }
            this.block = mc.objectMouseOver.getBlockPos();
            this.setProgress();
        } catch (IllegalAccessException ex) {
        }
    }

    public void onDisable() {
        this.resetVariables();
    }

    private void resetVariables() {
        this.progress = 0.0f;
        this.block = null;
        this.progressStr = "";
    }
}
