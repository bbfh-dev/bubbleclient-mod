package me.bubblefish.bubbleclient.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static me.bubblefish.bubbleclient.config.Config.getConfigMapValue;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"
            )
    )
    private boolean alwaysPressed(KeyBinding sprintKey) {
        return (getConfigMapValue((byte) 5))? true : sprintKey.isPressed();
    }
}
