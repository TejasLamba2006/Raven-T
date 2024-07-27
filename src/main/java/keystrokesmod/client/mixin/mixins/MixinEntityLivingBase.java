package keystrokesmod.client.mixin.mixins;

import com.google.common.collect.Maps;
import keystrokesmod.client.utils.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import keystrokesmod.client.module.modules.other.RotationHandler;

import java.util.Map;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {
    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Unique
    private final Map<Integer, PotionEffect> raven_T$activePotionsMap = Maps.newHashMap();

    @Shadow
    public PotionEffect getActivePotionEffect(Potion potionIn) {
        return this.raven_T$activePotionsMap.get(Integer.valueOf(potionIn.id));
    }

    @Shadow
    public boolean isPotionActive(Potion potionIn) {
        return this.raven_T$activePotionsMap.containsKey(Integer.valueOf(potionIn.id));
    }

    @Shadow
    public float rotationYawHead;

    @Shadow
    public float renderYawOffset;

    @Shadow
    public float swingProgress;

    /**
     * @author strangerrs
     * @reason mixin func110146f
     */
    @Overwrite
    protected float func_110146_f(float p_1101461, float p_1101462) {
        float rotationYaw = this.rotationYaw;
        if (RotationHandler.fullBody != null && RotationHandler.rotateBody != null && !RotationHandler.fullBody.isToggled() && RotationHandler.rotateBody.isToggled() && (EntityLivingBase) (Object) this instanceof EntityPlayerSP) {
            if (this.swingProgress > 0F) {
                p_1101461 = RotationUtils.renderYaw;
            }
            rotationYaw = RotationUtils.renderYaw;
            rotationYawHead = RotationUtils.renderYaw;
        }
        float f = MathHelper.wrapAngleTo180_float(p_1101461 - this.renderYawOffset);
        this.renderYawOffset += f * 0.3F;
        float f1 = MathHelper.wrapAngleTo180_float(rotationYaw - this.renderYawOffset);
        boolean flag = f1 < 90.0F || f1 >= 90.0F;

        if (f1 < -75.0F) {
            f1 = -75.0F;
        }

        if (f1 >= 75.0F) {
            f1 = 75.0F;
        }

        this.renderYawOffset = rotationYaw - f1;

        if (f1 * f1 > 2500.0F) {
            this.renderYawOffset += f1 * 0.2F;
        }

        if (flag) {
            p_1101462 *= -1.0F;
        }

        return p_1101462;
    }

    @Shadow
    protected float getJumpUpwardsMotion() {
        return 0.42F;
    }
    }