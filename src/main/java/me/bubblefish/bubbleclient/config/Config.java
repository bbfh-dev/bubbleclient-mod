package me.bubblefish.bubbleclient.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Config {
    public static String CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(Path.of("BubbleClient")).toString();
    private static HashMap<UUID, Byte> playersMap = new HashMap<>();
    private static HashMap<Byte, String> clansMap = new HashMap<>();
    private static HashMap<Byte, Boolean> configMap = new HashMap<>();


    public static Byte getPlayerValue(UUID key) {
        return playersMap.get(key);
    }

    public static HashMap<UUID, Byte> getPlayersMap() {
        return playersMap;
    }

    public static HashMap<Byte, String> getClansMap() {
        return clansMap;
    }

    public static HashMap<String, LiteralText> cachedFormattedName = new HashMap<>();

    public static UUID getPlayerId(String name) {
        for (PlayerListEntry entry : Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getPlayerList()) {
            if (entry.getProfile().getName().equals(name)) return entry.getProfile().getId();
        }

        return null;
    }
    public static List<String> getClanList() {
        List<String> output = new ArrayList<>();
        for (String value : clansMap.values()) {
            String[] strings = value.split(" ");
            output.add(strings[strings.length - 1].replace("(", "").replace(")", ""));
        }
        return output;
    }


    public static LiteralText getClanFormat(String playerName, Byte playerClan) {
        LiteralText cached = cachedFormattedName.get(playerName);

        if (cached != null) return cached;

        String playerClanFormatted = getClansMap().get(playerClan);

        if (playerClanFormatted == null) return new LiteralText(playerName);

        LiteralText output = (LiteralText) new LiteralText(playerName + " " + playerClanFormatted.split(" ")[1].replace("(", "").replace(")", "")).formatted(Formatting.BOLD);
        if (playerClanFormatted.contains("§b"))
            output = (LiteralText) output.formatted(Formatting.AQUA);
        else if (playerClanFormatted.contains("§a"))
            output = (LiteralText) output.formatted(Formatting.GREEN);
        else if (playerClanFormatted.contains("§4"))
            output = (LiteralText) output.formatted(Formatting.DARK_RED);
        else if (playerClanFormatted.contains("§c"))
            output = (LiteralText) output.formatted(Formatting.RED);
        else if (playerClanFormatted.contains("§3"))
            output = (LiteralText) output.formatted(Formatting.DARK_AQUA);
        else if (playerClanFormatted.contains("§d"))
            output = (LiteralText) output.formatted(Formatting.LIGHT_PURPLE);
        else if (playerClanFormatted.contains("§9"))
            output = (LiteralText) output.formatted(Formatting.DARK_PURPLE);
        else if (playerClanFormatted.contains("§1"))
            output = (LiteralText) output.formatted(Formatting.DARK_BLUE);
        else if (playerClanFormatted.contains("§6"))
            output = (LiteralText) output.formatted(Formatting.GOLD);
        else if (playerClanFormatted.contains("§e"))
            output = (LiteralText) output.formatted(Formatting.YELLOW);
        else if (playerClanFormatted.contains("§5"))
            output = (LiteralText) output.formatted(Formatting.DARK_GREEN);
        cachedFormattedName.put(playerName, output);
        return output;
    }


    public static void setClansMap(HashMap<Byte, String> newClansMap) {
        clansMap = newClansMap;
        storeConfigClansMap(newClansMap);
    }

    public static void setPlayersMap(HashMap<UUID, Byte> newPlayersMap) {
        playersMap = newPlayersMap;
        storeConfigPlayersMap(newPlayersMap);
    }

    public static void setConfigMap(HashMap<Byte, Boolean> newConfigMap) {
        configMap = newConfigMap;
    }

    public static void modifyConfigProperty(Byte key, Boolean value) {
        configMap.replace(key, value);
        storeConfigMap(configMap);
    }

    public static Boolean getConfigMapValue(Byte key) {
        Boolean output = configMap.get(key);
        return output != null && output;
    }

    public static void storeConfigPlayersMap(HashMap<UUID, Byte> newPlayersMap) {
        if (newPlayersMap == null) return;

        List<String> contents = new ArrayList<>();
        for (UUID key : newPlayersMap.keySet())
            contents.add(
                    key.toString() + "=" + newPlayersMap.get(key)
            );
        writeToFile(getConfigPath("players.txt"), contents);
    }

    public static void storeConfigClansMap(HashMap<Byte, String> newClansMap) {
        List<String> contents = new ArrayList<>();
        for (Byte key : newClansMap.keySet())
            contents.add(
                    key + "=" + newClansMap.get(key)
            );
        writeToFile(getConfigPath("clans.txt"), contents);
    }

    public static void storeConfigMap(HashMap<Byte, Boolean> newConfigMap) {
        List<String> contents = new ArrayList<>();
        for (Byte key : newConfigMap.keySet())
            contents.add(
                    key + "=" + newConfigMap.get(key)
            );
        writeToFile(getConfigPath("config.txt"), contents);
    }

    public static HashMap<UUID, Byte> fetchConfigPlayersMap() {
        List<String> lines = readFile(getConfigPath("players.txt"));
        HashMap<UUID, Byte> output = new HashMap<>();

        for (String line : lines) {
            try {
                output.put(
                        UUID.fromString(line.split("=")[0]),
                        Byte.valueOf(line.split("=")[1])
                );
            } catch (NumberFormatException ignored) {}
        }

        return output;
    }

    public static HashMap<Byte, String> fetchConfigClansMap() {
        List<String> lines = readFile(getConfigPath("clans.txt"));
        HashMap<Byte, String> output = new HashMap<>();

        for (String line : lines) {
            try {
                output.put(
                        Byte.valueOf(line.split("=")[0]),
                        line.split("=")[1]
                );
            } catch (NumberFormatException ignored) {}
        }

        return output;
    }

    public static HashMap<Byte, Boolean> fetchConfigMap() {
        List<String> lines = readFile(getConfigPath("config.txt"));
        HashMap<Byte, Boolean> output = new HashMap<>();

        for (String line : lines) {
            try {
                output.put(
                        Byte.valueOf(line.split("=")[0]),
                        Boolean.valueOf(line.split("=")[1])
                );
            } catch (NumberFormatException ignored) {}
        }

        return output;
    }

    private static String getConfigPath(String path) {
        return FabricLoader.getInstance().getConfigDir().resolve(Path.of("BubbleClient", path)).toString();
    }


    private static void writeToFile(String path, List<String> lines) {
        try {
            FileWriter file = new FileWriter(path);
            for (String line : lines) file.write(line + System.getProperty("line.separator"));
            file.flush();
            file.close();
        } catch (IOException ignored) {}
    }


    private static List<String> readFile(String path) {
        List<String> output = new ArrayList<>();
        String line;

        try {
            File file = new File(path);
            if (!file.exists()) {
                new File(CONFIG_DIR).mkdirs();
                file.createNewFile();
            }

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                output.add(line);
            }

            fileReader.close();
            bufferedReader.close();

        } catch (IOException ignored) {}

        return output;
    }
}
