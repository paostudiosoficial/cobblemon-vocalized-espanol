package paostudios.vocalized_es;

import com.cobblemon.mod.common.api.callback.PartySelectPokemonDTO;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.item.battle.BagItemLike;
import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class Objetos {
    private static volatile UUID objetivo;
    private static volatile long hasta;

    private Objetos() {
    }

    private static boolean sirve(ItemStack s) {
        if (s.isEmpty()) return false;
        Item i = s.getItem();
        return i instanceof PokemonSelectingItem || i instanceof BagItemLike;
    }

    private static Inventory inventario() {
        LocalPlayer p = Minecraft.getInstance().player;
        return p == null ? null : p.getInventory();
    }

    public static List<String> nombres() {
        List<String> out = new ArrayList<>();
        Inventory inv = inventario();
        if (inv == null) return out;
        for (int i = 0; i < 36; i++) {
            ItemStack s = inv.getItem(i);
            if (sirve(s)) {
                String n = s.getHoverName().getString();
                if (!out.contains(n)) out.add(n);
            }
        }
        return out;
    }

    public static int buscar(String texto) {
        Inventory inv = inventario();
        if (inv == null) return -1;
        int mejor = -1;
        double puntos = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack s = inv.getItem(i);
            if (!sirve(s)) continue;
            String n = s.getHoverName().getString();
            double v = Fonetica.puntaje(texto, n) + n.length() / 1000.0;
            if (v > puntos && v >= Fonetica.MINIMO) {
                mejor = i;
                puntos = v;
            }
        }
        return mejor;
    }

    public static void usar(int slot, UUID destino) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null || mc.gameMode == null) return;
        Inventory inv = p.getInventory();
        if (slot < 9) inv.selected = slot;
        else mc.gameMode.handleInventoryMouseClick(p.inventoryMenu.containerId, slot, inv.selected, ClickType.SWAP, p);
        objetivo = destino;
        hasta = System.currentTimeMillis() + 4000;
        mc.gameMode.useItem(p, InteractionHand.MAIN_HAND);
    }

    public static int elegir(List<PartySelectPokemonDTO> opciones) {
        UUID u = objetivo;
        if (u == null || System.currentTimeMillis() > hasta) return -1;
        objetivo = null;
        List<Pokemon> equipo = Equipo.lista();
        if (equipo.size() != opciones.size()) return -1;
        for (int i = 0; i < equipo.size(); i++) {
            if (equipo.get(i).getUuid().equals(u)) return opciones.get(i).getEnabled() ? i : -1;
        }
        return -1;
    }
}
