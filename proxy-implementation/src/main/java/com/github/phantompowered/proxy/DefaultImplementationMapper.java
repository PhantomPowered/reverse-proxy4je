/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy;

import com.github.phantompowered.proxy.account.BasicProvidedSessionService;
import com.github.phantompowered.proxy.api.ImplementationMapper;
import com.github.phantompowered.proxy.api.block.BlockAccess;
import com.github.phantompowered.proxy.api.block.BlockState;
import com.github.phantompowered.proxy.api.block.BlockStateRegistry;
import com.github.phantompowered.proxy.api.command.CommandContainer;
import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.configuration.Configuration;
import com.github.phantompowered.proxy.api.connection.*;
import com.github.phantompowered.proxy.api.database.object.DatabaseObject;
import com.github.phantompowered.proxy.api.database.object.DefaultDatabaseObject;
import com.github.phantompowered.proxy.api.entity.EntityEffect;
import com.github.phantompowered.proxy.api.entity.PlayerInfo;
import com.github.phantompowered.proxy.api.entity.PlayerSkinConfiguration;
import com.github.phantompowered.proxy.api.entity.types.*;
import com.github.phantompowered.proxy.api.entity.types.block.EnderCrystal;
import com.github.phantompowered.proxy.api.entity.types.block.FallingBlock;
import com.github.phantompowered.proxy.api.entity.types.block.TNTPrimed;
import com.github.phantompowered.proxy.api.entity.types.item.*;
import com.github.phantompowered.proxy.api.entity.types.living.EntityLiving;
import com.github.phantompowered.proxy.api.entity.types.living.Flying;
import com.github.phantompowered.proxy.api.entity.types.living.animal.Animal;
import com.github.phantompowered.proxy.api.entity.types.living.animal.Bat;
import com.github.phantompowered.proxy.api.entity.types.living.animal.Squid;
import com.github.phantompowered.proxy.api.entity.types.living.animal.WaterAnimal;
import com.github.phantompowered.proxy.api.entity.types.living.animal.ageable.*;
import com.github.phantompowered.proxy.api.entity.types.living.animal.npc.NPC;
import com.github.phantompowered.proxy.api.entity.types.living.animal.npc.Villager;
import com.github.phantompowered.proxy.api.entity.types.living.animal.tamable.Ocelot;
import com.github.phantompowered.proxy.api.entity.types.living.animal.tamable.Tameable;
import com.github.phantompowered.proxy.api.entity.types.living.animal.tamable.Wolf;
import com.github.phantompowered.proxy.api.entity.types.living.boss.Boss;
import com.github.phantompowered.proxy.api.entity.types.living.boss.EnderDragon;
import com.github.phantompowered.proxy.api.entity.types.living.boss.Wither;
import com.github.phantompowered.proxy.api.entity.types.living.creature.Creature;
import com.github.phantompowered.proxy.api.entity.types.living.creature.Golem;
import com.github.phantompowered.proxy.api.entity.types.living.creature.IronGolem;
import com.github.phantompowered.proxy.api.entity.types.living.creature.Snowman;
import com.github.phantompowered.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.phantompowered.proxy.api.entity.types.living.monster.*;
import com.github.phantompowered.proxy.api.entity.types.minecart.CommandBlockMinecart;
import com.github.phantompowered.proxy.api.entity.types.minecart.FurnaceMinecart;
import com.github.phantompowered.proxy.api.entity.types.minecart.Minecart;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.event.ListenerContainer;
import com.github.phantompowered.proxy.api.item.*;
import com.github.phantompowered.proxy.api.network.channel.NetworkChannel;
import com.github.phantompowered.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.phantompowered.proxy.api.network.registry.handler.PacketHandlerRegistryEntry;
import com.github.phantompowered.proxy.api.network.registry.packet.PacketRegistry;
import com.github.phantompowered.proxy.api.network.registry.packet.PacketRegistryEntry;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.paste.PasteServer;
import com.github.phantompowered.proxy.api.paste.PasteServerProvider;
import com.github.phantompowered.proxy.api.paste.PasteServerUploadResult;
import com.github.phantompowered.proxy.api.permission.PermissionHolder;
import com.github.phantompowered.proxy.api.ping.ServerPingProvider;
import com.github.phantompowered.proxy.api.player.OfflinePlayer;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.PlayerAbilities;
import com.github.phantompowered.proxy.api.player.PlayerRepository;
import com.github.phantompowered.proxy.api.player.id.PlayerIdStorage;
import com.github.phantompowered.proxy.api.player.inventory.PlayerInventory;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginManager;
import com.github.phantompowered.proxy.api.potion.PotionEffect;
import com.github.phantompowered.proxy.api.scoreboard.Objective;
import com.github.phantompowered.proxy.api.scoreboard.Score;
import com.github.phantompowered.proxy.api.scoreboard.Scoreboard;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.service.ServiceRegistryEntry;
import com.github.phantompowered.proxy.api.session.ProvidedSessionService;
import com.github.phantompowered.proxy.api.tick.TickHandlerProvider;
import com.github.phantompowered.proxy.block.DefaultBlockAccess;
import com.github.phantompowered.proxy.block.DefaultBlockState;
import com.github.phantompowered.proxy.block.DefaultBlockStateRegistry;
import com.github.phantompowered.proxy.command.DefaultCommandContainer;
import com.github.phantompowered.proxy.command.DefaultCommandMap;
import com.github.phantompowered.proxy.configuration.JsonConfiguration;
import com.github.phantompowered.proxy.connection.*;
import com.github.phantompowered.proxy.connection.cache.TimedEntityEffect;
import com.github.phantompowered.proxy.connection.player.*;
import com.github.phantompowered.proxy.connection.player.scoreboard.BasicObjective;
import com.github.phantompowered.proxy.connection.player.scoreboard.BasicScore;
import com.github.phantompowered.proxy.connection.player.scoreboard.BasicScoreboard;
import com.github.phantompowered.proxy.connection.player.scoreboard.BasicTeam;
import com.github.phantompowered.proxy.connection.player.scoreboard.minecraft.Team;
import com.github.phantompowered.proxy.connection.whitelist.DefaultWhitelist;
import com.github.phantompowered.proxy.entity.*;
import com.github.phantompowered.proxy.entity.types.ProxyAmbient;
import com.github.phantompowered.proxy.entity.types.ProxyExperienceOrb;
import com.github.phantompowered.proxy.entity.types.ProxyHanging;
import com.github.phantompowered.proxy.entity.types.block.ProxyEnderCrystal;
import com.github.phantompowered.proxy.entity.types.block.ProxyFallingBlock;
import com.github.phantompowered.proxy.entity.types.block.ProxyTNTPrimed;
import com.github.phantompowered.proxy.entity.types.item.*;
import com.github.phantompowered.proxy.entity.types.living.ProxyEntityLiving;
import com.github.phantompowered.proxy.entity.types.living.animal.ProxyAnimal;
import com.github.phantompowered.proxy.entity.types.living.animal.ProxyBat;
import com.github.phantompowered.proxy.entity.types.living.animal.ProxySquid;
import com.github.phantompowered.proxy.entity.types.living.animal.ProxyWaterAnimal;
import com.github.phantompowered.proxy.entity.types.living.animal.ageable.*;
import com.github.phantompowered.proxy.entity.types.living.animal.npc.ProxyNPC;
import com.github.phantompowered.proxy.entity.types.living.animal.npc.ProxyVillager;
import com.github.phantompowered.proxy.entity.types.living.animal.tamable.ProxyOcelot;
import com.github.phantompowered.proxy.entity.types.living.animal.tamable.ProxyTameable;
import com.github.phantompowered.proxy.entity.types.living.animal.tamable.ProxyWolf;
import com.github.phantompowered.proxy.entity.types.living.boss.ProxyBoss;
import com.github.phantompowered.proxy.entity.types.living.boss.ProxyEnderDragon;
import com.github.phantompowered.proxy.entity.types.living.boss.ProxyWither;
import com.github.phantompowered.proxy.entity.types.living.creature.ProxyCreature;
import com.github.phantompowered.proxy.entity.types.living.creature.ProxyGolem;
import com.github.phantompowered.proxy.entity.types.living.creature.ProxyIronGolem;
import com.github.phantompowered.proxy.entity.types.living.creature.ProxySnowman;
import com.github.phantompowered.proxy.entity.types.living.human.ProxyPlayer;
import com.github.phantompowered.proxy.entity.types.living.monster.*;
import com.github.phantompowered.proxy.entity.types.minecart.ProxyCommandBlockMinecart;
import com.github.phantompowered.proxy.entity.types.minecart.ProxyFurnaceMinecart;
import com.github.phantompowered.proxy.entity.types.minecart.ProxyMinecart;
import com.github.phantompowered.proxy.event.DefaultEventManager;
import com.github.phantompowered.proxy.event.DefaultListenerContainer;
import com.github.phantompowered.proxy.item.*;
import com.github.phantompowered.proxy.network.channel.DefaultNetworkChannel;
import com.github.phantompowered.proxy.network.registry.handler.DefaultPacketHandlerRegistry;
import com.github.phantompowered.proxy.network.registry.handler.DefaultPacketHandlerRegistryEntry;
import com.github.phantompowered.proxy.network.registry.packet.DefaultPacketRegistry;
import com.github.phantompowered.proxy.network.registry.packet.DefaultPacketRegistryEntry;
import com.github.phantompowered.proxy.network.wrapper.DefaultProtoBuf;
import com.github.phantompowered.proxy.paste.DefaultPasteServer;
import com.github.phantompowered.proxy.paste.DefaultPasteServerProvider;
import com.github.phantompowered.proxy.paste.DefaultPasteServerUploadResult;
import com.github.phantompowered.proxy.ping.DefaultServerPingProvider;
import com.github.phantompowered.proxy.plugin.DefaultPluginContainer;
import com.github.phantompowered.proxy.plugin.DefaultPluginManager;
import com.github.phantompowered.proxy.potion.ProxyPotionEffect;
import com.github.phantompowered.proxy.service.BasicServiceRegistry;
import com.github.phantompowered.proxy.storage.DefaultPlayerIdStorage;
import com.github.phantompowered.proxy.tick.DefaultTickHandlerProvider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class DefaultImplementationMapper implements ImplementationMapper {

    private final BiMap<Class<?>, Class<?>> mappings = HashBiMap.create();

    {
        this.init();
    }

    private void init() {
        this.registerMapping(BlockState.class, DefaultBlockState.class);
        this.registerMapping(BlockAccess.class, DefaultBlockAccess.class);
        this.registerMapping(BlockStateRegistry.class, DefaultBlockStateRegistry.class);

        this.registerMapping(CommandMap.class, DefaultCommandMap.class);
        this.registerMapping(CommandContainer.class, DefaultCommandContainer.class);

        this.registerMapping(Configuration.class, JsonConfiguration.class);

        this.registerMapping(ServiceConnection.class, BasicServiceConnection.class);
        this.registerMapping(InteractiveServiceConnection.class, BasicInteractiveServiceConnection.class);
        this.registerMapping(ServiceConnector.class, DefaultServiceConnector.class);
        this.registerMapping(ServiceInventory.class, DefaultServiceInventory.class);
        this.registerMapping(ServiceWorldDataProvider.class, BasicServiceWorldDataProvider.class);
        this.registerMapping(Whitelist.class, DefaultWhitelist.class);

        this.registerMapping(DatabaseObject.class, DefaultDatabaseObject.class);

        this.registerMapping(PlayerInfo.class, BasicPlayerInfo.class);
        this.registerMapping(PlayerSkinConfiguration.class, DefaultPlayerSkinConfiguration.class);
        this.registerMapping(EntityEffect.class, TimedEntityEffect.class);

        this.registerEntityTypes();

        this.registerMapping(EventManager.class, DefaultEventManager.class);
        this.registerMapping(ListenerContainer.class, DefaultListenerContainer.class);

        this.registerItemMetas();
        this.registerNetworking();
        this.registerPaste();

        this.registerMapping(PermissionHolder.class, DefaultOfflinePlayer.class);
        this.registerMapping(ServerPingProvider.class, DefaultServerPingProvider.class);

        this.registerPlayer();
        this.registerPlugin();

        this.registerMapping(PotionEffect.class, ProxyPotionEffect.class);

        this.registerScoreboard();

        this.registerMapping(ServiceRegistry.class, BasicServiceRegistry.class);
        this.registerMapping(ServiceRegistryEntry.class, BasicServiceRegistry.class);

        this.registerMapping(ProvidedSessionService.class, BasicProvidedSessionService.class);

        this.registerMapping(TickHandlerProvider.class, DefaultTickHandlerProvider.class);
    }

    private void registerScoreboard() {
        this.registerMapping(Team.class, BasicTeam.class);
        this.registerMapping(Scoreboard.class, BasicScoreboard.class);
        this.registerMapping(Score.class, BasicScore.class);
        this.registerMapping(Objective.class, BasicObjective.class);
    }

    private void registerPlugin() {
        this.registerMapping(PluginContainer.class, DefaultPluginContainer.class);
        this.registerMapping(PluginManager.class, DefaultPluginManager.class);
    }

    private void registerPlayer() {
        this.registerMapping(PlayerRepository.class, DefaultPlayerRepository.class);
        this.registerMapping(PlayerAbilities.class, DefaultPlayerAbilities.class);
        this.registerMapping(Player.class, DefaultPlayer.class);
        this.registerMapping(OfflinePlayer.class, DefaultOfflinePlayer.class);
        this.registerMapping(PlayerInventory.class, DefaultPlayerInventory.class);
        this.registerMapping(PlayerIdStorage.class, DefaultPlayerIdStorage.class);
    }

    private void registerPaste() {
        this.registerMapping(PasteServer.class, DefaultPasteServer.class);
        this.registerMapping(PasteServerProvider.class, DefaultPasteServerProvider.class);
        this.registerMapping(PasteServerUploadResult.class, DefaultPasteServerUploadResult.class);
    }

    private void registerNetworking() {
        this.registerMapping(ProtoBuf.class, DefaultProtoBuf.class);
        this.registerMapping(PacketHandlerRegistry.class, DefaultPacketHandlerRegistry.class);
        this.registerMapping(PacketHandlerRegistryEntry.class, DefaultPacketHandlerRegistryEntry.class);
        this.registerMapping(PacketRegistry.class, DefaultPacketRegistry.class);
        this.registerMapping(PacketRegistryEntry.class, DefaultPacketRegistryEntry.class);
        this.registerMapping(NetworkChannel.class, DefaultNetworkChannel.class);
    }

    private void registerItemMetas() {
        this.registerMapping(BookMeta.class, ProxyBookMeta.class);
        this.registerMapping(EnchantedBookMeta.class, ProxyEnchantedBookMeta.class);
        this.registerMapping(ItemMeta.class, ProxyItemMeta.class);
        this.registerMapping(LeatherArmorMeta.class, ProxyLeatherArmorMeta.class);
        this.registerMapping(PotionMeta.class, ProxyPotionMeta.class);
        this.registerMapping(SkullMeta.class, ProxySkullMeta.class);
    }

    private void registerEntityTypes() {
        this.registerMapping(Entity.class, ProxyEntity.class);
        this.registerMapping(Scaleable.class, ProxyScaleable.class);
        this.registerMapping(Hanging.class, ProxyHanging.class);
        this.registerMapping(Firework.class, ProxyFirework.class);
        this.registerMapping(ExperienceOrb.class, ProxyExperienceOrb.class);
        this.registerMapping(Boat.class, ProxyBoat.class);
        this.registerMapping(ArmorStand.class, ProxyArmorStand.class);
        this.registerMapping(Ambient.class, ProxyAmbient.class);
        this.registerMapping(CommandBlockMinecart.class, ProxyCommandBlockMinecart.class);
        this.registerMapping(FurnaceMinecart.class, ProxyFurnaceMinecart.class);
        this.registerMapping(Minecart.class, ProxyMinecart.class);
        this.registerMapping(Flying.class, ProxyFlying.class);
        this.registerMapping(EntityLiving.class, ProxyEntityLiving.class);

        // monster
        this.registerMapping(Blaze.class, ProxyBlaze.class);
        this.registerMapping(CaveSpider.class, ProxyCaveSpider.class);
        this.registerMapping(Creeper.class, ProxyCreeper.class);
        this.registerMapping(Enderman.class, ProxyEnderman.class);
        this.registerMapping(Endermite.class, ProxyEndermite.class);
        this.registerMapping(Ghast.class, ProxyGhast.class);
        this.registerMapping(GiantZombie.class, ProxyGiantZombie.class);
        this.registerMapping(Guardian.class, ProxyGuardian.class);
        this.registerMapping(LavaSlime.class, ProxyLavaSlime.class);
        this.registerMapping(PigZombie.class, ProxyPigZombie.class);
        this.registerMapping(Silverfish.class, ProxySilverfish.class);
        this.registerMapping(Skeleton.class, ProxySkeleton.class);
        this.registerMapping(Slime.class, ProxySlime.class);
        this.registerMapping(Spider.class, ProxySpider.class);
        this.registerMapping(Witch.class, ProxyWitch.class);
        this.registerMapping(Zombie.class, ProxyZombie.class);

        // human
        this.registerMapping(EntityPlayer.class, ProxyPlayer.class);

        // creature
        this.registerMapping(Creature.class, ProxyCreature.class);
        this.registerMapping(Golem.class, ProxyGolem.class);
        this.registerMapping(IronGolem.class, ProxyIronGolem.class);
        this.registerMapping(Snowman.class, ProxySnowman.class);

        // boss
        this.registerMapping(Boss.class, ProxyBoss.class);
        this.registerMapping(EnderDragon.class, ProxyEnderDragon.class);
        this.registerMapping(Wither.class, ProxyWither.class);

        // animal
        this.registerMapping(WaterAnimal.class, ProxyWaterAnimal.class);
        this.registerMapping(Squid.class, ProxySquid.class);
        this.registerMapping(Bat.class, ProxyBat.class);
        this.registerMapping(Animal.class, ProxyAnimal.class);

        // tamable
        this.registerMapping(Wolf.class, ProxyWolf.class);
        this.registerMapping(Tameable.class, ProxyTameable.class);
        this.registerMapping(Ocelot.class, ProxyOcelot.class);

        // npc
        this.registerMapping(NPC.class, ProxyNPC.class);
        this.registerMapping(Villager.class, ProxyVillager.class);

        // ageable
        this.registerMapping(Ageable.class, ProxyAgeable.class);
        this.registerMapping(Chicken.class, ProxyChicken.class);
        this.registerMapping(Cow.class, ProxyCow.class);
        this.registerMapping(Horse.class, ProxyHorse.class);
        this.registerMapping(MushroomCow.class, ProxyMushroomCow.class);
        this.registerMapping(Pig.class, ProxyPig.class);
        this.registerMapping(Rabbit.class, ProxyRabbit.class);
        this.registerMapping(Sheep.class, ProxySheep.class);

        // item
        this.registerMapping(Arrow.class, ProxyArrow.class);
        this.registerMapping(Egg.class, ProxyEgg.class);
        this.registerMapping(EnderPearl.class, ProxyEnderPearl.class);
        this.registerMapping(EnderSignal.class, ProxyEnderSignal.class);
        this.registerMapping(Fireball.class, ProxyFireball.class);
        this.registerMapping(FishingHook.class, ProxyFishingHook.class);
        this.registerMapping(Item.class, ProxyItem.class);
        this.registerMapping(ItemFrame.class, ProxyItemFrame.class);
        this.registerMapping(Leash.class, ProxyLeash.class);
        this.registerMapping(Potion.class, ProxyPotion.class);
        this.registerMapping(Projectile.class, ProxyProjectile.class);
        this.registerMapping(SmallFireball.class, ProxySmallFireball.class);
        this.registerMapping(Snowball.class, ProxySnowball.class);
        this.registerMapping(ThrownExpBottle.class, ProxyThrownExpBottle.class);
        this.registerMapping(WitherSkull.class, ProxyWitherSkull.class);

        // block
        this.registerMapping(EnderCrystal.class, ProxyEnderCrystal.class);
        this.registerMapping(FallingBlock.class, ProxyFallingBlock.class);
        this.registerMapping(TNTPrimed.class, ProxyTNTPrimed.class);
    }

    private void registerMapping(Class<?> apiClass, Class<?> implementationClass) {
        this.mappings.put(apiClass, implementationClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> getImplementationClass(Class<T> apiClass) {
        return (Class<? extends T>) this.mappings.get(apiClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> getApiClass(Class<T> implementationClass) {
        return (Class<T>) this.mappings.inverse().get(implementationClass);
    }
}
