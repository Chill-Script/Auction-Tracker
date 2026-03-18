package com.example.auction;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AuctionTracker implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Load data on startup
        DataStorage.load();
        
        // This is the magic line that runs the bot without using buggy Mixins
        ClientTickEvents.END_CLIENT_TICK.register(AuctionBot::tick);

        // Register the command - This matches the class name in Step 1
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            AuctionCommand.register(dispatcher);
        });
    }
}