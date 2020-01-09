package me.fivespace.advancementstooltip.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class DetailedAdvancementWidget extends DrawableHelper {

    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
    private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(.+) \\S+");
    private final DetailedAdvancementTreeWidget tree;
    private final Advancement advancement;
    private final AdvancementDisplay display;
    private final String title;
    private final int width;
    private List<String> description;
    private final MinecraftClient client;
    private DetailedAdvancementWidget parent;
    private final List<DetailedAdvancementWidget> children = Lists.newArrayList();
    private AdvancementProgress progress;
    private final int xPos;
    private final int yPos;

    private int scrollStart;
    private final int scrollLength;
    private boolean isShiftKeyHeld;

    public DetailedAdvancementWidget(DetailedAdvancementTreeWidget advancementTreeWidget_1, MinecraftClient minecraftClient_1, Advancement advancement_1, AdvancementDisplay advancementDisplay_1) {
        this.tree = advancementTreeWidget_1;
        this.advancement = advancement_1;
        this.display = advancementDisplay_1;
        this.client = minecraftClient_1;
        this.title = minecraftClient_1.textRenderer.trimToWidth(advancementDisplay_1.getTitle().asFormattedString(), 163);
        this.xPos = MathHelper.floor(advancementDisplay_1.getX() * 28.0F);
        this.yPos = MathHelper.floor(advancementDisplay_1.getY() * 27.0F);

        this.scrollStart = 0;
        this.scrollLength = 10;
        this.isShiftKeyHeld = false;

        int int_1 = advancement_1.getRequirementCount();
        int int_2 = String.valueOf(int_1).length();
        int int_3 = int_1 > 1 ? minecraftClient_1.textRenderer.getStringWidth("  ") + minecraftClient_1.textRenderer.getStringWidth("0") * int_2 * 2 + minecraftClient_1.textRenderer.getStringWidth("/") : 0;
        int int_4 = 29 + minecraftClient_1.textRenderer.getStringWidth(this.title) + int_3;
        String string_1 = advancementDisplay_1.getDescription().asFormattedString();
        this.description = this.wrapDescription(string_1, int_4);

        String string_2;
        for (Iterator var10 = this.description.iterator(); var10.hasNext(); int_4 = Math.max(int_4, minecraftClient_1.textRenderer.getStringWidth(string_2))) {
            string_2 = (String) var10.next();
        }

        this.width = int_4 + 3 + 5;
    }

    private void setDescription(String text) {
        int int_1 = this.advancement.getRequirementCount();
        int int_2 = String.valueOf(int_1).length();
        int int_3 = int_1 > 1 ? this.client.textRenderer.getStringWidth("  ") + this.client.textRenderer.getStringWidth("0") * int_2 * 2 + this.client.textRenderer.getStringWidth("/") : 0;
        int int_4 = 29 + this.client.textRenderer.getStringWidth(this.title) + int_3;
        this.description = this.wrapDescription(text, int_4);
    }

    private void resetDescription() {
        String desc = this.display.getDescription().asFormattedString();
        this.setDescription(desc);
    }

    //TODO: bruh
    private void updateDescription() {
        if (this.progress == null) {
            return;
        }

        Iterable<String> criteria = this.progress.getUnobtainedCriteria();
        List<String> entries = new ArrayList<>();
        criteria.forEach(entries::add);

        if (Screen.hasShiftDown() && !this.isShiftKeyHeld) {
            this.isShiftKeyHeld = true;
        } else if (!Screen.hasShiftDown() && this.isShiftKeyHeld) {
            this.isShiftKeyHeld = false;
        } else if (this.isShiftKeyHeld) {
            if (Screen.hasAltDown() && this.scrollStart > 0) {
                this.scrollStart -= 1;
            } else if (Screen.hasControlDown() && this.scrollStart + this.scrollLength < entries.size()) {
                this.scrollStart += 1;
            }
        }

        String desc;
        if (entries.size() == 0) {
            desc = "§2None";
        } else {
            Collections.sort(entries);
            int start = Math.max(0, Math.min(entries.size(), scrollStart));
            int end = Math.max(0, Math.min(entries.size(), scrollStart + scrollLength));
            desc = "§2Criteria (" + entries.size() + "):\n§a" + String.join("\n", entries.subList(start, end));
        }

        if (this.isShiftKeyHeld) {
            this.setDescription(desc);
        } else {
            this.resetDescription();
        }
    }

    private List<String> wrapDescription(String string_1, int int_1) {
        if (string_1.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> list_1 = this.client.textRenderer.wrapStringToWidthAsList(string_1, int_1);
            if (list_1.size() < 2) {
                return list_1;
            } else {
                String string_2 = list_1.get(0);
                String string_3 = list_1.get(1);
                int int_2 = this.client.textRenderer.getStringWidth(string_2 + ' ' + string_3.split(" ")[0]);
                if (int_2 - int_1 <= 10) {
                    return this.client.textRenderer.wrapStringToWidthAsList(string_1, int_2);
                } else {
                    Matcher matcher_1 = BACKSLASH_S_PATTERN.matcher(string_2);
                    if (matcher_1.matches()) {
                        int int_3 = this.client.textRenderer.getStringWidth(matcher_1.group(1));
                        if (int_1 - int_3 <= 10) {
                            return this.client.textRenderer.wrapStringToWidthAsList(string_1, int_3);
                        }
                    }

                    return list_1;
                }
            }
        }
    }

    
    private DetailedAdvancementWidget getRootWidget(Advancement advancement_1) {
        do {
            advancement_1 = advancement_1.getParent();
        } while (advancement_1 != null && advancement_1.getDisplay() == null);

        if (advancement_1 != null && advancement_1.getDisplay() != null) {
            return this.tree.getWidgetForAdvancement(advancement_1);
        } else {
            return null;
        }
    }

    public void renderLines(int int_1, int int_2, boolean boolean_1) {
        if (this.parent != null) {
            int int_3 = int_1 + this.parent.xPos + 13;
            int int_4 = int_1 + this.parent.xPos + 26 + 4;
            int int_5 = int_2 + this.parent.yPos + 13;
            int int_6 = int_1 + this.xPos + 13;
            int int_7 = int_2 + this.yPos + 13;
            int int_8 = boolean_1 ? -16777216 : -1;
            if (boolean_1) {
                this.hLine(int_4, int_3, int_5 - 1, int_8);
                this.hLine(int_4 + 1, int_3, int_5, int_8);
                this.hLine(int_4, int_3, int_5 + 1, int_8);
                this.hLine(int_6, int_4 - 1, int_7 - 1, int_8);
                this.hLine(int_6, int_4 - 1, int_7, int_8);
                this.hLine(int_6, int_4 - 1, int_7 + 1, int_8);
                this.vLine(int_4 - 1, int_7, int_5, int_8);
                this.vLine(int_4 + 1, int_7, int_5, int_8);
            } else {
                this.hLine(int_4, int_3, int_5, int_8);
                this.hLine(int_6, int_4, int_7, int_8);
                this.vLine(int_4, int_7, int_5, int_8);
            }
        }

        Iterator var10 = this.children.iterator();

        while (var10.hasNext()) {
            DetailedAdvancementWidget advancementWidget_1 = (DetailedAdvancementWidget) var10.next();
            advancementWidget_1.renderLines(int_1, int_2, boolean_1);
        }

    }

    public void renderWidgets(int int_1, int int_2) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            float float_1 = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
            AdvancementObtainedStatus advancementObtainedStatus_2;
            if (float_1 >= 1.0F) {
                advancementObtainedStatus_2 = AdvancementObtainedStatus.OBTAINED;
            } else {
                advancementObtainedStatus_2 = AdvancementObtainedStatus.UNOBTAINED;
            }

            this.client.getTextureManager().bindTexture(WIDGETS_TEX);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            this.blit(int_1 + this.xPos + 3, int_2 + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus_2.getSpriteIndex() * 26, 26, 26);
            GuiLighting.enableForItems();
            this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), int_1 + this.xPos + 8, int_2 + this.yPos + 5);
        }

        Iterator var5 = this.children.iterator();

        while (var5.hasNext()) {
            DetailedAdvancementWidget advancementWidget_1 = (DetailedAdvancementWidget) var5.next();
            advancementWidget_1.renderWidgets(int_1, int_2);
        }

    }

    public void setProgress(AdvancementProgress advancementProgress_1) {
        this.progress = advancementProgress_1;
        this.updateDescription();
    }

    public void addChild(DetailedAdvancementWidget advancementWidget_1) {
        this.children.add(advancementWidget_1);
    }

    public void drawTooltip(int int_1, int int_2, float float_1, int int_3, int int_4) {
        boolean boolean_1 = int_3 + int_1 + this.xPos + this.width + 26 >= this.tree.getScreen().width;
        String string_1 = this.progress == null ? null : this.progress.getProgressBarFraction();
        int int_5 = string_1 == null ? 0 : this.client.textRenderer.getStringWidth(string_1);
        int var10000 = 113 - int_2 - this.yPos - 26;
        int var10002 = this.description.size();
        boolean boolean_2 = var10000 <= 6 + var10002 * 9;
        float float_2 = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
        int int_6 = MathHelper.floor(float_2 * (float) this.width);
        AdvancementObtainedStatus advancementObtainedStatus_10;
        AdvancementObtainedStatus advancementObtainedStatus_11;
        AdvancementObtainedStatus advancementObtainedStatus_12;
        if (float_2 >= 1.0F) {
            int_6 = this.width / 2;
            advancementObtainedStatus_10 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus_11 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus_12 = AdvancementObtainedStatus.OBTAINED;
        } else if (int_6 < 2) {
            int_6 = this.width / 2;
            advancementObtainedStatus_10 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus_11 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus_12 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (int_6 > this.width - 2) {
            int_6 = this.width / 2;
            advancementObtainedStatus_10 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus_11 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus_12 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            advancementObtainedStatus_10 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus_11 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus_12 = AdvancementObtainedStatus.UNOBTAINED;
        }

        int int_7 = this.width - int_6;
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        int int_8 = int_2 + this.yPos;
        int int_10;
        if (boolean_1) {
            int_10 = int_1 + this.xPos - this.width + 26 + 6;
        } else {
            int_10 = int_1 + this.xPos;
        }

        this.updateDescription();

        int var10001 = this.description.size();
        int int_11 = 32 + var10001 * 9;
        if (!this.description.isEmpty()) {
            if (boolean_2) {
                this.method_2324(int_10, int_8 + 26 - int_11, this.width, int_11, 10, 200, 26, 0, 52);
            } else {
                this.method_2324(int_10, int_8, this.width, int_11, 10, 200, 26, 0, 52);
            }
        }

        this.blit(int_10, int_8, 0, advancementObtainedStatus_10.getSpriteIndex() * 26, int_6, 26);
        this.blit(int_10 + int_6, int_8, 200 - int_7, advancementObtainedStatus_11.getSpriteIndex() * 26, int_7, 26);
        this.blit(int_1 + this.xPos + 3, int_2 + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus_12.getSpriteIndex() * 26, 26, 26);
        if (boolean_1) {
            this.client.textRenderer.drawWithShadow(this.title, (float) (int_10 + 5), (float) (int_2 + this.yPos + 9), -1);
            if (string_1 != null) {
                this.client.textRenderer.drawWithShadow(string_1, (float) (int_1 + this.xPos - int_5), (float) (int_2 + this.yPos + 9), -1);
            }
        } else {
            this.client.textRenderer.drawWithShadow(this.title, (float) (int_1 + this.xPos + 32), (float) (int_2 + this.yPos + 9), -1);
            if (string_1 != null) {
                this.client.textRenderer.drawWithShadow(string_1, (float) (int_1 + this.xPos + this.width - int_5 - 5), (float) (int_2 + this.yPos + 9), -1);
            }
        }

        int int_12;
        int var10003;
        TextRenderer var20;
        String var21;
        float var22;
        if (boolean_2) {
            for (int_12 = 0; int_12 < this.description.size(); ++int_12) {
                var20 = this.client.textRenderer;
                var21 = this.description.get(int_12);
                var22 = (float) (int_10 + 5);
                var10003 = int_8 + 26 - int_11 + 7;
                var20.draw(var21, var22, (float) (var10003 + int_12 * 9), -5592406);
            }
        } else {
            for (int_12 = 0; int_12 < this.description.size(); ++int_12) {
                var20 = this.client.textRenderer;
                var21 = this.description.get(int_12);
                var22 = (float) (int_10 + 5);
                var10003 = int_2 + this.yPos + 9 + 17;
                var20.draw(var21, var22, (float) (var10003 + int_12 * 9), -5592406);
            }
        }

        GuiLighting.enableForItems();
        this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), int_1 + this.xPos + 8, int_2 + this.yPos + 5);
    }

    protected void method_2324(int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, int int_8, int int_9) {
        this.blit(int_1, int_2, int_8, int_9, int_5, int_5);
        this.method_2321(int_1 + int_5, int_2, int_3 - int_5 - int_5, int_5, int_8 + int_5, int_9, int_6 - int_5 - int_5, int_7);
        this.blit(int_1 + int_3 - int_5, int_2, int_8 + int_6 - int_5, int_9, int_5, int_5);
        this.blit(int_1, int_2 + int_4 - int_5, int_8, int_9 + int_7 - int_5, int_5, int_5);
        this.method_2321(int_1 + int_5, int_2 + int_4 - int_5, int_3 - int_5 - int_5, int_5, int_8 + int_5, int_9 + int_7 - int_5, int_6 - int_5 - int_5, int_7);
        this.blit(int_1 + int_3 - int_5, int_2 + int_4 - int_5, int_8 + int_6 - int_5, int_9 + int_7 - int_5, int_5, int_5);
        this.method_2321(int_1, int_2 + int_5, int_5, int_4 - int_5 - int_5, int_8, int_9 + int_5, int_6, int_7 - int_5 - int_5);
        this.method_2321(int_1 + int_5, int_2 + int_5, int_3 - int_5 - int_5, int_4 - int_5 - int_5, int_8 + int_5, int_9 + int_5, int_6 - int_5 - int_5, int_7 - int_5 - int_5);
        this.method_2321(int_1 + int_3 - int_5, int_2 + int_5, int_5, int_4 - int_5 - int_5, int_8 + int_6 - int_5, int_9 + int_5, int_6, int_7 - int_5 - int_5);
    }

    protected void method_2321(int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, int int_8) {
        for (int int_9 = 0; int_9 < int_3; int_9 += int_7) {
            int int_10 = int_1 + int_9;
            int int_11 = Math.min(int_7, int_3 - int_9);

            for (int int_12 = 0; int_12 < int_4; int_12 += int_8) {
                int int_13 = int_2 + int_12;
                int int_14 = Math.min(int_8, int_4 - int_12);
                this.blit(int_10, int_13, int_5, int_6, int_11, int_14);
            }
        }

    }

    public boolean shouldRender(int int_1, int int_2, int int_3, int int_4) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            int int_5 = int_1 + this.xPos;
            int int_6 = int_5 + 26;
            int int_7 = int_2 + this.yPos;
            int int_8 = int_7 + 26;
            return int_3 >= int_5 && int_3 <= int_6 && int_4 >= int_7 && int_4 <= int_8;
        } else {
            return false;
        }
    }

    public void addToTree() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getRootWidget(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }

    }

    public int getYPos() {
        return this.yPos;
    }

    public int getXPos() {
        return this.xPos;
    }
}
