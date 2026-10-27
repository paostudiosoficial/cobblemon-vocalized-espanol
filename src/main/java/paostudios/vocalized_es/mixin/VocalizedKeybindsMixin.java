package paostudios.vocalized_es.mixin;

import com.cobblemon.vocalized.common.client.registry.VocalizedKeybinds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VocalizedKeybinds.class, remap = false)
public class VocalizedKeybindsMixin {
    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$tecla(KeyMapping tecla, CallbackInfoReturnable<Boolean> cir) {
        InputConstants.Key k = tecla.getKey();
        cir.setReturnValue(k.getType() == InputConstants.Type.KEYSYM && k.getValue() != -1
                && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), k.getValue()));
    }
}
