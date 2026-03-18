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
    private static boolean isScanFinished = false;
    private static boolean sessionStarted = false;
    private static int lastScannedPage = -1; 
    private static final Random random = new Random();
    private static final Pattern PAGE_PATTERN = Pattern.compile("All (\\d+)/(\\d+)");

    public static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null || !(client.currentScreen instanceof HandledScreen<?> screen)) {
            reset();
            return;
        }

        if (isScanFinished) return;
        String title = screen.getTitle().getString();

        if (title.contains("AuctionHouse - All")) {
            Matcher matcher = PAGE_PATTERN.matcher(title);
            int currentPage = -1;
            int totalPages = -1;

            if (matcher.find()) {
                currentPage = Integer.parseInt(matcher.group(1));
                totalPages = Integer.parseInt(matcher.group(2));
            }

            if (!sessionStarted) {
                DataStorage.clearSessionCount();
                sessionStarted = true;
                lastScannedPage = -1; 
            }

            // PAGE LOCK: Only scan if this is a new page number
            if (currentPage != -1 && currentPage != lastScannedPage) {
                ScreenHandler handler = screen.getScreenHandler();
                for (int i = 0; i < 45; i++) {
                    ItemStack stack = handler.getSlot(i).getStack();
                    if (!stack.isEmpty()) {
                        AuctionScanner.scanSlot(stack);
                    }
                }
                lastScannedPage = currentPage; 
            }

            if (currentPage != -1 && totalPages != -1 && currentPage >= totalPages) {
                finishScan(client);
                return;
            }

            if (scanDelay > 0) {
                scanDelay--;
                return;
            }

            int nextBtn = -1;
            ScreenHandler handler = screen.getScreenHandler();
            for (int i = 0; i < handler.slots.size(); i++) {
                String name = handler.getSlot(i).getStack().getName().getString().toLowerCase();
                if (name.contains("next") || name.contains("▶")) {
                    nextBtn = i;
                    break;
                }
            }

            if (nextBtn != -1) {
                client.interactionManager.clickSlot(handler.syncId, nextBtn, 0, SlotActionType.PICKUP, client.player);
                scanDelay = 80 + random.nextInt(40);
            } else {
                finishScan(client);
            }
        }
    }

    private static void finishScan(MinecraftClient client) {
        if (!isScanFinished) {
            isScanFinished = true;
            DataStorage.save();
            client.player.sendMessage(Text.literal("§b§l[AuctionTracker] §fScan complete! Quantity counts are now accurate."), false);
        }
    }

    private static void reset() {
        isScanFinished = false;
        sessionStarted = false;
        lastScannedPage = -1;
        scanDelay = 0;
    }
}