package me.fivespace.advancementstooltip.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.screen.advancement.AdvancementTreeWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementTreeWidget.class)
public interface AdvancementTreeWidgetMixin {

    @Accessor("widgets")
    Map<Advancement, AdvancementWidget> getWidgets();

    @Accessor("field_2690")
    double getOriginX();

    @Accessor("field_2689")
    double getOriginY();
}
