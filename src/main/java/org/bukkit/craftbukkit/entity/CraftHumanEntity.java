package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.HandSide;
import net.minecraft.block.BlockState;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.CooldownTracker;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BlastFurnaceTileEntity;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.util.math.Vec3d;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftMerchantCustom;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class CraftHumanEntity extends CraftLivingEntity implements HumanEntity {
    private CraftInventoryPlayer inventory;
    private final CraftInventory enderChest;
    protected final PermissibleBase perm = new PermissibleBase(this);
    private boolean op;
    private GameMode mode;

    public CraftHumanEntity(final CraftServer server, final PlayerEntity entity) {
        super(server, entity);
        mode = server.getDefaultGameMode();
        this.inventory = new CraftInventoryPlayer(entity.inventory);
        enderChest = new CraftInventory(entity.getEnderChest());
    }

    @Override
    public PlayerInventory getInventory() {
        return inventory;
    }

    @Override
    public EntityEquipment getEquipment() {
        return inventory;
    }

    @Override
    public Inventory getEnderChest() {
        return enderChest;
    }

    @Override
    public MainHand getMainHand() {
        return getHandle().getMainHand()== EnumMainHand.LEFT ? MainHand.LEFT : MainHand.RIGHT;
    }

    @Override
    public ItemStack getItemInHand() {
        return getInventory().getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack item) {
        getInventory().setItemInHand(item);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return CraftItemStack.asCraftMirror(getHandle().inventory.getCarried());
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
        net.minecraft.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        getHandle().inventory.setCarried(stack);
        if (this instanceof CraftPlayer) {
            ((ServerPlayerEntity) getHandle()).broadcastCarriedItem(); // Send set slot for cursor
        }
    }

    @Override
    public int getSleepTicks() {
        return getHandle().sleepTicks;
    }

    @Override
    public Location getBedSpawnLocation() {
        World world = getServer().getWorld(getHandle().spawnWorld);
        BlockPos bed = getHandle().getBed();

        if (world != null && bed != null) {
            Optional<Vec3D> spawnLoc = PlayerEntity.getBed(((CraftWorld) world).getHandle(), bed, getHandle().isRespawnForced());
            if (spawnLoc.isPresent()) {
                Vec3D vec = spawnLoc.get();
                return new Location(world, vec.x, vec.y, vec.z);
            }
        }
        return null;
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        setBedSpawnLocation(location, false);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean override) {
        if (location == null) {
            getHandle().setRespawnPosition(null, override, false);
        } else {
            getHandle().setRespawnPosition(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), override, false);
            getHandle().spawnWorld = location.getWorld().getName();
        }
    }

    @Override
    public boolean sleep(Location location, boolean force) {
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
        Preconditions.checkArgument(location.getWorld().equals(getWorld()), "Cannot sleep across worlds");

        BlockPos blockposition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        IBlockData iblockdata = getHandle().world.getType(blockposition);
        if (!(iblockdata.getBlock() instanceof BlockBed)) {
            return false;
        }

        if (getHandle().sleep(blockposition, force).left().isPresent()) {
            return false;
        }

        // From BlockBed
        iblockdata = (IBlockData) iblockdata.set(BlockBed.OCCUPIED, true);
        getHandle().world.setTypeAndData(blockposition, iblockdata, 4);

        return true;
    }

    @Override
    public void wakeup(boolean setSpawnLocation) {
        Preconditions.checkState(isSleeping(), "Cannot wakeup if not sleeping");

        getHandle().wakeup(true, setSpawnLocation);
    }

    @Override
    public Location getBedLocation() {
        Preconditions.checkState(isSleeping(), "Not sleeping");

        BlockPos bed = getHandle().getBed();
        return new Location(getWorld(), bed.getX(), bed.getY(), bed.getZ());
    }

    @Override
    public String getName() {
        return getHandle().getName();
    }

    @Override
    public boolean isOp() {
        return op;
    }

    @Override
    public boolean isPermissionSet(String name) {
        return perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return this.perm.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return this.perm.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return perm.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return perm.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return perm.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return perm.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        perm.recalculatePermissions();
    }

    @Override
    public void setOp(boolean value) {
        this.op = value;
        perm.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return perm.getEffectivePermissions();
    }

    @Override
    public GameMode getGameMode() {
        return mode;
    }

    @Override
    public void setGameMode(GameMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        this.mode = mode;
    }

    @Override
    public PlayerEntity getHandle() {
        return (PlayerEntity) entity;
    }

    public void setHandle(final PlayerEntity entity) {
        super.setHandle(entity);
        this.inventory = new CraftInventoryPlayer(entity.inventory);
    }

    @Override
    public String toString() {
        return "CraftHumanEntity{" + "id=" + getEntityId() + "name=" + getName() + '}';
    }

    @Override
    public InventoryView getOpenInventory() {
        return getHandle().openContainer.getBukkitView();
    }

    @Override
    public InventoryView openInventory(Inventory inventory) {
        if(!(getHandle() instanceof ServerPlayerEntity)) return null;
        ServerPlayerEntity player = (ServerPlayerEntity) getHandle();
        InventoryType type = inventory.getType();
        Container formerContainer = getHandle().openContainer;

        ITileInventory iinventory = null;
        if (inventory instanceof CraftInventoryDoubleChest) {
            iinventory = ((CraftInventoryDoubleChest) inventory).tile;
        } else if (inventory instanceof CraftInventory) {
            CraftInventory craft = (CraftInventory) inventory;
            if (craft.getInventory() instanceof ITileInventory) {
                iinventory = (ITileInventory) craft.getInventory();
            }
        }

        if (iinventory instanceof ITileInventory) {
            if (iinventory instanceof TileEntity) {
                TileEntity te = (TileEntity) iinventory;
                if (!te.hasWorld()) {
                    te.setLocation(getHandle().world, getHandle().getChunkCoordinates());
                }
            }
        }

        switch (type) {
            case PLAYER:
            case CHEST:
            case ENDER_CHEST:
                if (iinventory instanceof ITileInventory) {
                    getHandle().openContainer((ITileInventory) iinventory);
                } else {
                    ContainerType customSize;
                    switch (inventory.getSize()) {
                        case 9:
                            customSize = ContainerType.GENERIC_9X1;
                            break;
                        case 18:
                            customSize = ContainerType.GENERIC_9X2;
                            break;
                        case 27:
                            customSize = ContainerType.GENERIC_9X3;
                            break;
                        case 36:
                        case 41: // PLAYER
                            customSize = ContainerType.GENERIC_9X4;
                            break;
                        case 45:
                            customSize = ContainerType.GENERIC_9X5;
                            break;
                        case 54:
                            customSize = ContainerType.GENERIC_9X6;
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported custom inventory size " + inventory.getSize());
                    }
                    openCustomInventory(inventory, player, customSize);
                }
                break;
            case DISPENSER:
                if (iinventory instanceof DispenserTileEntity) {
                    getHandle().openContainer((DispenserTileEntity) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.GENERIC_3X3);
                }
                break;
            case DROPPER:
                if (iinventory instanceof DropperTileEntity) {
                    getHandle().openContainer((DropperTileEntity) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.GENERIC_3X3);
                }
                break;
            case FURNACE:
                if (iinventory instanceof MobSpawnerTileEntity) {
                    getHandle().openContainer((MobSpawnerTileEntity) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.FURNACE);
                }
                break;
            case WORKBENCH:
                openCustomInventory(inventory, player, ContainerType.CRAFTING);
                break;
            case BREWING:
                if (iinventory instanceof BrewingStandTileEntity) {
                    getHandle().openContainer((BrewingStandTileEntity) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.BREWING_STAND);
                }
                break;
            case ENCHANTING:
                openCustomInventory(inventory, player, ContainerType.ENCHANTMENT);
                break;
            case HOPPER:
                if (iinventory instanceof TileEntityHopper) {
                    getHandle().openContainer((TileEntityHopper) iinventory);
                } else if (iinventory instanceof EntityMinecartHopper) {
                    getHandle().openContainer((EntityMinecartHopper) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.HOPPER);
                }
                break;
            case BEACON:
                if (iinventory instanceof BeaconTileEntity) {
                    getHandle().openContainer((BeaconTileEntity) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.BEACON);
                }
                break;
            case ANVIL:
                if (iinventory instanceof ITileInventory) {
                    getHandle().openContainer((ITileInventory) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.ANVIL);
                }
                break;
            case SHULKER_BOX:
                if (iinventory instanceof TileEntityShulkerBox) {
                    getHandle().openContainer((TileEntityShulkerBox) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.SHULKER_BOX);
                }
                break;
            case BARREL:
                if (iinventory instanceof TileEntityBarrel) {
                    getHandle().openContainer((TileEntityBarrel) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.GENERIC_9X3);
                }
                break;
            case BLAST_FURNACE:
                if (iinventory instanceof TileEntityBlastFurnace) {
                    getHandle().openContainer((TileEntityBlastFurnace) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.BLAST_FURNACE);
                }
                break;
            case LECTERN:
                if (iinventory instanceof TileEntityLectern) {
                    getHandle().openContainer((TileEntityLectern) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.LECTERN);
                }
                break;
            case SMOKER:
                if (iinventory instanceof SmokerTileEntity) {
                    getHandle().openContainer((SmokerTileEntity) iinventory);
                } else {
                    openCustomInventory(inventory, player, ContainerType.SMOKER);
                }
                break;
            case STONECUTTER:
                openCustomInventory(inventory, player, ContainerType.STONECUTTER);
                break;
            case LOOM:
                openCustomInventory(inventory, player, ContainerType.LOOM);
                break;
            case CARTOGRAPHY:
                openCustomInventory(inventory, player, ContainerType.CARTOGRAPHY_TABLE);
                break;
            case GRINDSTONE:
                openCustomInventory(inventory, player, ContainerType.GRINDSTONE);
                break;
            case CREATIVE:
            case CRAFTING:
            case MERCHANT:
            default:
                throw new IllegalArgumentException("Can't open a " + type + " inventory!");
        }
        if (getHandle().openContainer == formerContainer) {
            return null;
        }
        getHandle().openContainer.checkReachable = false;
        return getHandle().openContainer.getBukkitView();
    }

    private void openCustomInventory(Inventory inventory, ServerPlayerEntity player, ContainerType<?> windowType) {
        if (player.connection == null) return;
        Preconditions.checkArgument(windowType != null, "Unknown windowType");
        Container container = new CraftContainer(inventory, this.getHandle(), player.nextContainerCounter());

        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if(container == null) return;

        String title = container.getBukkitView().getTitle();

        player.connection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, new ChatComponentText(title)));
        getHandle().openContainer = container;
        getHandle().openContainer.addSlotListener(player);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean force) {
        if (!force) {
            Block block = location.getBlock();
            if (block.getType() != Material.CRAFTING_TABLE) {
                return null;
            }
        }
        if (location == null) {
            location = getLocation();
        }
        getHandle().openContainer(((BlockWorkbench) Blocks.CRAFTING_TABLE).getInventory(null, getHandle().world, new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
        if (force) {
            getHandle().openContainer.checkReachable = false;
        }
        return getHandle().openContainer.getBukkitView();
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean force) {
        if (!force) {
            Block block = location.getBlock();
            if (block.getType() != Material.ENCHANTING_TABLE) {
                return null;
            }
        }
        if (location == null) {
            location = getLocation();
        }

        // If there isn't an enchant table we can force create one, won't be very useful though.
        BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        getHandle().openContainer(((BlockEnchantmentTable) Blocks.ENCHANTING_TABLE).getInventory(null, getHandle().world, pos));

        if (force) {
            getHandle().openContainer.checkReachable = false;
        }
        return getHandle().openContainer.getBukkitView();
    }

    @Override
    public void openInventory(InventoryView inventory) {
        if (!(getHandle() instanceof ServerPlayerEntity)) return; // TODO: NPC support?
        if (((ServerPlayerEntity) getHandle()).connection == null) return;
        if (getHandle().openContainer != getHandle().container) {
            // fire INVENTORY_CLOSE if one already open
            ((ServerPlayerEntity)getHandle()).connection.a(new CCloseWindowPacket(getHandle().openContainer.windowId));
        }
        ServerPlayerEntity player = (ServerPlayerEntity) getHandle();
        Container container;
        if (inventory instanceof CraftInventoryView) {
            container = ((CraftInventoryView) inventory).getHandle();
        } else {
            container = new CraftContainer(inventory, this.getHandle(), player.nextContainerCounter());
        }

        // Trigger an INVENTORY_OPEN event
        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if (container == null) {
            return;
        }

        // Now open the window
        InventoryType type = inventory.getType();
        ContainerType<?> windowType = CraftContainer.getNotchInventoryType(type);
        String title = inventory.getTitle();
        player.connection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, new ChatComponentText(title)));
        player.openContainer = container;
        player.openContainer.addSlotListener(player);
    }

    @Override
    public InventoryView openMerchant(Villager villager, boolean force) {
        Preconditions.checkNotNull(villager, "villager cannot be null");

        return this.openMerchant((Merchant) villager, force);
    }

    @Override
    public InventoryView openMerchant(Merchant merchant, boolean force) {
        Preconditions.checkNotNull(merchant, "merchant cannot be null");

        if (!force && merchant.isTrading()) {
            return null;
        } else if (merchant.isTrading()) {
            // we're not supposed to have multiple people using the same merchant, so we have to close it.
            merchant.getTrader().closeInventory();
        }

        IMerchant mcMerchant;
        ITextComponent name;
        int level = 1; // note: using level 0 with active 'is-regular-villager'-flag allows hiding the name suffix
        if (merchant instanceof CraftAbstractVillager) {
            mcMerchant = ((CraftAbstractVillager) merchant).getHandle();
            name = ((CraftAbstractVillager) merchant).getHandle().getScoreboardDisplayName();
            if (merchant instanceof CraftVillager) {
                level = ((CraftVillager) merchant).getHandle().getVillagerData().getLevel();
            }
        } else if (merchant instanceof CraftMerchantCustom) {
            mcMerchant = ((CraftMerchantCustom) merchant).getMerchant();
            name = ((CraftMerchantCustom) merchant).getMerchant().getScoreboardDisplayName();
        } else {
            throw new IllegalArgumentException("Can't open merchant " + merchant.toString());
        }

        mcMerchant.setTradingPlayer(this.getHandle());
        mcMerchant.openTrade(this.getHandle(), name, level);

        return this.getHandle().openContainer.getBukkitView();
    }

    @Override
    public void closeInventory() {
        getHandle().closeInventory();
    }

    @Override
    public boolean isBlocking() {
        return getHandle().isBlocking();
    }

    @Override
    public boolean isHandRaised() {
        return getHandle().isHandRaised();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        return false;
    }

    @Override
    public int getExpToLevel() {
        return getHandle().getExpToLevel();
    }

    @Override
    public boolean hasCooldown(Material material) {
        Preconditions.checkArgument(material != null, "material");

        return getHandle().getCooldownTracker().hasCooldown(CraftMagicNumbers.getItem(material));
    }

    @Override
    public int getCooldown(Material material) {
        Preconditions.checkArgument(material != null, "material");

        ItemCooldown.Info cooldown = getHandle().getCooldownTracker().cooldowns.get(CraftMagicNumbers.getItem(material));
        return (cooldown == null) ? 0 : Math.max(0, cooldown.endTick - getHandle().getCooldownTracker().currentTick);
    }

    @Override
    public void setCooldown(Material material, int ticks) {
        Preconditions.checkArgument(material != null, "material");
        Preconditions.checkArgument(ticks >= 0, "Cannot have negative cooldown");

        getHandle().getCooldownTracker().setCooldown(CraftMagicNumbers.getItem(material), ticks);
    }

    @Override
    public boolean discoverRecipe(NamespacedKey recipe) {
        return discoverRecipes(Arrays.asList(recipe)) != 0;
    }

    @Override
    public int discoverRecipes(Collection<NamespacedKey> recipes) {
        return getHandle().discoverRecipes(bukkitKeysToMinecraftRecipes(recipes));
    }

    @Override
    public boolean undiscoverRecipe(NamespacedKey recipe) {
        return undiscoverRecipes(Arrays.asList(recipe)) != 0;
    }

    @Override
    public int undiscoverRecipes(Collection<NamespacedKey> recipes) {
        return getHandle().undiscoverRecipes(bukkitKeysToMinecraftRecipes(recipes));
    }

    private Collection<IRecipe<?>> bukkitKeysToMinecraftRecipes(Collection<NamespacedKey> recipeKeys) {
        Collection<IRecipe<?>> recipes = new ArrayList<>();
        CraftingManager manager = getHandle().world.getMinecraftServer().getCraftingManager();

        for (NamespacedKey recipeKey : recipeKeys) {
            Optional<? extends IRecipe<?>> recipe = manager.a(CraftNamespacedKey.toMinecraft(recipeKey));
            if (!recipe.isPresent()) {
                continue;
            }

            recipes.add(recipe.get());
        }

        return recipes;
    }

    @Override
    public org.bukkit.entity.Entity getShoulderEntityLeft() {
        if (!getHandle().getShoulderEntityLeft().isEmpty()) {
            Optional<Entity> shoulder = EntityTypes.a(getHandle().getShoulderEntityLeft(), getHandle().world);

            return (!shoulder.isPresent()) ? null : shoulder.get().getBukkitEntity();
        }

        return null;
    }

    @Override
    public void setShoulderEntityLeft(org.bukkit.entity.Entity entity) {
        getHandle().setShoulderEntityLeft(entity == null ? new CompoundNBT() : ((CraftEntity) entity).save());
        if (entity != null) {
            entity.remove();
        }
    }

    @Override
    public org.bukkit.entity.Entity getShoulderEntityRight() {
        if (!getHandle().getShoulderEntityRight().isEmpty()) {
            Optional<Entity> shoulder = EntityTypes.a(getHandle().getShoulderEntityRight(), getHandle().world);

            return (!shoulder.isPresent()) ? null : shoulder.get().getBukkitEntity();
        }

        return null;
    }

    @Override
    public void setShoulderEntityRight(org.bukkit.entity.Entity entity) {
        getHandle().setShoulderEntityRight(entity == null ? new CompoundNBT() : ((CraftEntity) entity).save());
        if (entity != null) {
            entity.remove();
        }
    }
}
