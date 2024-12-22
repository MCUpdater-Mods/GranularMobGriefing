package com.mcupdater.granularmobgriefing;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = GranularMobGriefing.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITIES;
    static final ModConfigSpec SPEC;

    static {
        BUILDER = new ModConfigSpec.Builder();
        ENTITIES = BUILDER
                .comment("Entites that are prevented from griefing")
                .defineListAllowEmpty("entities", List.of("minecraft:creeper","minecraft:enderman"), Config::validateEntity);
        SPEC = BUILDER.build();
    }

    private static boolean validateEntity(Object obj) {
        return obj instanceof String entityName && BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse(entityName));
    }

    public static Set<EntityType<?>> entities;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        entities = ENTITIES.get().stream()
                .map(entityId -> BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityId)))
                .collect(Collectors.toSet());
    }

    public static void add(EntityType<?> entityType) {
        entities.add(entityType);
        save();
    }

    public static void remove(EntityType<?> entityType) {
        entities.remove(entityType);
        save();
    }

    private static void save() {
        ENTITIES.set(entities.stream().map(lookup -> EntityType.getKey(lookup).toString()).collect(Collectors.toList()));
        ENTITIES.save();
    }
}
