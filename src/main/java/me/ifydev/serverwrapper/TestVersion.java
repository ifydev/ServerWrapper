package me.ifydev.serverwrapper;

import net.minecraft.DetectedVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = DetectedVersion.class, remap = false)
public abstract class TestVersion {

    @Shadow @Final private String name;

    @Overwrite
    public String getName() {
        return name + "/ServerWrapper";
    }
}
