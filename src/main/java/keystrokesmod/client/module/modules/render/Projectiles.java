package keystrokesmod.client.module.modules.render;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.impl.ForgeEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.world.AntiBot;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Projectiles extends Module {

    private TickSetting autoScale;
    private TickSetting disableUncharged;
    private TickSetting highlightOnEntity;
    private int highlightColor = new Color(234, 38, 38).getRGB();

    public Projectiles() {
        super("Projectiles", ModuleCategory.render);
        this.registerSetting(autoScale = new TickSetting("Auto-scale", true));
        this.registerSetting(disableUncharged = new TickSetting("Disable uncharged bow", true));
        this.registerSetting(highlightOnEntity = new TickSetting("Highlight on entity", true));
    }

    @Subscribe
    public void onForgeEvent(ForgeEvent fe) {
        if (fe.getEvent() instanceof RenderWorldLastEvent) {
            if (!Utils.Player.isPlayerInGame() || mc.thePlayer.getHeldItem() == null)
                return;
            EntityPlayerSP player = mc.thePlayer;

            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (!(heldItem.getItem() instanceof ItemBow) && !(heldItem.getItem() instanceof ItemSnowball) && !(heldItem.getItem() instanceof ItemEgg) && !(heldItem.getItem() instanceof ItemEnderPearl)) {
                return;
            }
            if (heldItem.getItem() instanceof ItemBow && !mc.thePlayer.isUsingItem() && disableUncharged.isToggled()) {
                return;
            }
            boolean bow = false;
            if (heldItem.getItem() instanceof ItemBow) {
                bow = true;
            }

            float playerYaw = mc.thePlayer.rotationYaw;
            float playerPitch = mc.thePlayer.rotationPitch;

            double posX = mc.getRenderManager().viewerPosX - (double) (MathHelper.cos(playerYaw / 180.0f * (float) Math.PI) * 0.16f);
            double posY = mc.getRenderManager().viewerPosY + (double) mc.thePlayer.getEyeHeight() - (double) 0.1f;
            double posZ = mc.getRenderManager().viewerPosZ - (double) (MathHelper.sin(playerYaw / 180.0f * (float) Math.PI) * 0.16f);

            double motionX = (double) (-MathHelper.sin(playerYaw / 180.0f * (float) Math.PI) * MathHelper.cos(playerPitch / 180.0f * (float) Math.PI)) * (bow ? 1.0 : 0.4);
            double motionY = (double) (-MathHelper.sin(playerPitch / 180.0f * (float) Math.PI)) * (bow ? 1.0 : 0.4);
            double motionZ = (double) (MathHelper.cos(playerYaw / 180.0f * (float) Math.PI) * MathHelper.cos(playerPitch / 180.0f * (float) Math.PI)) * (bow ? 1.0 : 0.4);
            int itemInUse = 40;
            if (mc.thePlayer.getItemInUseCount() > 0 && bow) {
                itemInUse = mc.thePlayer.getItemInUseCount();
            }
            int n10 = 72000 - itemInUse;
            float f10 = (float) n10 / 20.0f;
            if ((double) (f10 = (f10 * f10 + f10 * 2.0f) / 3.0f) < 0.1) {
                return;
            }
            if (f10 > 1.0f) {
                f10 = 1.0f;
            }
            RenderUtils.glColor(-1);
            GL11.glPushMatrix();
            boolean bl3 = GL11.glIsEnabled(2929);
            boolean bl4 = GL11.glIsEnabled(3553);
            boolean bl5 = GL11.glIsEnabled(3042);
            if (bl3) {
                GL11.glDisable(2929);
            }
            if (bl4) {
                GL11.glDisable(3553);
            }
            GL11.glEnable(2848);
            GL11.glBlendFunc(770, 771);
            if (!bl5) {
                GL11.glEnable(3042);
            }
            float f11 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= f11;
            motionY /= f11;
            motionZ /= f11;
            motionX *= (double) (bow ? f10 * 2.0f : 1.0f) * 1.5;
            motionY *= (double) (bow ? f10 * 2.0f : 1.0f) * 1.5;
            motionZ *= (double) (bow ? f10 * 2.0f : 1.0f) * 1.5;
            GL11.glLineWidth(1.5f);
            GL11.glBegin(3);
            boolean ground = false;
            MovingObjectPosition target = null;
            boolean highlight = false;
            double[] transform = new double[]{posX, posY, posZ, motionX, motionY, motionZ};
            for (int k = 0; k <= 100 && !ground; ++k) {
                Vec3 start = new Vec3(transform[0], transform[1], transform[2]);
                Vec3 predicted = new Vec3(transform[0] + transform[3], transform[1] + transform[4], transform[2] + transform[5]);
                MovingObjectPosition rayTraced = mc.theWorld.rayTraceBlocks(start, predicted, false, true, false);
                if (rayTraced == null) {
                    rayTraced = getEntityHit(start, predicted);
                    if (rayTraced != null) {
                        highlight = true;
                        break;
                    }
                    float f14 = 0.99f;
                    transform[4] *= f14;
                    transform[0] += (transform[3] *= f14);
                    transform[1] += (transform[4] -= bow ? 0.05 : 0.03);
                    transform[2] += (transform[5] *= f14);
                }
            }

            for (int k = 0; k <= 100 && !ground; ++k) {
                Vec3 start = new Vec3(posX, posY, posZ);
                Vec3 predicted = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                MovingObjectPosition rayTraced = mc.theWorld.rayTraceBlocks(start, predicted, false, true, false);
                if (rayTraced != null) {
                    ground = true;
                    target = rayTraced;
                } else {
                    MovingObjectPosition entityHit = getEntityHit(start, predicted);
                    if (entityHit != null) {
                        target = entityHit;
                        ground = true;
                    }
                }
                if (highlight && highlightOnEntity.isToggled()) {
                    RenderUtils.glColor(highlightColor);
                }
                float f14 = 0.99f;
                motionY *= f14;
                GL11.glVertex3d((posX += (motionX *= f14)) - mc.getRenderManager().viewerPosX, (posY += (motionY -= bow ? 0.05 : 0.03)) - mc.getRenderManager().viewerPosY, (posZ += (motionZ *= f14)) - mc.getRenderManager().viewerPosZ);
            }
            GL11.glEnd();
            GL11.glDisable(2929);
            GL11.glDisable(3042);
            GL11.glTranslated(posX - mc.getRenderManager().viewerPosX, posY - mc.getRenderManager().viewerPosY, posZ - mc.getRenderManager().viewerPosZ);
            if (target != null && target.sideHit != null) {
                switch (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK ? target.sideHit.getIndex() : target.sideHit.getIndex()) {
                    case 2:
                    case 3: {
                        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                        break;
                    }
                    case 4:
                    case 5: {
                        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                        break;
                    }
                }
            }
            if (autoScale.isToggled()) {
                double distance = Math.max(mc.thePlayer.getDistance(posX + motionX, posY + motionY, posZ + motionZ) * 0.042830285, 1);
                GL11.glScaled(distance, distance, distance);
            }
            this.drawX();
            GL11.glDisable(2848);
            if (bl3) {
                GL11.glEnable(2929);
            }
            if (bl4) {
                GL11.glEnable(3553);
            }
            if (!bl5) {
                GL11.glDisable(3042);
            }
            GL11.glPopMatrix();
        }
    }

    public MovingObjectPosition getEntityHit(Vec3 origin, Vec3 destination) {
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (!(e instanceof EntityLivingBase)) {
                continue;
            }
            if (e instanceof EntityPlayer && AntiBot.bot(e)) {
                continue;
            }
            if (e != mc.thePlayer) {
                float expand = 0.3f;
                AxisAlignedBB boundingBox = e.getEntityBoundingBox().expand(expand, expand, expand);
                MovingObjectPosition possibleHit = boundingBox.calculateIntercept(origin, destination);
                if (possibleHit != null) {
                    return possibleHit;
                }
            }
        }
        return null;
    }

    public void drawX() {
        GL11.glBegin(1);
        GL11.glVertex3d(-0.25, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, -0.25);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.25, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.25);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glEnd();
    }
}
