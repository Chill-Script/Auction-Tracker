package com.example.auction;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.List;

public class AuctionTooltip {
    public static void appendTooltip(ItemStack stack, List<Text> tooltip) {
        if (stack == null || stack.isEmpty()) return;

        if (!Screen.hasShiftDown()) {
            tooltip.add(Text.literal("Hold [SHIFT] for Market Data").formatted(Formatting.GRAY));
            return;
        }

        String id = AuctionScanner.getFingerprint(stack);
        List<Double> history = DataStorage.getItemHistory(id);

        if (history != null && !history.isEmpty()) {
            tooltip.add(Text.literal(" "));
            tooltip.add(Text.literal("📊 MARKET DATA 📊").formatted(Formatting.GOLD, Formatting.BOLD));

            tooltip.add(Text.literal("Lowest Price: ").formatted(Formatting.GREEN)
                .append(Text.literal("$" + String.format("%,.0f", DataStorage.getMin(id))).formatted(Formatting.GREEN)));
            
            tooltip.add(Text.literal("Highest Price: ").formatted(Formatting.RED)
                .append(Text.literal("$" + String.format("%,.0f", DataStorage.getMax(id))).formatted(Formatting.RED)));

            int count = DataStorage.getCurrentCount(id);
            if (count > 0) {
                tooltip.add(Text.literal("Status: ").formatted(Formatting.GRAY)
                    .append(Text.literal("Available Now (" + count + ")").formatted(Formatting.AQUA)));
            } else {
                tooltip.add(Text.literal("Status: ").formatted(Formatting.GRAY)
                    .append(Text.literal("Last seen " + DataStorage.getLastSeenDate(id)).formatted(Formatting.YELLOW)));
            }
        }
    }
}