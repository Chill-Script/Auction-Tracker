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
    
    // Core data maps: These hold your historical prices, session counts, and specific dates
    private static Map<String, List<Double>> priceData = new HashMap<>();
    private static Map<String, Integer> currentSessionCount = new HashMap<>();
    private static Map<String, String> itemLastSeenMap = new HashMap<>();
    private static String lastScanTime = "Never";

    /**
     * Loads the auction_trends.json file from the config folder.
     * This ensures data survives game restarts and PC reboots.
     */
    public static void load() {
        File file = PATH.toFile();
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            StorageWrapper wrapper = GSON.fromJson(reader, StorageWrapper.class);
            if (wrapper != null) {
                priceData = (wrapper.prices != null) ? wrapper.prices : new HashMap<>();
                lastScanTime = (wrapper.timestamp != null) ? wrapper.timestamp : "Never";
                itemLastSeenMap = (wrapper.itemLastSeen != null) ? wrapper.itemLastSeen : new HashMap<>();
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Saves all current data to the hard drive.
     * Called at the end of every successful scan.
     */
    public static void save() {
        lastScanTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        try (Writer writer = new FileWriter(PATH.toFile())) {
            GSON.toJson(new StorageWrapper(priceData, lastScanTime, itemLastSeenMap), writer);
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Records a new price for an item.
     * Updates historical list, current session count, and item-specific timestamp.
     */
    public static void addPrice(String item, double price, int stackSize) {
        // Keep the full history of prices for Min/Max calculations
        priceData.computeIfAbsent(item, k -> new ArrayList<>()).add(price);
        
        // Track how many are on the AH right now (Session specific)
        currentSessionCount.put(item, currentSessionCount.getOrDefault(item, 0) + 1);
        
        // Update the 'Last Seen' date for this specific item to right now
        itemLastSeenMap.put(item, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
    }

    /**
     * Completely clears all history, session data, and timestamps.
     * Then saves the empty state to the hard drive to overwrite the file.
     */
    public static void wipeAllData() {
        priceData.clear();
        currentSessionCount.clear();
        itemLastSeenMap.clear();
        lastScanTime = "Never";
        save(); // Overwrite the JSON file with empty data
    }

    public static String getLastScanTime() {
        return lastScanTime;
    }

    public static String getLastSeenDate(String id) {
        return itemLastSeenMap.getOrDefault(id, "Unknown");
    }

    /**
     * Resets the 'Amount on AH' count before a new scan starts.
     * Crucially, this DOES NOT clear historical prices.
     */
    public static void clearSessionCount() {
        currentSessionCount.clear(); 
    }

    public static int getCurrentCount(String id) {
        return currentSessionCount.getOrDefault(id, 0);
    }

    public static List<Double> getItemHistory(String id) {
        return priceData.getOrDefault(id, new ArrayList<>());
    }

    public static double getMin(String id) {
        List<Double> prices = priceData.get(id);
        if (prices == null || prices.isEmpty()) return 0;
        return Collections.min(prices);
    }

    public static double getMax(String id) {
        List<Double> prices = priceData.get(id);
        if (prices == null || prices.isEmpty()) return 0;
        return Collections.max(prices);
    }

    /**
     * Internal class used by GSON to structure the JSON file correctly.
     */
    private static class StorageWrapper {
        Map<String, List<Double>> prices;
        String timestamp;
        Map<String, String> itemLastSeen;
        
        StorageWrapper(Map<String, List<Double>> p, String t, Map<String, String> ils) {
            this.prices = p;
            this.timestamp = t;
            this.itemLastSeen = ils;
        }
    }
}