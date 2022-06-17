package me.bubblefish.bubbleclient.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.bubblefish.bubbleclient.BubbleClient.playerDealtReach;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "attackEntity", at = @At(value = "TAIL", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;ClientPlayerInteractionManager()Z"))
    public void ClientPlayerInteractionManager(PlayerEntity player, Entity target, CallbackInfo ci) {
        playerDealtReach = Math.max(0f, Math.min(3f, Math.round((player.distanceTo(target) - target.getWidth() / 2f) * 100f) / 100f));
    }
}
