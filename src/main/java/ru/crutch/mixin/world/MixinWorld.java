package ru.crutch.mixin.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.generator.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.crutch.interfaces.world.IMixinWorld;
import ru.crutch.interfaces.entity.IMixinEntity;

@Mixin(net.minecraft.world.World.class)
public abstract class MixinWorld implements IMixinWorld{

    @Shadow
    public WorldProvider provider;
    @Shadow(remap = false)
    public boolean restoringBlockSnapshots;
    @Shadow
    protected IChunkProvider chunkProvider;
    private boolean pvpMode;

    @Shadow
    protected abstract IChunkProvider createChunkProvider();

    @Shadow
    public abstract WorldInfo getWorldInfo();

    @Shadow
    public abstract boolean checkNoEntityCollision(AxisAlignedBB bb);

    @Shadow
    public abstract boolean checkNoEntityCollision(AxisAlignedBB bb, Entity entityType);


    @Shadow public abstract IBlockState getBlockState(BlockPos pos);


    @Override
    public void setProvider(WorldProvider provider){
        this.provider = provider;
    }

    @Override
    public WorldProvider getProvider(){
        return this.provider;
    }

    @Override
    public boolean getpvpMode(){
        return this.pvpMode;
    }
    @Override
    public void setpvpMode(boolean flag){
        this.pvpMode = flag;
    }
    
    @Shadow public boolean spawnPeacefulMobs;
    @Shadow protected boolean spawnHostileMobs;
    public long ticksPerAnimalSpawns;
    public long ticksPerMonsterSpawns;

    private ChunkGenerator generator;
    private CraftWorld craftWorld;
    private IBlockState iblockstate;
    public boolean keepSpawnInMemory = true;

    @Override
    public void setTicksPerMonsterSpawns(final int ticksPerMonsterSpawns){
        this.ticksPerMonsterSpawns = ticksPerMonsterSpawns;
    }
    @Override
    public long getTicksPerMonsterSpawns(){
        return this.ticksPerMonsterSpawns;
    }

    @Override
    public void setTicksPerAnimalSpawns(final int ticksPerAnimalSpawns){
        this.ticksPerAnimalSpawns = ticksPerAnimalSpawns;
    }
    @Override
    public long getTicksPerAnimalSpawns(){
        return this.ticksPerAnimalSpawns;
    }

    @Override
    public boolean getKeepSpawnInMemory(){
        return this.keepSpawnInMemory;
    }

    @Override
    public void setKeepSpawnInMemory(boolean flag){
        this.keepSpawnInMemory = flag;
    }

    @Override
    public boolean getSpawnHostileMobs(){
        return this.spawnHostileMobs;
    }

    @Override
    public boolean getSpawnPeacefulMobs(){
        return this.spawnPeacefulMobs;
    }



    @Redirect(method = "<init>", at = @At(value = "RETURN", target = "Lnet/minecraft/world/World;createChunkProvider()Lnet/minecraft/world/chunk/IChunkProvider;"))
    public IChunkProvider createChunkProviderReplacement(World world)
    {
        if(!((Object) this instanceof WorldServer) || getServer() == null)
            return createChunkProvider();

        int providerId = DimensionManager.getProviderType( provider.getDimension()).getId();
        
        org.bukkit.World.Environment env = org.bukkit.World.Environment.getEnvironment(providerId);
        env.getClass(); //NPE
        WorldInfo info = getWorldInfo();
        initBukkit(info == null ? null : getServer().getGenerator(info.getWorldName()), env);

        //

        IChunkLoader ichunkloader = world.getSaveHandler().getChunkLoader(provider);
        if(provider.getClass().toString().startsWith("net.minecraft."))
        {
            // CraftBukkit start
            org.bukkit.craftbukkit.generator.InternalChunkGenerator gen;

            if(this.generator != null)
            {
                gen = new org.bukkit.craftbukkit.generator.CustomChunkGenerator(world, world.getSeed(), this.generator);
            }
            else if(provider instanceof WorldProviderHell)
            {
                gen = new org.bukkit.craftbukkit.generator.NetherChunkGenerator(world, world.getSeed());
            }
            else if(provider instanceof WorldProviderEnd)
            {
                gen = new org.bukkit.craftbukkit.generator.SkyLandsChunkGenerator(world, world.getSeed());
            }
            else
            {
                gen = new org.bukkit.craftbukkit.generator.NormalChunkGenerator(world, world.getSeed());
            }
            this.chunkProvider = new ChunkProviderServer((WorldServer) world, ichunkloader, gen);
            // CraftBukkit end
        }
        else
        {
            this.chunkProvider = new ChunkProviderServer((WorldServer) world, ichunkloader, provider.createChunkGenerator());
        }

        return this.chunkProvider;
    }

