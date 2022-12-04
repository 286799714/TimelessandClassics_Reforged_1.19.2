package com.tac.guns;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


@Mod.EventBusSubscriber
@Mod(Reference.MOD_ID)
public class GunMod
{
    public GunMod() throws IOException {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static String loadResource(ResourceLocation resourceLocation) throws IOException {
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation).get();
        InputStream inputStream = resource.open();
        return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @SubscribeEvent
    public void onGameOverlayRender(RenderArmEvent event){

    }
}
