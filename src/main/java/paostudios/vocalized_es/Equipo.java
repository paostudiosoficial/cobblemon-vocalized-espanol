package paostudios.vocalized_es;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.activestate.SentOutState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Equipo {
    private Equipo() {
    }

    public static List<Pokemon> lista() {
        List<Pokemon> out = new ArrayList<>();
        for (Pokemon p : CobblemonClient.INSTANCE.getStorage().getParty().getSlots()) if (p != null) out.add(p);
        return out;
    }

    public static int slot(Pokemon p) {
        return CobblemonClient.INSTANCE.getStorage().getParty().getSlots().indexOf(p);
    }

    public static List<String> nombres(Pokemon p) {
        List<String> out = new ArrayList<>(2);
        out.add(p.getDisplayName(false).getString());
        String especie = p.getSpecies().getTranslatedName().getString();
        if (!out.contains(especie)) out.add(especie);
        return out;
    }

    public static List<String> nombres(Predicate<Pokemon> filtro) {
        List<String> out = new ArrayList<>();
        for (Pokemon p : lista()) if (filtro.test(p)) out.addAll(nombres(p));
        return out;
    }

    public static Pokemon buscar(String texto, Predicate<Pokemon> filtro) {
        Pokemon mejor = null;
        double puntos = 0;
        for (Pokemon p : lista()) {
            if (!filtro.test(p)) continue;
            for (String n : nombres(p)) {
                double v = Fonetica.puntaje(texto, n) + n.length() / 1000.0;
                if (v > puntos && v >= Fonetica.MINIMO) {
                    mejor = p;
                    puntos = v;
                }
            }
        }
        return mejor;
    }

    public static boolean afuera(Pokemon p) {
        return p.getState() instanceof SentOutState;
    }

    public static String firma() {
        StringBuilder sb = new StringBuilder();
        for (Pokemon p : lista()) sb.append(p.getUuid()).append(p.getDisplayName(false).getString()).append(';');
        return sb.toString();
    }
}
