package com.example.auction;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import java.util.List;

public class AuctionScanner {
    
    /**
     * Generates a unique, stable ID for items.
     * This is the "Scientific Name" (e.g., item.minecraft.diamond_sword).
     * It is 100% consistent across different scans and game restarts.
     */
    public static String getFingerprint(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "empty";
        return stack.getItem().getTranslationKey();
    }

    /**
     * Parses the item lore to extract price data and sends it to DataStorage.
     */
    public static void scanSlot(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;

        List<Text> lore = stack.getLore().getLines();
        double finalPrice = -1;

        for (Text line : lore) {
            // Convert to lowercase and strip commas for easier numerical parsing
            String text = line.getString().toLowerCase().replace(",", "");
            
            // Common AH labels (supports multiple server styles)
            if (text.contains("price:") || text.contains("buy it now:") || text.contains("cost:")) {
                
                // Keep only numbers and the decimal point
                String numeric = text.replaceAll("[^0-9.]", "");
                
                if (!numeric.isEmpty()) {
                    try {
                        finalPrice = Double.parseDouble(numeric);
                    } catch (NumberFormatException e) {
                        // If parsing fails for one line, continue checking others
                        continue;
                    }
                }
            }
        }

        // Only save if a valid price was found
        if (finalPrice > 0) {
            // Record the price and the item ID in our persistent database
            DataStorage.addPrice(getFingerprint(stack), finalPrice, stack.getCount());
        }
    }
}