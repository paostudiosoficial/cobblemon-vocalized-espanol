package paostudios.vocalized_es;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Texto {
    private static final Pattern MARCAS = Pattern.compile("\\p{M}+");
    private static final Pattern NO_ALFANUM = Pattern.compile("[^a-z0-9]+");
    private static final Charset CP1252 = Charset.forName("windows-1252");

    public static final List<String> RENDIRSE = List.of(
            "me rindo", "me doy por vencido", "me doy por vencida", "abandono", "me retiro");

    public static final List<String> VOLVER = List.of(
            "vuelve", "volve", "regresa", "retirate", "ven aca", "ven aqui", "a tu pokeball");

    public static final List<String> SACAR = List.of(
            "yo te elijo", "te elijo", "adelante", "sal", "ve");

    public static final List<String> CAMBIAR = List.of(
            "yo te elijo", "te elijo", "adelante", "cambio a", "cambia a", "cambiar a", "cambio", "ve");

    public static final List<String> HUIR = List.of(
            "huir", "huye", "huyamos", "escapar", "escapa", "escapemos", "corre");

    public static final List<String> MEGA = List.of("megaevoluciona", "megaevolucion", "megaevolucionar", "mega");

    public static final List<String> ULTRA = List.of("ultraexplosion", "ultra explosion", "ultra");

    public static final List<String> ZETA = List.of("movimiento z", "poder z", "zeta");

    public static final List<String> DINAMAX = List.of("dinamax", "dynamax", "gigamax", "gigantamax", "giga");

    public static final List<String> TERA = List.of("teracristaliza", "teracristalizacion", "teracristal", "tera");

    public static final String SIN_MATCH = "⁣";

    public static volatile String ultimo = "";

    private Texto() {
    }

    public static String limpiar(String s) {
        if (s == null || s.isEmpty()) return s;
        return MARCAS.matcher(Normalizer.normalize(reparar(s), Normalizer.Form.NFD)).replaceAll("");
    }

    public static String normalizar(String s) {
        if (s == null) return "";
        return NO_ALFANUM.matcher(limpiar(s).toLowerCase(Locale.ROOT)).replaceAll(" ").trim();
    }

    public static boolean contiene(String texto, String frase) {
        String f = normalizar(frase);
        return !f.isEmpty() && (" " + texto + " ").contains(" " + f + " ");
    }

    public static boolean contieneAlguna(String texto, Collection<String> frases) {
        for (String f : frases) if (contiene(texto, f)) return true;
        return false;
    }

    private static String reparar(String s) {
        if (s.indexOf('Ã') < 0 && s.indexOf('Â') < 0) return s;
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(s.getBytes(CP1252)))
                    .toString();
        } catch (CharacterCodingException e) {
            return s;
        }
    }

    public static List<String> sumar(List<String> base, Collection<String> extra) {
        List<String> out = new ArrayList<>(base);
        for (String e : extra) if (e != null && !e.isBlank() && !out.contains(e)) out.add(e);
        return out;
    }
}
