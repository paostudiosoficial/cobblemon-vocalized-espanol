package paostudios.vocalized_es;

import com.cobblemon.vocalized.common.config.ConfigLoader;
import net.neoforged.fml.ModList;
import su.plo.voice.api.client.config.ClientConfig;
import su.plo.voice.client.ModVoiceClient;

public final class Plasmo {
    private static Boolean instalado;

    private Plasmo() {
    }

    public static boolean activo() {
        if (instalado == null) instalado = ModList.get().isLoaded("plasmovoice");
        if (!instalado) return false;
        try {
            return ConfigLoader.INSTANCE.getClientConfig().getUseSimpleVoiceChatMicConfig();
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean muteado() {
        return activo() && Api.muteado();
    }

    public static String microfono() {
        return activo() ? Api.microfono() : null;
    }

    private static final class Api {
        private static ClientConfig.Voice voz() {
            ModVoiceClient c = ModVoiceClient.INSTANCE;
            return c == null ? null : c.getConfig().getVoice();
        }

        static boolean muteado() {
            try {
                ClientConfig.Voice v = voz();
                return v != null && (v.getDisabled().value() || v.getMicrophoneDisabled().value());
            } catch (Throwable e) {
                return false;
            }
        }

        static String microfono() {
            try {
                ClientConfig.Voice v = voz();
                String d = v == null ? null : v.getInputDevice().value();
                return d == null || d.isBlank() ? null : d;
            } catch (Throwable e) {
                return null;
            }
        }
    }
}
