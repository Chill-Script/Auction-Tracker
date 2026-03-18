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
        // Safety check: ensure we are in a valid world state
        if (player == null) return;

        List<Text> tooltip = cir.getReturnValue();
        ItemStack stack = (ItemStack)(Object)this;

        if (stack.isEmpty()) return;

        // Get fingerprint, session count, and historical prices from DataStorage
        String id = AuctionScanner.getFingerprint(stack);
        int count = DataStorage.getCurrentCount(id);
        double minPrice = DataStorage.getMin(id);
        double maxPrice = DataStorage.getMax(id);
        String globalScanTime = DataStorage.getLastScanTime();

        tooltip.add(Text.literal(" ")); // Visual spacer
        tooltip.add(Text.literal("📊 AuctionTracker").formatted(Formatting.GOLD, Formatting.BOLD));

        // STATE 1: User has NEVER performed a scan since installing the mod
        if (globalScanTime.equals("Never")) {
            tooltip.add(Text.literal("Please scan AH first").formatted(Formatting.RED));
        } 
        
        // STATE 2: Item HAS history (Found in this scan or a previous one)
        else if (minPrice > 0) {
            
            // If count is 0, it wasn't found in the LATEST scan specifically
            if (count == 0) {
                tooltip.add(Text.literal("Item was not in AH last scan").formatted(Formatting.RED));
            } else {
                // Display the amount found in the current active scan
                tooltip.add(Text.literal("Amount on AH: ").formatted(Formatting.GRAY)
                    .append(Text.literal(String.valueOf(count)).formatted(Formatting.AQUA)));
            }

            // Label adjustment for single vs stackable items
            String label = (stack.getMaxCount() > 1) ? " (Per Item): " : ": ";

            // Prices remain visible across restarts
            tooltip.add(Text.literal("Lowest Price" + label).formatted(Formatting.GRAY)
                .append(Text.literal("$" + String.format("%,.0f", minPrice)).formatted(Formatting.GREEN)));
            
            tooltip.add(Text.literal("Highest Price" + label).formatted(Formatting.GRAY)
                .append(Text.literal("$" + String.format("%,.0f", maxPrice)).formatted(Formatting.RED)));

            // Shows the specific time this exact item was last detected
            tooltip.add(Text.literal("Item last seen in AH: ").formatted(Formatting.YELLOW)
                .append(Text.literal(DataStorage.getLastSeenDate(id)).formatted(Formatting.YELLOW)));
                
        } 
        
        // STATE 3: A global scan happened, but this item was not included in it or any previous scans
        else {
            tooltip.add(Text.literal("Item was not in AH last scan").formatted(Formatting.RED));
        }

        // Global footer showing the time of the last full scan execution
        tooltip.add(Text.literal("Last Scanned: ").formatted(Formatting.DARK_GRAY)
            .append(Text.literal(globalScanTime).formatted(Formatting.DARK_GRAY)));
    }
}