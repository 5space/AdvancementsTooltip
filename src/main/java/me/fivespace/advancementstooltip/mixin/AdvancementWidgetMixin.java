package me.fivespace.advancementstooltip.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {

    @Shadow private AdvancementProgress field_2714;
    @Shadow @Final private AdvancementDisplay display;
    @Shadow @Final private Advancement advancement;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private String field_2713;

    @Shadow @Final @Mutable private List<String> field_2705;

    @Shadow protected abstract List<String> method_2330(String string_1, int int_1);

    private int scrollStart = 0;

    private void setDescription(String text) {
        int int_1 = this.advancement.getRequirementCount();
        int int_2 = String.valueOf(int_1).length();
        int int_3 = int_1 > 1 ? this.client.textRenderer.getStringWidth("  ") + this.client.textRenderer.getStringWidth("0") * int_2 * 2 + this.client.textRenderer.getStringWidth("/") : 0;
        int int_4 = 29 + this.client.textRenderer.getStringWidth(this.field_2713) + int_3;
        this.field_2705 = this.method_2330(text, int_4);
    }

    private void resetDescription() {
        String desc = this.display.getDescription().asFormattedString();
        this.setDescription(desc);
    }

//    private void updateDescription() {
//        if (this.field_2714 == null) {
//            return;
//        }
//
//        Iterable<String> criteria = this.field_2714.getUnobtainedCriteria();
//        List<String> entries = new ArrayList<>();
//        criteria.forEach(entries::add);
//
//        boolean showInfo = Screen.hasShiftDown();
//
//        if (showInfo) {
//            if (Screen.hasAltDown()) {
//                if (this.scrollStart > 0) {
//                    this.scrollStart -= 1;
//                }
//            } else if (Screen.hasControlDown() && this.scrollStart + 10 < entries.size()) {
//                this.scrollStart += 1;
//            }
//        }
//
//        String description;
//        if (entries.size() == 0) {
//            description = "§2None";
//        } else {
//            Collections.sort(entries);
//            int start = Math.max(0, Math.min(entries.size(), scrollStart));
//            int end = Math.max(0, Math.min(entries.size(), scrollStart + 10));
//            description = "§2Criteria (" + entries.size() + "):\n§a" + String.join("\n", entries.subList(start, end));
//        }
//
//        if (showInfo) {
//            this.setDescription(description);
//        } else {
//            this.resetDescription();
//        }
//    }

    private void updateDescription() {
        if (this.field_2714 == null) {
            return;
        }

        Iterable<String> criteria = this.field_2714.getUnobtainedCriteria();
        List<String> entries = new ArrayList<>();
        criteria.forEach(entries::add);

        boolean showInfo = Screen.hasShiftDown();

        if (showInfo) {
            if (Screen.hasAltDown()) {
                if (this.scrollStart > 0) {
                    this.scrollStart -= 1;
                }
            } else if (Screen.hasControlDown() && this.scrollStart + 10 < entries.size()) {
                this.scrollStart += 1;
            }
        }

        String description;
        if (entries.size() == 0) {
            description = "§2None";
        } else {
            Collections.sort(entries);
            int start = Math.max(0, Math.min(entries.size(), scrollStart));
            int end = Math.max(0, Math.min(entries.size(), scrollStart + 10));
            description = "§2Criteria (" + entries.size() + "):\n§a" + String.join("\n", entries.subList(start, end));
        }

        if (showInfo) {
            this.setDescription(description);
        } else {
            this.resetDescription();
        }
    }
    @Inject(method = "method_2331", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 1))
    private void tooltipInjector(int int_1, int int_2, float float_1, int int_3, int int_4, CallbackInfo ci) {
        this.updateDescription();
    }
}