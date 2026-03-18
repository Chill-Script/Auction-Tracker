package com.example.auction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class DataStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("auction_trends.json");
    private static Map<String, List<Double>> priceData = new HashMap<>();

    public static void recordPrice(String item, double price, String seller) {
        priceData.computeIfAbsent(item, k -> new ArrayList<>()).add(price);
        save();
    }

    public static double getMyPrice(String item) {
        // In a real scenario, you'd track your specific listings here
        return -1; 
    }

    private static void save() {
        try (Writer writer = new FileWriter(PATH.toFile())) {
            GSON.toJson(priceData, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }
}