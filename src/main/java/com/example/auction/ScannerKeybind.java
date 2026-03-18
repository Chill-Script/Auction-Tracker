package com.example.auction;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class ScannerKeybind {
    private static KeyBinding scanKey;

    public static void register() {
        // This is the method the error was complaining about!
        scanKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auctiontracker.scan", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_J, 
                "category.auctiontracker"
        ));
    }

    public static void clickNextPage(MinecraftClient client, HandledScreen<?> screen) {
        // Scans the bottom navigation row (slots 45-53)
        for (int i = 45; i < screen.getScreenHandler().slots.size(); i++) {
            ItemStack stack = screen.getScreenHandler().getSlot(i).getStack();
            if (stack.isEmpty()) continue;

            String name = stack.getName().getString().toLowerCase();
            // Matches your Photo 2 "Next Page" button
            if (name.contains("next page")) {
                if (client.interactionManager != null) {
                    client.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.PICKUP, client.player);
                }
                return;
            }
        }
    }
}