    private void initBukkit(ChunkGenerator generator, org.bukkit.World.Environment env)
    {
        this.generator = generator;
        this.craftWorld = new CraftWorld((WorldServer) (Object) this, generator, env);

        if(generator != null)
            getWorld().getPopulators().addAll(generator.getDefaultPopulators(getWorld()));
        getServer().addWorld(craftWorld);
    }


    @Override
    public CraftWorld getWorld()
    {
        return craftWorld;
    }

    @Override
    public CraftServer getServer()
    {
        return (CraftServer) Bukkit.getServer();
    }

    @Override
    public boolean addEntity(Entity entity, CreatureSpawnEvent.SpawnReason spawnReason)
    {
        if(entity == null || (this.restoringBlockSnapshots && entity instanceof EntityItem))
            return false;
        IMixinEntity mentity = (IMixinEntity) entity;
        org.bukkit.event.Cancellable event = null;
        // Cauldron start - workaround for handling CraftBukkit's SpawnReason with customspawners and block spawners
        String strSpawnReason = mentity.getSpawnReason();
        if(strSpawnReason != null)
        {
            if(strSpawnReason.equals("natural"))
            {
                spawnReason = SpawnReason.NATURAL;
            }
            else if(strSpawnReason.equals("spawner"))
            {
                spawnReason = SpawnReason.SPAWNER;
            }
        }
        // Cauldron end

        if(entity instanceof EntityLivingBase && !(entity instanceof EntityPlayerMP))
        {
            event = CraftEventFactory.callCreatureSpawnEvent((EntityLivingBase) entity, spawnReason);
        }
        else if(entity instanceof EntityItem)
        {
            event = CraftEventFactory.callItemSpawnEvent((EntityItem) entity);
        }
        else if(mentity.getBukkitEntity() instanceof org.bukkit.entity.Projectile)
        {
            // Not all projectiles extend EntityProjectile, so check for Bukkit interface instead
            event = CraftEventFactory.callProjectileLaunchEvent(entity);
        }

        if(event != null && (event.isCancelled() || entity.isDead))
        {
            entity.isDead = true;
            return false;
        }

        return real_spawnEntityInWorld(entity);
    }

    @Override
    public boolean spawnEntityInWorld(Entity entity) {
        return addEntity(entity, SpawnReason.DEFAULT);
    }

    // must be public to be compatible with some mods using world proxy
    public abstract boolean real_spawnEntityInWorld(Entity entity);
    //TODO setData and getData
    /*
    @Override
    public boolean setRawTypeId(int x, int y, int z, int typeId)
    {
        return this.setBlock(x, y, z, Block.getBlockById(typeId), 0, 4);
    }

    @Override
    public boolean setRawTypeIdAndData(int x, int y, int z, int typeId, int data)
    {
        return this.setBlock(x, y, z, Block.getBlockById(typeId), data, 4);
    }

    @Override
    public boolean setTypeId(int x, int y, int z, int typeId)
    {
        return this.setBlock(x, y, z, Block.getBlockById(typeId), 0, 3);
    }

    @Override
    public boolean setTypeIdAndData(int x, int y, int z, int typeId, int data)
    {
        return this.setBlock(x, y, z, Block.getBlockById(typeId), data, 3);
    }

    @Override
    public int getTypeId(int x, int y, int z)
    {
        return Block.getIdFromBlock(new IBlockState(new BlockPos(x,y,z)).getBlock());
    }

    @Override
    public boolean setTypeAndData(int x, int y, int z, Block block, int data, int flag)
    {
        return this.setBlock(x, y, z, block, data, flag);
    }


    @Override
    public boolean setData(int x, int y, int z, int data, int flag)
    {
        this.iblockstate = this.getBlockState(new BlockPos(x,y,z));
        return this.iblockstate.getBlock().(x, y, z, data, flag);
    }

    @Override
    public int getData(int x, int y, int z)
    {
        this.iblockstate = this.getBlockState(new BlockPos(x,y,z));
        return this.iblockstate.getBlock().getMetaFromState(iblockstate);
    }*/

    @Override
    public org.bukkit.block.Block getType(int x, int y, int z)
    {
        this.iblockstate = this.getBlockState(new BlockPos(x,y,z));
        return (org.bukkit.block.Block)this.iblockstate.getBlock();
    }

}
