package me.bubblefish.bubbleclient.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;

import static me.bubblefish.bubbleclient.config.Config.modifyConfigProperty;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class BubbleClientCommand {
    public BubbleClientCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("bubbleclient")
                        .then(literal("displayPlayerHealth")
                                .then(literal("true").executes(context -> setConfigProperty(context, (byte) 0, true)))
                                .then(literal("false").executes(context -> setConfigProperty(context, (byte) 0, false)))
                        )
                        .then(literal("displayPlayerClans")
                                .then(literal("true").executes(context -> setConfigProperty(context, (byte) 1, true)))
                                .then(literal("false").executes(context -> setConfigProperty(context, (byte) 1, false)))
                        )
                        .then(literal("grayscaleClanlessPlayers")
                                .then(literal("true").executes(context -> setConfigProperty(context, (byte) 2, true)))
                                .then(literal("false").executes(context -> setConfigProperty(context, (byte) 2, false)))
                        )
                        .then(literal("noCameraShake")
                                .then(literal("true").executes(context -> setConfigProperty(context, (byte) 3, true)))
                                .then(literal("false").executes(context -> setConfigProperty(context, (byte) 3, false)))
                        )
                        .then(literal("displayReach")
                                .then(literal("true").executes(context -> setConfigProperty(context, (byte) 4, true)))
                                .then(literal("false").executes(context -> setConfigProperty(context, (byte) 4, false)))
                        )
                        .then(literal("alwaysSprint")
                                .then(literal("true").executes(context -> setConfigProperty(context, (byte) 5, true)))
                                .then(literal("false").executes(context -> setConfigProperty(context, (byte) 5, false)))
                        )
        );
    }

    private int setConfigProperty(CommandContext<FabricClientCommandSource> context, Byte key, Boolean value) {
        modifyConfigProperty(key, value);
        context.getSource().sendFeedback(new LiteralText("The property was set to " + value));
        return 1;
    }
}
