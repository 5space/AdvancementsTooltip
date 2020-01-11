package me.fivespace.advancementstooltip.mixin;

import me.fivespace.advancementstooltip.IAdvancementWidgetMixin;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementTreeWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin extends Screen {

    @Shadow private AdvancementTreeWidget selectedWidget;

    public AdvancementsScreenMixin(ClientAdvancementManager clientAdvancementManager_1) {
        super(NarratorManager.EMPTY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amt) {
        if (Screen.hasShiftDown()) {
            AdvancementTreeWidgetMixin treeWidget = ((AdvancementTreeWidgetMixin) this.selectedWidget);
            Collection<AdvancementWidget> widgets = treeWidget.getWidgets().values();
            int x = (this.width - 252)/2;
            int y = (this.height - 140)/2;
            int originX = MathHelper.floor(treeWidget.getOriginX());
            int originY = MathHelper.floor(treeWidget.getOriginY());
            for (AdvancementWidget widget : widgets) {
                if (widget.method_2329(originX, originY, (int) mouseX - x - 9, (int) mouseY - y - 18)) {
                    ((IAdvancementWidgetMixin) widget).changeScroll(amt);
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
