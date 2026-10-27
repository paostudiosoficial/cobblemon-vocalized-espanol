package paostudios.vocalized_es;

import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.battles.FleeAttemptActionResponse;
import com.cobblemon.mod.common.battles.ForfeitActionResponse;
import com.cobblemon.mod.common.battles.InBattleMove;
import com.cobblemon.mod.common.battles.MoveActionResponse;
import com.cobblemon.mod.common.battles.ShowdownMoveset;
import com.cobblemon.mod.common.battles.ShowdownActionResponse;
import com.cobblemon.mod.common.battles.SwitchActionResponse;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattle;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattleSide;
import com.cobblemon.mod.common.client.battle.SingleActionRequest;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.vocalized.common.client.controller.battle.BattleVoiceController;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;

public final class Combate {
    private static volatile ShowdownMoveset.Gimmick gimmickPendiente;
    private static volatile SingleActionRequest pedidoPendiente;

    private Combate() {
    }

    public static List<String> frases(boolean forzado) {
        List<String> out = new ArrayList<>();
        ClientBattle b = CobblemonClient.INSTANCE.getBattle();
        if (b == null) return out;
        out.addAll(Equipo.nombres(cambiable(b)));
        out.addAll(Texto.CAMBIAR);
        if (forzado) return out;
        out.addAll(Texto.HUIR);
        out.addAll(Objetos.nombres());
        return out;
    }

    public static boolean manejar(String texto) {
        ClientBattle b = CobblemonClient.INSTANCE.getBattle();
        if (b == null) return false;
        SingleActionRequest r = b.getFirstUnansweredRequest();
        if (r == null || r.getResponse() != null) return false;
        String t = Texto.normalizar(texto);
        if (t.isEmpty()) return false;
        boolean forzado = r.getMoveSet() == null;

        if (!forzado && Texto.contieneAlguna(t, Texto.RENDIRSE)) {
            responder(b, r, new ForfeitActionResponse());
            return true;
        }

        if (!forzado && Texto.contieneAlguna(t, Texto.HUIR) && salvaje(b)) {
            responder(b, r, new FleeAttemptActionResponse());
            return true;
        }

        if (!forzado) {
            int slot = Objetos.buscar(t);
            if (slot >= 0) {
                Pokemon p = Equipo.buscar(t, x -> true);
                UUID destino = p != null ? p.getUuid() : activo(r);
                if (destino == null) return false;
                Minecraft.getInstance().execute(() -> Objetos.usar(slot, destino));
                return true;
            }
        }

        String mov = forzado ? null : movimiento(t, r);
        Pokemon p = Equipo.buscar(t, cambiable(b));
        if (p != null && (forzado || mov == null || Texto.contieneAlguna(t, Texto.CAMBIAR))) {
            responder(b, r, new SwitchActionResponse(p.getUuid()));
            return true;
        }

        ShowdownMoveset.Gimmick g = forzado ? null : gimmick(t, r.getMoveSet());
        if (mov == null && g != null) {
            gimmickPendiente = g;
            pedidoPendiente = r;
            return true;
        }

        if (mov != null) {
            if (g == null && pedidoPendiente == r) g = gimmickPendiente;
            gimmickPendiente = null;
            pedidoPendiente = null;
            ActiveClientBattlePokemon obj = objetivo(t, b, r);
            String pnx = obj == null ? null : obj.getPNX();
            if (g == null) {
                Minecraft.getInstance().execute(() -> BattleVoiceController.selectMove(mov, pnx));
            } else {
                String id = g.getId();
                Minecraft.getInstance().execute(() -> {
                    if (r.getResponse() != null) return;
                    String destino = pnx != null ? pnx : BattleVoiceController.resolveTarget(mov, r, b);
                    r.setResponse(new MoveActionResponse(mov, destino, id));
                    b.checkForFinishedChoosing();
                });
            }
            return true;
        }
        return false;
    }

    private static ShowdownMoveset.Gimmick gimmick(String t, ShowdownMoveset ms) {
        if (ms == null) return null;
        for (ShowdownMoveset.Gimmick g : ms.getGimmicks()) {
            List<String> palabras = switch (g) {
                case MEGA_EVOLUTION -> Texto.MEGA;
                case ULTRA_BURST -> Texto.ULTRA;
                case Z_POWER -> Texto.ZETA;
                case DYNAMAX -> Texto.DINAMAX;
                case TERASTALLIZATION -> Texto.TERA;
            };
            for (String p : palabras) {
                boolean ok = p.length() < 7 ? Texto.contiene(t, p) : Fonetica.puntaje(t, p) >= Fonetica.MINIMO;
                if (ok) return g;
            }
        }
        return null;
    }

    private static void responder(ClientBattle b, SingleActionRequest r, ShowdownActionResponse resp) {
        Minecraft.getInstance().execute(() -> {
            if (r.getResponse() == null) {
                r.setResponse(resp);
                b.checkForFinishedChoosing();
            }
        });
    }

    private static UUID activo(SingleActionRequest r) {
        ActiveClientBattlePokemon a = r.getActivePokemon();
        ClientBattlePokemon bp = a == null ? null : a.getBattlePokemon();
        return bp == null ? null : bp.getUuid();
    }

    private static boolean salvaje(ClientBattle b) {
        for (ClientBattleSide s : b.getSides())
            for (ClientBattleActor a : s.getActors())
                if (a.getType() == ActorType.WILD) return true;
        return false;
    }

    private static Set<UUID> activos(ClientBattle b) {
        Set<UUID> out = new HashSet<>();
        for (ClientBattleSide s : b.getSides())
            for (ActiveClientBattlePokemon a : s.getActiveClientBattlePokemon()) {
                ClientBattlePokemon bp = a.getBattlePokemon();
                if (bp != null) out.add(bp.getUuid());
            }
        return out;
    }

    private static Predicate<Pokemon> cambiable(ClientBattle b) {
        Set<UUID> act = activos(b);
        return p -> !p.isFainted() && !act.contains(p.getUuid());
    }

    private static String movimiento(String t, SingleActionRequest r) {
        if (r.getMoveSet() == null) return null;
        String mejor = null;
        double puntos = 0;
        for (InBattleMove m : r.getMoveSet().getMoves()) {
            String n = Moves.getByNameOrDummy(m.getId()).getDisplayName().getString();
            double v = Fonetica.puntaje(t, n) + n.length() / 1000.0;
            if (v > puntos && v >= Fonetica.MINIMO) {
                mejor = m.getId();
                puntos = v;
            }
        }
        return mejor;
    }

    private static ActiveClientBattlePokemon objetivo(String t, ClientBattle b, SingleActionRequest r) {
        ActiveClientBattlePokemon mejor = null;
        double puntos = 0;
        for (ClientBattleSide s : b.getSides())
            for (ActiveClientBattlePokemon a : s.getActiveClientBattlePokemon()) {
                ClientBattlePokemon bp = a.getBattlePokemon();
                if (bp == null || a == r.getActivePokemon()) continue;
                for (String n : new String[]{bp.getDisplayName().getString(), bp.getSpecies().getTranslatedName().getString()}) {
                    double v = Fonetica.puntaje(t, n) + n.length() / 1000.0;
                    if (v > puntos && v >= Fonetica.MINIMO) {
                        mejor = a;
                        puntos = v;
                    }
                }
            }
        return mejor;
    }
}
