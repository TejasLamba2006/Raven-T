package keystrokesmod.client.mixin.mixins;

import keystrokesmod.client.event.impl.MoveInputEvent;
import keystrokesmod.client.main.Raven;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MovementInputFromOptions.class)
public abstract class MixinMovementInputFromOptions extends MovementInput {

    @Shadow @Final private GameSettings gameSettings;

    /**
     * @author xia__mc
     * @reason to fix movement
     */
    @Overwrite
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        final MoveInputEvent moveInputEvent = new MoveInputEvent(moveForward, moveStrafe, jump, sneak, 0.3D);

        Raven.eventBus.post(moveInputEvent);

        final double sneakMultiplier = moveInputEvent.getSneakSlowDownMultiplier();
        this.moveForward = moveInputEvent.getForward();
        this.moveStrafe = moveInputEvent.getStrafe();
        this.jump = moveInputEvent.isJump();
        this.sneak = moveInputEvent.isSneak();

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * sneakMultiplier);
            this.moveForward = (float) ((double) this.moveForward * sneakMultiplier);
        }

        //Raven.eventBus.post(new PostPlayerInputEvent());
    }
}