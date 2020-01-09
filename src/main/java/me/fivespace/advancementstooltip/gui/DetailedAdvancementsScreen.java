package me.fivespace.advancementstooltip.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientAdvancementManager.Listener;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.util.Identifier;

public class DetailedAdvancementsScreen extends Screen implements Listener {

    private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
    private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
    private final ClientAdvancementManager advancementHandler;
    private final Map<Advancement, DetailedAdvancementTreeWidget> widgetMap = Maps.newLinkedHashMap();
    private DetailedAdvancementTreeWidget selectedWidget;
    private boolean movingTab;

    public DetailedAdvancementsScreen(ClientAdvancementManager advancementHandler) {
        super(NarratorManager.EMPTY);
        this.advancementHandler = advancementHandler;
    }

    protected void init() {
        this.widgetMap.clear();
        this.selectedWidget = null;
        this.advancementHandler.setListener(this);
        if (this.selectedWidget == null && !this.widgetMap.isEmpty()) {
            this.advancementHandler.selectTab(this.widgetMap.values().iterator().next().getRootAdvancement(), true);
        } else {
            this.advancementHandler.selectTab(this.selectedWidget == null ? null : this.selectedWidget.getRootAdvancement(), true);
        }

    }

    public void removed() {
        this.advancementHandler.setListener(null);
        ClientPlayNetworkHandler clientPlayNetworkHandler_1 = this.minecraft.getNetworkHandler();
        if (clientPlayNetworkHandler_1 != null) {
            clientPlayNetworkHandler_1.sendPacket(AdvancementTabC2SPacket.close());
        }

    }

    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (int_1 == 0) {
            int int_2 = (this.width - 252) / 2;
            int int_3 = (this.height - 140) / 2;
            Iterator var8 = this.widgetMap.values().iterator();

            while (var8.hasNext()) {
                DetailedAdvancementTreeWidget advancementTreeWidget_1 = (DetailedAdvancementTreeWidget) var8.next();
                if (advancementTreeWidget_1.isClickOnTab(int_2, int_3, double_1, double_2)) {
                    this.advancementHandler.selectTab(advancementTreeWidget_1.getRootAdvancement(), true);
                    break;
                }
            }
        }

