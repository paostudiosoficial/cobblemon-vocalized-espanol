package paostudios.vocalized_es;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(VocalizedES.MODID)
public class VocalizedES {
    public static final String MODID = "vocalized_es";
    private static final Logger LOG = LogUtils.getLogger();

    public VocalizedES() {
        if (FMLEnvironment.dist == Dist.CLIENT) cargarVosk();
    }

    private static void cargarVosk() {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        ClassLoader cl = VocalizedES.class.getClassLoader();
        try {
            t.setContextClassLoader(cl);
            Class.forName("com.cobblemon.vocalized.common.vosk.LibVosk", true, cl);
            LOG.info("[VocalizedES] Vosk cargado");
        } catch (Throwable e) {
            LOG.error("[VocalizedES] No se pudo cargar Vosk", e);
        } finally {
            t.setContextClassLoader(prev);
        }
    }
}
