package me.fivespace.advancementstooltip.mixin;

import me.fivespace.advancementstooltip.IAdvancementWidgetMixin;
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
public abstract class AdvancementWidgetMixin implements IAdvancementWidgetMixin {

    @Shadow private AdvancementProgress field_2714;
    @Shadow @Final private AdvancementDisplay display;
    @Shadow @Final private Advancement advancement;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private String field_2713;

    @Shadow @Final @Mutable private List<String> field_2705;

    @Shadow protected abstract List<String> method_2330(String string_1, int int_1);

    private double scroll = 0;

    public void changeScroll(double amt) {
        Iterable<String> criteria = this.field_2714.getUnobtainedCriteria();
        List<String> entries = new ArrayList<>();
        criteria.forEach(entries::add);
        Collections.sort(entries);

        int count = 0;
        for (String entry : entries) {
            count += this.wrapDescription("§a" + entry).size();
        }

        this.scroll += amt;
        this.scroll = Math.max(0, Math.min(count - 9, scroll));
    }

    private List<String> wrapDescription(String text) {
        int int_1 = this.advancement.getRequirementCount();
        int int_2 = String.valueOf(int_1).length();
        int int_3 = int_1 > 1 ? this.client.textRenderer.getStringWidth("  ") + this.client.textRenderer.getStringWidth("0") * int_2 * 2 + this.client.textRenderer.getStringWidth("/") : 0;
        int int_4 = 29 + this.client.textRenderer.getStringWidth(this.field_2713) + int_3;
        return this.method_2330(text, int_4);
    }

    private List<String> getDescriptionList() {

        if (Screen.hasShiftDown()) {
            Iterable<String> criteria = this.field_2714.getUnobtainedCriteria();
            List<String> entries = new ArrayList<>();
            criteria.forEach(entries::add);

            if (entries.size() == 0) {
                return Collections.singletonList("§2None");
            } else {
                Collections.sort(entries);
                int start = (int) scroll;
                int end = (int) scroll + 9;
                List<String> description = new ArrayList<>();
                for (String entry : entries) {
                    description.addAll(this.wrapDescription("§a" + entry));
                }
                description = description.subList(start, end);
                description.add(0, "§2Criteria (" + entries.size() + "):");
                return description;
            }
        } else {
            return this.wrapDescription(this.display.getDescription().asFormattedString());
        }
    }

    @Inject(method = "method_2331", at = @At(value = "HEAD"))
    private void tooltipInjector(int int_1, int int_2, float float_1, int int_3, int int_4, CallbackInfo ci) {
        if (this.field_2714 != null) {
            this.field_2705 = getDescriptionList();
        }
    }
}