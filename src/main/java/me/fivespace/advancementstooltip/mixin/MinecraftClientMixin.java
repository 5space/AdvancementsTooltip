package me.fivespace.advancementstooltip.mixin;

import me.fivespace.advancementstooltip.gui.DetailedAdvancementsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow public ClientPlayerEntity player;

    @Inject(method = "openScreen", at = @At("HEAD"))
    private void dummyGenerateRefmap(Screen screen, CallbackInfo ci) {
        // NO-OP this injection is only here to generate the refmap
    }

    @ModifyVariable(method = "openScreen", at = @At("HEAD"), argsOnly = true)
    private Screen openScreen(Screen screen) {
        if (screen != null && AdvancementsScreen.class == screen.getClass())
            return new DetailedAdvancementsScreen(player.networkHandler.getAdvancementHandler());
        return screen;
    }
}