package me.bubblefish.bubbleclient.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.bubblefish.bubbleclient.config.Config.*;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class ClanCommand {
    public ClanCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = literal("clan");
        LiteralArgumentBuilder<FabricClientCommandSource> argumentBuilder = literal("set");
        for (String clan : getClanList())
            argumentBuilder = argumentBuilder.then(
                    argument("Player name", EntityArgumentType.player())
                            .then(
                                    literal(clan)
                                            .executes(context -> setClan(context, clan))
                            )
            );

        dispatcher.register(
                command.then(argumentBuilder)
                        .then(literal("get")
                                .then(argument("Player name", EntityArgumentType.player())
                                        .executes(this::getClan)))
                        .then(literal("reset")
                                .then(argument("Player name", EntityArgumentType.player())
                                        .executes(this::resetClan)))
                        .then(literal("copy_all")
                                .executes(this::copyAll))
        );
    }

    private int resetClan(CommandContext<FabricClientCommandSource> context) {
        String playerName = context.getInput().split(" ")[2];
        HashMap<UUID, Byte> newPlayersMap = getPlayersMap();
        UUID uuid = getPlayerId(playerName);
        if (uuid == null) return 0;
        Byte playerClan = newPlayersMap.get(uuid);

        if (playerClan == null) return 0;

        newPlayersMap.remove(uuid);

        storeConfigPlayersMap(newPlayersMap);
        context.getSource().sendFeedback(new LiteralText("§e" + playerName + "§7's clan was reset"));

        cachedFormattedName.clear();
        return 1;
    }

    private int getClan(CommandContext<FabricClientCommandSource> context) {
        String playerName = context.getInput().split(" ")[2];
        Byte playerClan = getPlayerValue(getPlayerId(playerName));

        if (playerClan == null)
            context.getSource().sendFeedback(new LiteralText(
                    "§e" + playerName + "§7 is §cclanless"));
        else
            context.getSource().sendFeedback(new LiteralText(
                    "§e" + playerName + "§7's clan is §a" + getClanList().get(playerClan)));
        return 1;
    }

    private int setClan(CommandContext<FabricClientCommandSource> context, String clan) {
        String playerName = context.getInput().split(" ")[2];
        HashMap<UUID, Byte> newPlayersMap = getPlayersMap();
        HashMap<String, Byte> newPlayerClan = new HashMap<>();
        UUID uuid = getPlayerId(playerName);
        if (uuid == null) return 0;
        Byte playerClan = newPlayersMap.get(uuid);

        Byte clanByte = -1;
        for (String value : getClansMap().values()) {
            clanByte++;
            String[] strings = value.split(" ");
            newPlayerClan.put(strings[strings.length - 1].replace("(", "").replace(")", ""), clanByte);
        }

        if (playerClan == null)
            newPlayersMap.put(uuid, newPlayerClan.get(clan));
        else
            newPlayersMap.replace(uuid, newPlayerClan.get(clan));

        storeConfigPlayersMap(newPlayersMap);
        context.getSource().sendFeedback(new LiteralText("§e" + playerName + "§7's clan was set to §a" + clan));

        cachedFormattedName.clear();
        return 1;
    }

    private int copyAll(CommandContext<FabricClientCommandSource> context) {
        HashMap<UUID, Byte> playersMap = getPlayersMap();
        if (playersMap == null) return 0;

        StringBuilder contents = new StringBuilder();
        for (UUID key : playersMap.keySet())
            contents.append(
                    key.toString() + "=" + playersMap.get(key) + "\n"
            );

        StringSelection stringSelection = new StringSelection(contents.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        context.getSource().sendFeedback(new LiteralText("Player database was copied to clipboard!").formatted(Formatting.GREEN));
        return 1;
    }
}
