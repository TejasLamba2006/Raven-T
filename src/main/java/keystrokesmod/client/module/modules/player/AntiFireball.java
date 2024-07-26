package keystrokesmod.client.module.modules.player;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.EventTiming;
import keystrokesmod.client.event.impl.ForgeEvent;
import keystrokesmod.client.event.impl.PreMotionEvent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.modules.combat.aura.KillAura;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import keystrokesmod.client.event.impl.UpdateEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.RotationUtils;
import keystrokesmod.client.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.*;
import org.lwjgl.input.Mouse;

import java.util.HashSet;

public class AntiFireball extends Module {
    private SliderSetting fov;
    private SliderSetting range;
    private TickSetting disableWhileFlying;
    //private TickSetting disableWhileScaffold; as scaffold doesn't exist so
    private TickSetting blocksRotate;
    private TickSetting projectileRotate;
    public TickSetting silentSwing;
    public EntityFireball fireball;
    private HashSet<Entity> fireballs = new HashSet<>();
    public boolean attack;
    public AntiFireball() {
        super("AntiFireball", ModuleCategory.player);
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 4.0));
        this.registerSetting(range = new SliderSetting("Range", 8.0, 3.0, 15.0, 0.5));
        this.registerSetting(disableWhileFlying = new TickSetting("Disable while flying", false));
//        this.registerSetting(disableWhileScaffold = new TickSetting("Disable while scaffold", false)); as scaffold doesn't exist so
        this.registerSetting(blocksRotate = new TickSetting("Rotate with blocks", false));
        this.registerSetting(projectileRotate = new TickSetting("Rotate with projectiles", false));
        this.registerSetting(silentSwing = new TickSetting("Silent swing", false));

    }
    @Subscribe
    public void onPreMotion(PreMotionEvent e) {
        if (!condition() || stopAttack()) {
            return;
        }
        if (fireball != null) {
            final ItemStack getHeldItem = mc.thePlayer.getHeldItem();
            if (getHeldItem != null && getHeldItem.getItem() instanceof ItemBlock && !blocksRotate.isToggled() && Mouse.isButtonDown(1)) {
                return;
            }
            if (getHeldItem != null && (getHeldItem.getItem() instanceof ItemBow || getHeldItem.getItem() instanceof ItemSnowball || getHeldItem.getItem() instanceof ItemEgg || getHeldItem.getItem() instanceof ItemFishingRod) && !projectileRotate.isToggled()) {
                return;
            }
//            if (Raven.moduleManager.getModuleByClazz(Scaffold.class) != null && Scaffold.stopRotation()) {
//                return;
//            } as scaffold doesn't exist so
            float[] rotations = RotationUtils.getRotations(fireball, e.getYaw(), e.getPitch());
            e.setYaw(rotations[0]);
            e.setPitch(rotations[1]);
        }
    }

    @Subscribe
    public void onPreUpdate(UpdateEvent e) {
        if(e.getTiming() == EventTiming.POST)
            return;
        if (!condition() || stopAttack()) {
            return;
        }
        if (fireball != null) {
            if (Raven.moduleManager.getModuleByClazz(KillAura.class) != null && Raven.moduleManager.getModuleByClazz(KillAura.class).isEnabled()) {
                if (KillAura.target != null) {
                    attack = false;
                    return;
                }
                attack = true;
            } else {
                Utils.Player.attackEntity(fireball, !silentSwing.isToggled());
            }
        }
    }

    private EntityFireball getFireball() {
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityFireball)) {
                continue;
            }
            if (!this.fireballs.contains(entity)) {
                continue;
            }
            if (mc.thePlayer.getDistanceSqToEntity(entity) > range.getInput() * range.getInput()) {
                continue;
            }
            final float n = (float) fov.getInput();
            if (n != 360.0f && !Utils.Player.inFov(n, entity)) {
                continue;
            }
            return (EntityFireball) entity;
        }
        return null;
    }

    @Subscribe
    public void onForgeEvent(ForgeEvent fe) {
        if (fe.getEvent() instanceof EntityJoinWorldEvent) {
            EntityJoinWorldEvent e = ((EntityJoinWorldEvent) fe.getEvent());
            if (!Utils.Player.isPlayerInGame()) {
                return;
            }
            if (e.entity == mc.thePlayer) {
                this.fireballs.clear();
            } else if (e.entity instanceof EntityFireball && mc.thePlayer.getDistanceSqToEntity(e.entity) > 16.0) {
                this.fireballs.add(e.entity);
            }
        }
    }

    public void onDisable() {
        this.fireballs.clear();
        this.fireball = null;
        this.attack = false;
    }

    public void onUpdate() {
        if (!condition()) {
            return;
        }
        if (mc.currentScreen != null) {
            attack = false;
            fireball = null;
            return;
        }
        fireball = this.getFireball();
    }

    private boolean stopAttack() {
        return (Raven.moduleManager.getModuleByClazz(BedAura.class) != null && Raven.moduleManager.getModuleByClazz(BedAura.class).isEnabled() && BedAura.m != null) || (Raven.moduleManager.getModuleByClazz(KillAura.class) != null && Raven.moduleManager.getModuleByClazz(KillAura.class).isEnabled() && KillAura.target != null);
    }

    private boolean condition() {
        if (!Utils.Player.isPlayerInGame()) {
            return false;
        }
        if (mc.thePlayer.capabilities.isFlying && disableWhileFlying.isToggled()) {
            return false;
        }
//        if (Raven.moduleManager.getModuleByClazz(Scaffold.class) != null && Raven.moduleManager.getModuleByClazz(Scaffold.class).isEnabled() && disableWhileScaffold.isToggled()) {
//            return false;
//        } as scaffold doesn't exist so
        return true;
    }
}