        return super.mouseClicked(double_1, double_2, int_1);
    }

    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (this.minecraft.options.keyAdvancements.matchesKey(int_1, int_2)) {
            this.minecraft.openScreen(null);
            this.minecraft.mouse.lockCursor();
            return true;
        } else {
            return super.keyPressed(int_1, int_2, int_3);
        }
    }

    public void render(int int_1, int int_2, float float_1) {
        int int_3 = (this.width - 252) / 2;
        int int_4 = (this.height - 140) / 2;
        this.renderBackground();
        this.drawAdvancementTree(int_1, int_2, int_3, int_4);
        this.drawWidgets(int_3, int_4);
        this.drawWidgetTooltip(int_1, int_2, int_3, int_4);
    }

    public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
        if (int_1 != 0) {
            this.movingTab = false;
            return false;
        } else {
            if (!this.movingTab) {
                this.movingTab = true;
            } else if (this.selectedWidget != null) {
                this.selectedWidget.move(double_3, double_4);
            }

            return true;
        }
    }

    private void drawAdvancementTree(int int_1, int int_2, int int_3, int int_4) {
        DetailedAdvancementTreeWidget advancementTreeWidget_1 = this.selectedWidget;
        if (advancementTreeWidget_1 == null) {
            fill(int_3 + 9, int_4 + 18, int_3 + 9 + 234, int_4 + 18 + 113, -16777216);
            String string_1 = I18n.translate("advancements.empty");
            int int_5 = this.font.getStringWidth(string_1);
            TextRenderer var10000 = this.font;
            float var10002 = (float) (int_3 + 9 + 117 - int_5 / 2);
            int var10003 = int_4 + 18 + 56;
            var10000.draw(string_1, var10002, (float) (var10003 - 9 / 2), -1);
            var10000 = this.font;
            var10002 = (float) (int_3 + 9 + 117 - this.font.getStringWidth(":(") / 2);
            var10003 = int_4 + 18 + 113;
            var10000.draw(":(", var10002, (float) (var10003 - 9), -1);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) (int_3 + 9), (float) (int_4 + 18), -400.0F);
            GlStateManager.enableDepthTest();
            advancementTreeWidget_1.render();
            GlStateManager.popMatrix();
            GlStateManager.depthFunc(515);
            GlStateManager.disableDepthTest();
        }
    }

    public void drawWidgets(int int_1, int int_2) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GuiLighting.disable();
        this.minecraft.getTextureManager().bindTexture(WINDOW_TEXTURE);
        this.blit(int_1, int_2, 0, 0, 252, 140);
        if (this.widgetMap.size() > 1) {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            Iterator var3 = this.widgetMap.values().iterator();

            DetailedAdvancementTreeWidget advancementTreeWidget_2;
            while (var3.hasNext()) {
                advancementTreeWidget_2 = (DetailedAdvancementTreeWidget) var3.next();
                advancementTreeWidget_2.drawBackground(int_1, int_2, advancementTreeWidget_2 == this.selectedWidget);
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
            GuiLighting.enableForItems();
            var3 = this.widgetMap.values().iterator();

            while (var3.hasNext()) {
                advancementTreeWidget_2 = (DetailedAdvancementTreeWidget) var3.next();
                advancementTreeWidget_2.drawIcon(int_1, int_2, this.itemRenderer);
            }

            GlStateManager.disableBlend();
        }

        this.font.draw(I18n.translate("gui.advancements"), (float) (int_1 + 8), (float) (int_2 + 6), 4210752);
    }

    private void drawWidgetTooltip(int int_1, int int_2, int int_3, int int_4) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.selectedWidget != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepthTest();
            GlStateManager.translatef((float) (int_3 + 9), (float) (int_4 + 18), 400.0F);
            this.selectedWidget.drawWidgetTooltip(int_1 - int_3 - 9, int_2 - int_4 - 18, int_3, int_4);
            GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
        }

        if (this.widgetMap.size() > 1) {
            Iterator var5 = this.widgetMap.values().iterator();

            while (var5.hasNext()) {
                DetailedAdvancementTreeWidget advancementTreeWidget_1 = (DetailedAdvancementTreeWidget) var5.next();
                if (advancementTreeWidget_1.isClickOnTab(int_3, int_4, (double) int_1, (double) int_2)) {
                    this.renderTooltip(advancementTreeWidget_1.getTitle(), int_1, int_2);
                }
            }
        }

    }

    public void onRootAdded(Advancement advancement_1) {
        try {
            DetailedAdvancementTreeWidget advancementTreeWidget_1 = DetailedAdvancementTreeWidget.create(this.minecraft, this, this.widgetMap.size(), advancement_1);
            if (advancementTreeWidget_1 != null) {
                this.widgetMap.put(advancement_1, advancementTreeWidget_1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onRootRemoved(Advancement advancement_1) {
    }

    public void onDependentAdded(Advancement advancement_1) {
        DetailedAdvancementTreeWidget advancementTreeWidget_1 = this.getDetailedAdvancementTreeWidget(advancement_1);
        if (advancementTreeWidget_1 != null) {
            advancementTreeWidget_1.addAdvancement(advancement_1);
        }

    }

    public void onDependentRemoved(Advancement advancement_1) {
    }

    public void setProgress(Advancement advancement_1, AdvancementProgress advancementProgress_1) {
        DetailedAdvancementWidget advancementWidget_1 = this.getDetailedAdvancementWidget(advancement_1);
        if (advancementWidget_1 != null) {
            advancementWidget_1.setProgress(advancementProgress_1);
        }

    }

    public void selectTab(Advancement advancement_1) {
        this.selectedWidget = this.widgetMap.get(advancement_1);
    }

    public void onClear() {
        this.widgetMap.clear();
        this.selectedWidget = null;
    }


    public DetailedAdvancementWidget getDetailedAdvancementWidget(Advancement advancement_1) {
        DetailedAdvancementTreeWidget advancementTreeWidget_1 = this.getDetailedAdvancementTreeWidget(advancement_1);
        return advancementTreeWidget_1 == null ? null : advancementTreeWidget_1.getWidgetForAdvancement(advancement_1);
    }


    private DetailedAdvancementTreeWidget getDetailedAdvancementTreeWidget(Advancement advancement_1) {
        while (advancement_1.getParent() != null) {
            advancement_1 = advancement_1.getParent();
        }

        return this.widgetMap.get(advancement_1);
    }
}
