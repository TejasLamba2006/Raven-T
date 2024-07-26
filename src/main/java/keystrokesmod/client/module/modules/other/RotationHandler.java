package keystrokesmod.client.module.modules.other;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.impl.MoveInputEvent;
import keystrokesmod.client.event.impl.RotationEvent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.AimSimulator;
import keystrokesmod.client.utils.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.Nullable;
import keystrokesmod.client.utils.MoveUtil;

public final class RotationHandler extends Module {
    private static @Nullable Float movementYaw = null;
    private static @Nullable Float rotationYaw = null;
    private static @Nullable Float rotationPitch = null;
    private boolean isSet = false;
    private static MoveFix moveFix = MoveFix.NONE;
    public enum smoothBackMode {
        NONE,
        DEFAULT
    }
    private static final ComboSetting defaultMoveFix = new ComboSetting("Default MoveFix", MoveFix.NONE);
    private final ComboSetting smoothBack = new ComboSetting("Smooth back", smoothBackMode.NONE);
    private final SliderSetting aimSpeed = new SliderSetting("Aim speed", 5, 1, 15, 0.1);
    public static final TickSetting rotateBody = new TickSetting("Rotate body", true);
    public static final TickSetting fullBody = new TickSetting("Full body", false);
    public static final SliderSetting randomYawFactor = new SliderSetting("Random yaw factor", 1.0, 0.0, 10.0, 1.0);

    public RotationHandler() {
        super("RotationHandler", ModuleCategory.other);
        this.registerSettings(defaultMoveFix, smoothBack, aimSpeed);
        this.registerSetting(new DescriptionSetting("Classic"));
        this.registerSettings(rotateBody, fullBody, randomYawFactor);
        this.canBeEnabled = false;
    }

    public static float getMovementYaw(Entity entity) {
        if (entity instanceof EntityPlayerSP && movementYaw != null)
            return movementYaw;
        return entity.rotationYaw;
    }

    public static void setMovementYaw(float movementYaw) {
        RotationHandler.movementYaw = movementYaw;
    }

    public static void setRotationYaw(float rotationYaw) {
        if (AimSimulator.yawEquals(rotationYaw, mc.thePlayer.rotationYaw)) {
            RotationHandler.rotationYaw = null;
            return;
        }
        RotationHandler.rotationYaw = rotationYaw;
    }

    public static void setRotationPitch(float rotationPitch) {
        if (rotationPitch == mc.thePlayer.rotationPitch) {
            RotationHandler.rotationPitch = null;
            return;
        }
        RotationHandler.rotationPitch = rotationPitch;
    }

    public static void setMoveFix(MoveFix moveFix) {
        RotationHandler.moveFix = moveFix;
    }

    public static MoveFix getMoveFix() {
        if (moveFix != null)
            return moveFix;
        return (MoveFix) defaultMoveFix.getMode();
    }

    public static float getRotationYaw() {
        if (rotationYaw != null)
            return RotationUtils.normalize(rotationYaw);
        return RotationUtils.normalize(mc.thePlayer.rotationYaw);
    }

    public static float getRotationPitch() {
        if (rotationPitch != null)
            return rotationPitch;
        return mc.thePlayer.rotationPitch;
    }

    /**
     * Fix movement
     * @param event before update living entity (move)
     */
    @Subscribe
    public void onPreMotion(MoveInputEvent event) {
        if (isSet) {
            float viewYaw = RotationUtils.normalize(mc.thePlayer.rotationYaw);
            float viewPitch = RotationUtils.normalize(mc.thePlayer.rotationPitch);
            switch ((smoothBackMode) smoothBack.getMode()) {
                case NONE:
                    rotationYaw = null;
                    rotationPitch = null;
                    break;
                case DEFAULT:
                    setRotationYaw(AimSimulator.rotMove(viewYaw, getRotationYaw(), (float) aimSpeed.getInput()));
                    setRotationPitch(AimSimulator.rotMove(viewPitch, getRotationPitch(), (float) aimSpeed.getInput()));
                    break;
            }
        }

        if (AimSimulator.yawEquals(getRotationYaw(), mc.thePlayer.rotationYaw)) rotationYaw = null;
        if (getRotationPitch() == mc.thePlayer.rotationPitch) rotationPitch = null;

        RotationEvent rotationEvent = new RotationEvent(getRotationYaw(), getRotationPitch(), (MoveFix) defaultMoveFix.getMode());
        Raven.eventBus.post(rotationEvent);
        isSet = rotationEvent.isSet() || rotationYaw != null || rotationPitch != null;
        if (isSet) {
            rotationYaw = rotationEvent.getYaw();
            rotationPitch = rotationEvent.getPitch();
            moveFix = rotationEvent.getMoveFix();

            switch (moveFix) {
                case NONE:
                    movementYaw = null;
                    break;
                case SILENT:
                    movementYaw = getRotationYaw();

                    final float forward = event.getForward();
                    final float strafe = event.getStrafe();

                    final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(mc.thePlayer.rotationYaw, forward, strafe)));

                    if (forward == 0 && strafe == 0) {
                        return;
                    }

                    float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

                    for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
                        for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                            if (predictedStrafe == 0 && predictedForward == 0) continue;

                            final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(movementYaw, predictedForward, predictedStrafe)));
                            final double difference = Math.abs(angle - predictedAngle);

                            if (difference < closestDifference) {
                                closestDifference = (float) difference;
                                closestForward = predictedForward;
                                closestStrafe = predictedStrafe;
                            }
                        }
                    }

                    event.setForward(closestForward);
                    event.setStrafe(closestStrafe);
                    break;
                case STRICT:
                    movementYaw = getRotationYaw();
                    break;
            }
        } else {
            movementYaw = null;
            moveFix = null;
        }
    }

    public enum MoveFix {
        NONE,
        SILENT,
        STRICT;
    }
}