package paostudios.vocalized_es;

import com.cobblemon.vocalized.common.config.ConfigLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class Debug {
    private Debug() {
    }

    public static void mostrar(String texto) {
        if (texto == null || texto.isBlank()) return;
        try {
            if (!ConfigLoader.INSTANCE.getClientConfig().getDebug()) return;
        } catch (Throwable e) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> mc.gui.setOverlayMessage(Component.literal("» " + texto), false));
    }
}
