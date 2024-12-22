package com.mcupdater.granularmobgriefing;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(GranularMobGriefing.MODID)
public class GranularMobGriefing
{
    public static final String MODID = "granularmobgriefing";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GranularMobGriefing(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void blockMobGriefing(EntityMobGriefingEvent event) {
        Entity culprit = event.getEntity();
        if (Config.entities.contains(culprit.getType())) {
            event.setCanGrief(false);
        }
    }

    @SubscribeEvent
    public void serverStarted(final ServerStartedEvent event) {
        LOGGER.info("Listing known entity ids");
        BuiltInRegistries.ENTITY_TYPE.forEach(type -> {
            GranularMobGriefing.LOGGER.info("Entity id: {}", EntityType.getKey(type));
        });
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        GMGCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}
