package keystrokesmod.client.module.modules.render;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.impl.ForgeEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.world.AntiBot;
import keystrokesmod.client.module.setting.impl.TickSetting;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;

public class Chams extends Module {
    private TickSetting ignoreBots;
    private HashSet<Entity> bots = new HashSet<>();
    public Chams() {
        super("Chams", ModuleCategory.render);
        this.registerSetting(ignoreBots = new TickSetting("Ignore bots", false));
    }

    @Subscribe
    public void onForgeEvent(ForgeEvent fe) {
        if (fe.getEvent() instanceof Pre) {
            Pre e = (Pre) fe.getEvent();
                if (e.entity == mc.thePlayer) {
                    return;
                }
                if (ignoreBots.isToggled()) {
                    if (AntiBot.bot(e.entity)) {
                        return;
                    }
                    this.bots.add(e.entity);
                }
                GL11.glEnable(32823);
                GL11.glPolygonOffset(1.0f, -1000000.0f);
        } else if (fe.getEvent() instanceof Post) {
            Post e = (Post) fe.getEvent();
            if (ignoreBots.isToggled()) {
                if (!this.bots.contains(e.entity)) {
                    return;
                }
                this.bots.remove(e.entity);
            }
            GL11.glPolygonOffset(1.0f, 1000000.0f);
            GL11.glDisable(32823);
        }
    }
    public void onDisable() {
        this.bots.clear();
    }
}
