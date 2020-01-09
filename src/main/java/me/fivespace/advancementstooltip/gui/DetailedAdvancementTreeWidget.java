package me.fivespace.advancementstooltip.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;

import java.util.Iterator;
import java.util.Map;

import me.fivespace.advancementstooltip.hooks.AdvancementTabTypeHooks;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class DetailedAdvancementTreeWidget extends DrawableHelper {

    private final MinecraftClient client;
    private final DetailedAdvancementsScreen screen;
    private final AdvancementTabTypeHooks tabType;
    private final int index;
    private final Advancement rootAdvancement;
    private final AdvancementDisplay display;
    private final ItemStack icon;
    private final String title;
    private final DetailedAdvancementWidget rootWidget;
    private final Map<Advancement, DetailedAdvancementWidget> widgets = Maps.newLinkedHashMap();
    private double originX;
    private double originY;
    private int minPanX = 2147483647;
    private int minPanY = 2147483647;
    private int maxPanX = -2147483648;
    private int maxPanY = -2147483648;
    private float alpha;
    private boolean initialized;

    public DetailedAdvancementTreeWidget(MinecraftClient client, DetailedAdvancementsScreen screen, AdvancementTabTypeHooks tabType, int index, Advancement root, AdvancementDisplay display) {
        this.client = client;
        this.screen = screen;
        this.tabType = tabType;
        this.index = index;
        this.rootAdvancement = root;
        this.display = display;
        this.icon = display.getIcon();
        this.title = display.getTitle().asFormattedString();
        this.rootWidget = new DetailedAdvancementWidget(this, client, root, display);
        this.addWidget(this.rootWidget, root);
    }

    public Advancement getRootAdvancement() {
        return this.rootAdvancement;
    }

    public String getTitle() {
        return this.title;
    }

    public void drawBackground(int int_1, int int_2, boolean boolean_1) {
        this.tabType.drawBackground(this, int_1, int_2, boolean_1, this.index);
    }

    public void drawIcon(int int_1, int int_2, ItemRenderer itemRenderer_1) {
        this.tabType.drawIcon(int_1, int_2, this.index, itemRenderer_1, this.icon);
    }

    public void render() {
        if (!this.initialized) {
            this.originX = (double) (117 - (this.maxPanX + this.minPanX) / 2);
            this.originY = (double) (56 - (this.maxPanY + this.minPanY) / 2);
            this.initialized = true;
        }

        GlStateManager.depthFunc(518);
        fill(0, 0, 234, 113, -16777216);
        GlStateManager.depthFunc(515);
        Identifier identifier_1 = this.display.getBackground();
        if (identifier_1 != null) {
            this.client.getTextureManager().bindTexture(identifier_1);
        } else {
            this.client.getTextureManager().bindTexture(TextureManager.MISSING_IDENTIFIER);
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int int_1 = MathHelper.floor(this.originX);
        int int_2 = MathHelper.floor(this.originY);
        int int_3 = int_1 % 16;
        int int_4 = int_2 % 16;

        for (int int_5 = -1; int_5 <= 15; ++int_5) {
            for (int int_6 = -1; int_6 <= 8; ++int_6) {
                blit(int_3 + 16 * int_5, int_4 + 16 * int_6, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        this.rootWidget.renderLines(int_1, int_2, true);
        this.rootWidget.renderLines(int_1, int_2, false);
        this.rootWidget.renderWidgets(int_1, int_2);
    }

    public void drawWidgetTooltip(int int_1, int int_2, int int_3, int int_4) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0F, 0.0F, 200.0F);
        fill(0, 0, 234, 113, MathHelper.floor(this.alpha * 255.0F) << 24);
        boolean boolean_1 = false;
        int int_5 = MathHelper.floor(this.originX);
        int int_6 = MathHelper.floor(this.originY);
        if (int_1 > 0 && int_1 < 234 && int_2 > 0 && int_2 < 113) {
            Iterator var8 = this.widgets.values().iterator();

            while (var8.hasNext()) {
                DetailedAdvancementWidget advancementWidget_1 = (DetailedAdvancementWidget) var8.next();
                if (advancementWidget_1.shouldRender(int_5, int_6, int_1, int_2)) {
                    boolean_1 = true;
                    advancementWidget_1.drawTooltip(int_5, int_6, this.alpha, int_3, int_4);
                    break;
                }
            }
        }

        GlStateManager.popMatrix();
        if (boolean_1) {
            this.alpha = MathHelper.clamp(this.alpha + 0.02F, 0.0F, 0.3F);
        } else {
            this.alpha = MathHelper.clamp(this.alpha - 0.04F, 0.0F, 1.0F);
        }

    }

    public boolean isClickOnTab(int int_1, int int_2, double double_1, double double_2) {
        return this.tabType.isClickOnTab(int_1, int_2, this.index, double_1, double_2);
    }

    public static DetailedAdvancementTreeWidget create(MinecraftClient minecraftClient_1, DetailedAdvancementsScreen advancementsScreen_1, int int_1, Advancement advancement_1) throws ClassNotFoundException {
        if (advancement_1.getDisplay() == null) {
            return null;
        } else {
            AdvancementTabTypeHooks[] var4 = AdvancementTabTypeHooks.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                AdvancementTabTypeHooks advancementTabType_1 = var4[var6];
                if (int_1 < advancementTabType_1.method_2304()) {
                    return new DetailedAdvancementTreeWidget(minecraftClient_1, advancementsScreen_1, advancementTabType_1, int_1, advancement_1, advancement_1.getDisplay());
                }

                int_1 -= advancementTabType_1.method_2304();
            }

            return null;
        }
    }

    public void move(double double_1, double double_2) {
        if (this.maxPanX - this.minPanX > 234) {
            this.originX = MathHelper.clamp(this.originX + double_1, (double) (-(this.maxPanX - 234)), 0.0D);
        }

        if (this.maxPanY - this.minPanY > 113) {
            this.originY = MathHelper.clamp(this.originY + double_2, (double) (-(this.maxPanY - 113)), 0.0D);
        }

    }

    public void addAdvancement(Advancement advancement_1) {
        if (advancement_1.getDisplay() != null) {
            DetailedAdvancementWidget advancementWidget_1 = new DetailedAdvancementWidget(this, this.client, advancement_1, advancement_1.getDisplay());
            this.addWidget(advancementWidget_1, advancement_1);
        }
    }

    private void addWidget(DetailedAdvancementWidget advancementWidget_1, Advancement advancement_1) {
        this.widgets.put(advancement_1, advancementWidget_1);
        int int_1 = advancementWidget_1.getXPos();
        int int_2 = int_1 + 28;
        int int_3 = advancementWidget_1.getYPos();
        int int_4 = int_3 + 27;
        this.minPanX = Math.min(this.minPanX, int_1);
        this.maxPanX = Math.max(this.maxPanX, int_2);
        this.minPanY = Math.min(this.minPanY, int_3);
        this.maxPanY = Math.max(this.maxPanY, int_4);
        Iterator var7 = this.widgets.values().iterator();

        while (var7.hasNext()) {
            DetailedAdvancementWidget advancementWidget_2 = (DetailedAdvancementWidget) var7.next();
            advancementWidget_2.addToTree();
        }

    }


    public DetailedAdvancementWidget getWidgetForAdvancement(Advancement advancement_1) {
        return this.widgets.get(advancement_1);
    }

    public DetailedAdvancementsScreen getScreen() {
        return this.screen;
    }
}
