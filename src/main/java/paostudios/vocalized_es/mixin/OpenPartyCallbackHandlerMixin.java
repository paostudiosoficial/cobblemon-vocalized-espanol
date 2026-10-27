package paostudios.vocalized_es.mixin;

import com.cobblemon.mod.common.client.net.callback.party.OpenPartyCallbackHandler;
import com.cobblemon.mod.common.net.messages.client.callback.OpenPartyCallbackPacket;
import com.cobblemon.mod.common.net.messages.server.callback.party.PartyPokemonSelectedPacket;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paostudios.vocalized_es.Objetos;

@Mixin(value = OpenPartyCallbackHandler.class, remap = false)
public class OpenPartyCallbackHandlerMixin {
    @Inject(method = "handle(Lcom/cobblemon/mod/common/net/messages/client/callback/OpenPartyCallbackPacket;Lnet/minecraft/client/Minecraft;)V", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$elegir(OpenPartyCallbackPacket packet, Minecraft client, CallbackInfo ci) {
        int i = Objetos.elegir(packet.getPokemon());
        if (i < 0) return;
        new PartyPokemonSelectedPacket(packet.getUuid(), i).sendToServer();
        ci.cancel();
    }
}
