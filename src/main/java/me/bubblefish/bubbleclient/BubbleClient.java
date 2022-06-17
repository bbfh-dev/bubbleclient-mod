package me.bubblefish.bubbleclient;

import me.bubblefish.bubbleclient.command.BubbleClientCommand;
import me.bubblefish.bubbleclient.command.ClanCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static me.bubblefish.bubbleclient.config.Config.*;

public class BubbleClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("BubbleClient");
    private static KeyBinding killKeyBinding;
    private static KeyBinding lobbyKeyBinding;
    public static float playerDealtReach = 0.0f;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing BubbleClient");

        new BubbleClientCommand(ClientCommandManager.DISPATCHER);

        killKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "/kill", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "BubbleClient"
        ));

        lobbyKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "/lobby", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "BubbleClient"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (killKeyBinding.wasPressed()) {
                client.player.sendChatMessage("/kill");
            }

            if (lobbyKeyBinding.wasPressed()) {
                client.player.sendChatMessage("/lobby");
            }
        });

        CompletableFuture.runAsync(() -> {
            HashMap<UUID, Byte> onlinePlayersMap = fetch_players_map();
            HashMap<Byte, String> onlineClansMap = fetch_clans_map();
            HashMap<UUID, Byte> offlinePlayersMap = fetchConfigPlayersMap();
            HashMap<Byte, String> offlineClansMap = fetchConfigClansMap();

            offlinePlayersMap.putAll(onlinePlayersMap);
            offlineClansMap.putAll(onlineClansMap);
            setPlayersMap(offlinePlayersMap);
            setClansMap(offlineClansMap);

            HashMap<Byte, Boolean> offlineConfigMap = fetchConfigMap();
            offlineConfigMap.putIfAbsent((byte) 0, true);
            offlineConfigMap.putIfAbsent((byte) 1, true);
            offlineConfigMap.putIfAbsent((byte) 2, true);
            offlineConfigMap.putIfAbsent((byte) 3, false);
            offlineConfigMap.putIfAbsent((byte) 4, true);
            offlineConfigMap.putIfAbsent((byte) 5, true);
            setConfigMap(offlineConfigMap);

            new ClanCommand(ClientCommandManager.DISPATCHER);
        });
    }

    private HashMap<Byte, String> fetch_clans_map() {
        HashMap<Byte, String> fetchedClansMap = new HashMap<>();
        LOGGER.info("Fetching clans in asynchronous...");
        URL url;

        try {
            url = new URL("https://raw.githubusercontent.com/bubblefish-dev/bubbleclient-server/main/clans.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    String[] response = scanner.nextLine().split("=");
                    try {
                        if (response.length > 1)
                            fetchedClansMap.put(
                                    Byte.parseByte(response[0]),
                                    String.valueOf(response[1])
                            );
                    } catch (NumberFormatException ignored) {}
                }

                scanner.close();
            }

            LOGGER.info("Fetched " + fetchedClansMap.size() + " clan names");
        } catch (IOException e) {
            LOGGER.warn("Failed to fetch database:");
            e.printStackTrace();
        }

        return fetchedClansMap;
    }

    private HashMap<UUID, Byte> fetch_players_map() {
        HashMap<UUID, Byte> fetchedPlayersMap = new HashMap<>();
        LOGGER.info("Fetching database in asynchronous...");
        URL url;

        try {
            url = new URL("https://raw.githubusercontent.com/bubblefish-dev/bubbleclient-server/main/players.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    String[] response = scanner.nextLine().split("=");
                    try {
                        if (response.length > 1)
                            fetchedPlayersMap.put(
                                    UUID.fromString(response[0]),
                                    Byte.parseByte(response[1])
                            );
                    } catch (NumberFormatException ignored) {}
                }

                scanner.close();
            }

            LOGGER.info("Fetched " + fetchedPlayersMap.size() + " player names");
        } catch (IOException e) {
            LOGGER.warn("Failed to fetch database:");
            e.printStackTrace();
        }

        return fetchedPlayersMap;
    }
}
