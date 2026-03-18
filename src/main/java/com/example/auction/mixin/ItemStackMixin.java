package com.example.auction.mixin;

import com.example.auction.AuctionScanner;
import com.example.auction.DataStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void addAuctionTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> tooltip = cir.getReturnValue();
        ItemStack stack = (ItemStack)(Object)this;

        if (stack.isEmpty()) return;

        String id = AuctionScanner.getFingerprint(stack);
        int count = DataStorage.getCurrentCount(id);

        tooltip.add(Text.literal(" ")); // Visual spacer
        tooltip.add(Text.literal("📊 AuctionTracker").formatted(Formatting.GOLD, Formatting.BOLD));

        // Logic: Only show price data if the item was found in the MOST RECENT scan [cite: 2026-03-15]
        if (count > 0) {
            tooltip.add(Text.literal("Amount on AH: ").formatted(Formatting.GRAY)
                .append(Text.literal(String.valueOf(count)).formatted(Formatting.AQUA)));

            String label = (stack.getMaxCount() > 1) ? " (Per Item): " : ": ";

            tooltip.add(Text.literal("Lowest Price" + label).formatted(Formatting.GRAY)
                .append(Text.literal("$" + String.format("%,.0f", DataStorage.getMin(id))).formatted(Formatting.GREEN)));
            
            tooltip.add(Text.literal("Highest Price" + label).formatted(Formatting.GRAY)
                .append(Text.literal("$" + String.format("%,.0f", DataStorage.getMax(id))).formatted(Formatting.RED)));
        } else {
            // Requirement: If count is 0 after a scan, show this specific message [cite: 2026-03-15]
            tooltip.add(Text.literal("Item was not in AH last scan").formatted(Formatting.RED));
        }
    }
}