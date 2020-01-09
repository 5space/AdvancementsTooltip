package me.fivespace.advancementstooltip.hooks;


import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

public enum AdvancementTabTypeHooks {
    ABOVE(0, 0, 28, 32, 8),
    BELOW(84, 0, 28, 32, 8),
    LEFT(0, 64, 32, 28, 5),
    RIGHT(96, 64, 32, 28, 5);

    private final int field_2674;
    private final int field_2672;
    private final int field_2671;
    private final int field_2670;
    private final int field_2669;

    AdvancementTabTypeHooks(int int_1, int int_2, int int_3, int int_4, int int_5) {
        this.field_2674 = int_1;
        this.field_2672 = int_2;
        this.field_2671 = int_3;
        this.field_2670 = int_4;
        this.field_2669 = int_5;
    }

    public int method_2304() {
        return this.field_2669;
    }

    public void drawBackground(DrawableHelper drawableHelper_1, int int_1, int int_2, boolean boolean_1, int int_3) {
        int int_4 = this.field_2674;
        if (int_3 > 0) {
            int_4 += this.field_2671;
        }

        if (int_3 == this.field_2669 - 1) {
            int_4 += this.field_2671;
        }

        int int_5 = boolean_1 ? this.field_2672 + this.field_2670 : this.field_2672;
        drawableHelper_1.blit(int_1 + this.method_2302(int_3), int_2 + this.method_2305(int_3), int_4, int_5, this.field_2671, this.field_2670);
    }

    public void drawIcon(int int_1, int int_2, int int_3, ItemRenderer itemRenderer_1, ItemStack itemStack_1) {
        int int_4 = int_1 + this.method_2302(int_3);
        int int_5 = int_2 + this.method_2305(int_3);
        switch(this) {
            case ABOVE:
                int_4 += 6;
                int_5 += 9;
                break;
            case BELOW:
                int_4 += 6;
                int_5 += 6;
                break;
            case LEFT:
                int_4 += 10;
                int_5 += 5;
                break;
            case RIGHT:
                int_4 += 6;
                int_5 += 5;
        }

        itemRenderer_1.renderGuiItem(null, itemStack_1, int_4, int_5);
    }

    public int method_2302(int int_1) {
        switch(this) {
            case ABOVE:
            case BELOW:
                return (this.field_2671 + 4) * int_1;
            case LEFT:
                return -this.field_2671 + 4;
            case RIGHT:
                return 248;
            default:
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
        }
    }

    public int method_2305(int int_1) {
        switch(this) {
            case ABOVE:
                return -this.field_2670 + 4;
            case BELOW:
                return 136;
            case LEFT:
            case RIGHT:
                return this.field_2670 * int_1;
            default:
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
        }
    }

    public boolean isClickOnTab(int int_1, int int_2, int int_3, double double_1, double double_2) {
        int int_4 = int_1 + this.method_2302(int_3);
        int int_5 = int_2 + this.method_2305(int_3);
        return double_1 > (double)int_4 && double_1 < (double)(int_4 + this.field_2671) && double_2 > (double)int_5 && double_2 < (double)(int_5 + this.field_2670);
    }
}