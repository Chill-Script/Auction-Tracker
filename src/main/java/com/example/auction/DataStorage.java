package com.example.auction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataStorage {
    private static Map<String, List<Double>> priceData = new HashMap<>();
    private static Map<String, Integer> currentSessionCount = new HashMap<>();
    private static Map<String, Long> lastSeenTimestamp = new HashMap<>();
    
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("auction_data.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        try {
            File file = CONFIG_PATH.toFile();
            if (!file.exists()) return;
            try (Reader reader = new FileReader(file)) {
                Map<String, List<Double>> data = GSON.fromJson(reader, new TypeToken<Map<String, List<Double>>>(){}.getType());
                if (data != null) priceData = data;
            }
        } catch (Exception ignored) {}
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(priceData, writer);
        } catch (Exception ignored) {}
    }

    public static void addPrice(String id, double price, int amount) {
        priceData.computeIfAbsent(id, k -> new ArrayList<>()).add(price);
        currentSessionCount.put(id, currentSessionCount.getOrDefault(id, 0) + amount);
        lastSeenTimestamp.put(id, System.currentTimeMillis());
    }

    public static void clearSessionCount() { currentSessionCount.clear(); }
    public static int getCurrentCount(String id) { return currentSessionCount.getOrDefault(id, 0); }
    
    public static String getLastSeenDate(String id) {
        Long ts = lastSeenTimestamp.get(id);
        return (ts == null) ? "Never" : new SimpleDateFormat("MMM dd, HH:mm").format(new Date(ts));
    }

    public static double getMin(String id) {
        List<Double> prices = priceData.get(id);
        return (prices == null || prices.isEmpty()) ? 0 : Collections.min(prices);
    }

    public static double getMax(String id) {
        List<Double> prices = priceData.get(id);
        return (prices == null || prices.isEmpty()) ? 0 : Collections.max(prices);
    }
    
    public static List<Double> getItemHistory(String id) { return priceData.get(id); }
}