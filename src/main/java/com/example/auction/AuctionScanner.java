package com.example.auction;

import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuctionScanner {
    public static void scanSlot(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore == null) return;

        for (Text line : lore.lines()) {
            String text = line.getString().replace(",", "").replace("$", "").toLowerCase();
            Pattern p = Pattern.compile("(?:price|buy it now|cost|bid):?\\s*(\\d+)");
            Matcher m = p.matcher(text);

            if (m.find()) {
                try {
                    double totalPrice = Double.parseDouble(m.group(1));
                    int stackSize = stack.getCount();
                    
                    // Logic: If the item can stack, we calculate price per item.
                    // If it's gear (max count 1), the price remains the total price.
                    double finalPrice = (stack.getMaxCount() > 1) ? (totalPrice / stackSize) : totalPrice;
                    
                    DataStorage.addPrice(getFingerprint(stack), finalPrice, stackSize);
                } catch (Exception ignored) {}
            }
        }
    }

    public static String getFingerprint(ItemStack stack) {
        String name = stack.getName().getString();
        // Keep unique names like "Vote Token" but ignore stack sizes in the name
        return stack.getItem().toString() + "_" + name;
    }
}