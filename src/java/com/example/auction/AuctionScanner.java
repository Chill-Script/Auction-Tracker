package com.example.auction;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import java.util.List;

public class AuctionScanner {
    public static void scanSlot(ItemStack stack, String myUsername) {
        if (stack.isEmpty()) return;

        List<Text> lore = stack.getLore().getLines();
        double price = -1;
        String seller = "";

        for (Text line : lore) {
            String text = line.getString();
            if (text.contains("Price:")) {
                String numeric = text.replaceAll("[^0-9.]", "");
                if (!numeric.isEmpty()) price = Double.parseDouble(numeric);
            }
            if (text.contains("Seller:")) {
                seller = text.split(":")[1].trim();
            }
        }

        if (price > 0) {
            DataStorage.recordPrice(stack.getName().getString(), price, seller);
        }
    }
}