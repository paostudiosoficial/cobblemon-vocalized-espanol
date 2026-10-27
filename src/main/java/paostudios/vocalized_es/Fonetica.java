package paostudios.vocalized_es;

public final class Fonetica {
    public static final double MINIMO = 0.74;

    private Fonetica() {
    }

    public static String clave(String s) {
        String t = Texto.normalizar(s).replace(" ", "");
        StringBuilder o = new StringBuilder(t.length());
        int n = t.length();
        for (int i = 0; i < n; i++) {
            char c = t.charAt(i);
            char sig = i + 1 < n ? t.charAt(i + 1) : 0;
            switch (c) {
                case 'c' -> {
                    if (sig == 'h') { o.append('X'); i++; }
                    else if (sig == 'e' || sig == 'i') o.append('s');
                    else o.append('k');
                }
                case 's' -> {
                    if (sig == 'h') { o.append('X'); i++; }
                    else o.append('s');
                }
                case 'q' -> {
                    o.append('k');
                    if (sig == 'u') i++;
                }
                case 'g' -> {
                    if (sig == 'e' || sig == 'i') o.append('i');
                    else o.append('g');
                }
                case 'p' -> {
                    if (sig == 'h') { o.append('f'); i++; }
                    else o.append('p');
                }
                case 'l' -> {
                    if (sig == 'l') { o.append('i'); i++; }
                    else o.append('l');
                }
                case 'z' -> o.append('s');
                case 'v' -> o.append('b');
                case 'w' -> o.append('u');
                case 'y' -> o.append('i');
                case 'h' -> { }
                case 'x' -> o.append("ks");
                case 'n' -> o.append(sig == 'b' || sig == 'p' ? 'm' : 'n');
                default -> o.append(c);
            }
        }
        StringBuilder r = new StringBuilder(o.length());
        for (int i = 0; i < o.length(); i++) {
            char c = o.charAt(i);
            if (r.length() == 0 || r.charAt(r.length() - 1) != c) r.append(c);
        }
        return r.toString();
    }

    public static double puntaje(String texto, String nombre) {
        if (Texto.contiene(texto, nombre)) return 1.0;
        String objetivo = clave(nombre);
        if (objetivo.length() < 4 || texto.isEmpty()) return 0;
        String[] p = texto.split(" ");
        double mejor = 0;
        for (int i = 0; i < p.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < Math.min(p.length, i + 4); j++) {
                sb.append(p[j]);
                String k = clave(sb.toString());
                if (k.length() > objetivo.length() * 2) break;
                double v = parecido(k, objetivo);
                if (v > mejor) mejor = v;
            }
        }
        return mejor >= MINIMO ? mejor : 0;
    }

    private static final String[] GRUPOS = {"aeiou", "kgXs", "bpfm", "dt", "mn", "rld"};

    private static double parecido(String a, String b) {
        int max = Math.max(a.length(), b.length());
        if (max == 0) return 0;
        return 1.0 - distancia(a, b) / max;
    }

    private static boolean vocal(char c) {
        return "aeiou".indexOf(c) >= 0;
    }

    private static double borrar(char c) {
        return vocal(c) ? 0.5 : 1.0;
    }

    private static double cambiar(char x, char y) {
        if (x == y) return 0;
        for (String g : GRUPOS) if (g.indexOf(x) >= 0 && g.indexOf(y) >= 0) return 0.4;
        return 1.0;
    }

    private static double distancia(String a, String b) {
        double[] prev = new double[b.length() + 1];
        double[] cur = new double[b.length() + 1];
        for (int j = 1; j <= b.length(); j++) prev[j] = prev[j - 1] + borrar(b.charAt(j - 1));
        for (int i = 1; i <= a.length(); i++) {
            char ca = a.charAt(i - 1);
            cur[0] = prev[0] + borrar(ca);
            for (int j = 1; j <= b.length(); j++) {
                char cb = b.charAt(j - 1);
                cur[j] = Math.min(Math.min(cur[j - 1] + borrar(cb), prev[j] + borrar(ca)), prev[j - 1] + cambiar(ca, cb));
            }
            double[] t = prev;
            prev = cur;
            cur = t;
        }
        return prev[b.length()];
    }
}
