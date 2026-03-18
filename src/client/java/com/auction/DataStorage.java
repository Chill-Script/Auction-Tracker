package com.example.auction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("auction_trends.json");
    
    private static Map<String, List<Double>> priceData = new HashMap<>();
    private static Map<String, Integer> currentSessionCount = new HashMap<>();
    private static String lastScanTime = "Never";

    public static void load() {
        File file = PATH.toFile();
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            StorageWrapper wrapper = GSON.fromJson(reader, StorageWrapper.class);
            if (wrapper != null) {
                priceData = (wrapper.prices != null) ? wrapper.prices : new HashMap<>();
                lastScanTime = (wrapper.timestamp != null) ? wrapper.timestamp : "Never";
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void save() {
        lastScanTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        try (Writer writer = new FileWriter(PATH.toFile())) {
            GSON.toJson(new StorageWrapper(priceData, lastScanTime), writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void recordPrice(String item, double price, String seller) {
        priceData.computeIfAbsent(item, k -> new ArrayList<>()).add(price);
        currentSessionCount.put(item, currentSessionCount.getOrDefault(item, 0) + 1);
    }

    public static void clearSessionCount() {
        currentSessionCount.clear();
        priceData.clear(); 
    }

    // THE COMPILER NEEDS THIS SPECIFIC METHOD:
    public static String getLastScanTime() {
        return lastScanTime;
    }

    public static int getCurrentCount(String id) {
        return currentSessionCount.getOrDefault(id, 0);
    }

    public static double getMin(String id) {
        List<Double> prices = priceData.get(id);
        return (prices == null || prices.isEmpty()) ? 0 : Collections.min(prices);
    }

    public static double getMax(String id) {
        List<Double> prices = priceData.get(id);
        return (prices == null || prices.isEmpty()) ? 0 : Collections.max(prices);
    }

    private static class StorageWrapper {
        Map<String, List<Double>> prices;
        String timestamp;
        StorageWrapper(Map<String, List<Double>> p, String t) {
            this.prices = p;
            this.timestamp = t;
        }
    }
}