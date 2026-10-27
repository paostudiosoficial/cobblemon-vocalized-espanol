package paostudios.vocalized_es.mixin;

import com.cobblemon.vocalized.common.client.gui.screen.VocalizedScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paostudios.vocalized_es.Microfono;

@Mixin(value = VocalizedScreen.class, remap = false)
public class VocalizedScreenMixin {
    @Shadow
    private Button toggleButton;

    @Inject(method = "toggleVoiceMode", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$modo(CallbackInfo ci) {
        Microfono.siguiente();
        if (toggleButton != null) toggleButton.setMessage(Microfono.boton());
        ci.cancel();
    }

    @Inject(method = "getButtonText", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$texto(CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(Microfono.boton());
    }
}
