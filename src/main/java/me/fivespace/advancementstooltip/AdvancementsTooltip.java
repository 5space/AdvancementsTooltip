package me.fivespace.advancementstooltip;

import net.fabricmc.api.ModInitializer;
import net.minecraft.advancement.AdvancementProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvancementsTooltip implements ModInitializer {

//    public static String getCriteriaLeftDescription(AdvancementProgress progress, int start, int end) {
//        Iterable<String> criteria = progress.getUnobtainedCriteria();
//        List<String> entries = new ArrayList<>();
//        criteria.forEach(entries::add);
//        if (entries.size() == 0) {
//            return "§2None";
//        } else {
//            Collections.sort(entries);
//            start = Math.max(0, Math.min(entries.size(), start));
//            end = Math.max(0, Math.min(entries.size(), end));
//            return "§2Criteria remaining:\n§a" + String.join("\n", entries.subList(start, end));
//        }
//    }

	@Override
	public void onInitialize() {

	}
}
