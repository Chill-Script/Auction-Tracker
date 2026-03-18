package com.example.auction;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AuctionCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("at")
            .then(ClientCommandManager.literal("reset")
                .executes(context -> {
                    // This calls the wipe method we added to DataStorage
                    DataStorage.wipeAllData();
                    
                    context.getSource().sendFeedback(Text.literal("§6[AuctionTracker] §aAll historical data has been wiped."));
                    return 1;
                })
            )
        );
    }
}