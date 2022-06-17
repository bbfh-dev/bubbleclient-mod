package me.bubblefish.bubbleclient.mixins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.Util;

@Mixin(Util.OperatingSystem.class)
abstract class OperatingSystemMixin {
    @Shadow
    public abstract void open(URL url);

    /**
     * @author BubbleClient
     * @reason Async
     */
    @Overwrite
    public void open(URI uri) {
        CompletableFuture.runAsync(() -> {
            try {
                this.open(uri.toURL());
            } catch (MalformedURLException e) {
                System.err.println("Couldn't open uri '" + uri + "'");
                e.printStackTrace();
            }
        });
    }

    /**
     * @author BubbleClient
     * @reason Async
     */
    @Overwrite
    public void open(File file) {
        CompletableFuture.runAsync(() -> {
            try {
                this.open(file.toURI().toURL());
            } catch (MalformedURLException e) {
                System.err.println("Couldn't open file '" + file + "'");
                e.printStackTrace();
            }
        });
    }
}