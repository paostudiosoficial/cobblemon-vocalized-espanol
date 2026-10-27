package paostudios.vocalized_es;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.net.messages.server.SendOutPokemonPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.vocalized.common.client.registry.VocalizedKeybinds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public final class Fuera {
    private static final List<String> VOLVER_EN = List.of("recall", "return", "come back", "go back", "get back");
    private static String firma = "";
    private static long hasta;

    private Fuera() {
    }

    public static Boolean activo() {
        Microfono m = Microfono.actual();
        if (CobblemonClient.INSTANCE.getBattle() != null || (m != Microfono.COMBATE_Y_MUNDO && m != Microfono.PULSAR)) return false;
        if (Plasmo.muteado()) return false;
        if (m == Microfono.PULSAR) {
            long ahora = System.currentTimeMillis();
            if (VocalizedKeybinds.INSTANCE.isKeyDown(VocalizedKeybinds.PUSH_TO_TALK)) hasta = ahora + 1500;
            if (ahora > hasta) return false;
        }
        String f = Equipo.firma();
        if (!f.equals(firma)) {
            firma = f;
            return false;
        }
        return !f.isEmpty();
    }

    public static List<String> frases() {
        List<String> out = new ArrayList<>(Equipo.nombres(p -> true));
        out.addAll(Texto.SACAR);
        return out;
    }

    public static boolean manejar(String texto) {
        if (CobblemonClient.INSTANCE.getBattle() != null) return false;
        String t = Texto.normalizar(texto);
        if (t.isEmpty()) return false;
        boolean volver = Texto.contieneAlguna(t, Texto.VOLVER) || Texto.contieneAlguna(t, VOLVER_EN);
        if (volver) {
            Pokemon p = Equipo.buscar(t, Equipo::afuera);
            if (p != null) return enviar(p);
        }
        if (Texto.contieneAlguna(t, Texto.SACAR)) {
            Pokemon p = Equipo.buscar(t, x -> !x.isFainted() && !Equipo.afuera(x));
            if (p != null) return enviar(p);
        }
        return !volver;
    }

    private static boolean enviar(Pokemon p) {
        int slot = Equipo.slot(p);
        if (slot < 0) return false;
        Minecraft.getInstance().execute(() -> new SendOutPokemonPacket(slot).sendToServer());
        return true;
    }
}
