package com.zanckor.questapi;

import com.mojang.logging.LogUtils;
import com.zanckor.questapi.network.QuestNetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(QuestApi.MOD_ID)
public class QuestApi
{
    public static final String MOD_ID = "questapi";
    public static final Logger LOGGER = LogUtils.getLogger();

    public QuestApi() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        QuestNetworkHandler.register();

        MinecraftForge.EVENT_BUS.register(this);
    }
}
