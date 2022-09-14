package coda.breezy;

import coda.breezy.common.WindDirectionSavedData;
import coda.breezy.common.entities.HotAirBalloonEntity;
import coda.breezy.registry.BreezyEntities;
import coda.breezy.registry.BreezyItems;
import coda.breezy.registry.BreezyParticles;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// todo- add gust gauge, add sand bags
@Mod(Breezy.MOD_ID)
public class Breezy {
    public static final String MOD_ID = "breezy";
    public static final Logger LOGGER = LogManager.getLogger();

    public Breezy() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        bus.addListener(this::registerEntityAttributes);

        forgeBus.addListener(this::addWindParticles);
        forgeBus.addListener(this::resetWindDirection);

        forgeBus.register(this);

        BreezyParticles.PARTICLES.register(bus);
        BreezyEntities.ENTITIES.register(bus);
        BreezyItems.ITEMS.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BreezyConfig.Client.SPEC);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent e) {
        e.put(BreezyEntities.HOT_AIR_BALLOON.get(), HotAirBalloonEntity.createAttributes().build());
    }

    private void resetWindDirection(TickEvent.WorldTickEvent e) {
        Level world = e.world;

        if (world.getDayTime() % 24000 == 0) {
            WindDirectionSavedData.resetWindDirection(world.random);
        }

    }

    private void addWindParticles(BiomeLoadingEvent e) {
        BiomeSpecialEffects baseEffects = e.getEffects();

        BiomeSpecialEffects defaultEffects = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(BreezyParticles.WIND.get(), 0.0002F)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects lowWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.lowWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects mediumWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.mediumWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();
        BiomeSpecialEffects highWind = new BiomeSpecialEffects.Builder().ambientParticle(new AmbientParticleSettings(BreezyParticles.WIND.get(), BreezyConfig.Client.INSTANCE.highWindFrequency.get().floatValue() * 1000)).fogColor(baseEffects.getFogColor()).skyColor(baseEffects.getSkyColor()).waterColor(baseEffects.getWaterColor()).waterFogColor(baseEffects.getWaterFogColor()).build();

        Biome.BiomeCategory category = e.getCategory();

        if (category == Biome.BiomeCategory.NETHER || category == Biome.BiomeCategory.THEEND) {
            return;
        }
        if (BreezyConfig.Client.INSTANCE.shouldDisplayWind.get()) {
            switch (category) {
                // LOW
                case FOREST: e.setEffects(lowWind);
                case TAIGA: e.setEffects(lowWind);
                case DESERT: e.setEffects(lowWind);
                // MEDIUM
                case PLAINS: e.setEffects(mediumWind);
                case SAVANNA: e.setEffects(mediumWind);
                // HIGH
                case MOUNTAIN: e.setEffects(highWind);
                case EXTREME_HILLS: e.setEffects(highWind);
                case ICY: e.setEffects(highWind);

                default: e.setEffects(defaultEffects);
            }
        }
    }
}