package com.example.auction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuctionBot {
    private static int scanDelay = 0;
    private static int slotIterator = 0; // Tracks which slot we are currently scanning
    private static boolean isScanFinished = false;
    private static boolean sessionStarted = false;
    private static int lastScannedPage = -1; 
    private static final Random random = new Random();
    private static final Pattern PAGE_PATTERN = Pattern.compile("All (\\d+)/(\\d+)");

    public static void tick(MinecraftClient client) {
        // Reset state if we close the screen or leave the world
        if (client.world == null || client.player == null || !(client.currentScreen instanceof HandledScreen<?> screen)) {
            reset();
            return;
        }

        if (isScanFinished) return;
        String title = screen.getTitle().getString();

        // Ensure we are in the correct Auction House menu
        if (title.contains("AuctionHouse - All")) {
            Matcher matcher = PAGE_PATTERN.matcher(title);
            int currentPage = -1;
            int totalPages = -1;

            if (matcher.find()) {
                currentPage = Integer.parseInt(matcher.group(1));
                totalPages = Integer.parseInt(matcher.group(2));
            }

            // Start of a new scan session
            if (!sessionStarted) {
                DataStorage.clearSessionCount(); // Wipes current counts but keeps history
                sessionStarted = true;
                lastScannedPage = -1; 
                slotIterator = 0;
            }

            // --- PER-TICK SCANNING LOGIC ---
            // This processes one slot every game tick (1/20th of a second) to ensure accuracy
            if (currentPage != -1 && currentPage != lastScannedPage) {
                ScreenHandler handler = screen.getScreenHandler();
                
                if (slotIterator < 45) {
                    ItemStack stack = handler.getSlot(slotIterator).getStack();
                    if (!stack.isEmpty()) {
                        AuctionScanner.scanSlot(stack);
                    }
                    slotIterator++;
                    return; // Stop here and wait for the next tick for the next slot
                }
                
                // Once all 45 slots are scanned, mark this page as done
                lastScannedPage = currentPage;
                slotIterator = 0; 
            }

            // Finish the scan if we have reached the last page and finished its slots
            if (currentPage != -1 && totalPages != -1 && currentPage >= totalPages && slotIterator == 0) {
                finishScan(client);
                return;
            }

            // Handle the delay between clicking "Next Page"
            if (scanDelay > 0) {
                scanDelay--;
                return;
            }

            // --- NEXT PAGE NAVIGATION ---
            int nextBtn = -1;
            ScreenHandler handler = screen.getScreenHandler();
            for (int i = 0; i < handler.slots.size(); i++) {
                String name = handler.getSlot(i).getStack().getName().getString().toLowerCase();
                if (name.contains("next") || name.contains("▶")) {
                    nextBtn = i;
                    break;
                }
            }

            if (nextBtn != -1 && slotIterator == 0) {
                client.interactionManager.clickSlot(handler.syncId, nextBtn, 0, SlotActionType.PICKUP, client.player);
                // Random delay to mimic human behavior and allow server to load lore
                scanDelay = 80 + random.nextInt(40); 
            } else if (slotIterator == 0) {
                finishScan(client);
            }
        }
    }

    private static void finishScan(MinecraftClient client) {
        if (!isScanFinished) {
            isScanFinished = true;
            DataStorage.save(); // Save everything to auction_trends.json
            client.player.sendMessage(Text.literal("§b§l[AuctionTracker] §fScan complete! Quantity counts are now accurate."), false);
        }
    }

    private static void reset() {
        isScanFinished = false;
        sessionStarted = false;
        lastScannedPage = -1;
        scanDelay = 0;
        slotIterator = 0;
    }
}