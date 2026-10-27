package paostudios.vocalized_es;

import net.minecraft.client.Minecraft;

public final class Idioma {
    private Idioma() {
    }

    public static String modelo(String original) {
        try {
            String lang = Minecraft.getInstance().getLanguageManager().getSelected();
            if (lang != null && lang.startsWith("es")) return "models/es_es";
        } catch (Throwable ignored) {
        }
        return original;
    }
}
