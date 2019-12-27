package org.bukkit.craftbukkit.entity.memory;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.LongSerializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

public final class CraftMemoryMapper {

    private CraftMemoryMapper() {}

    public static Object fromNms(Object object) {
        if (object instanceof GlobalPos) {
            return fromNms((GlobalPos) object);
        } else if (object instanceof IDynamicSerializableLong) {
            return ((IDynamicSerializableLong) object).a();
        }

        throw new UnsupportedOperationException("Do not know how to map " + object);
    }

    public static Object toNms(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Location) {
            return toNms((Location) object);
        } else if (object instanceof Long) {
            return IDynamicSerializableLong.a((Long) object);
        }

        throw new UnsupportedOperationException("Do not know how to map " + object);
    }

    public static Location fromNms(GlobalPos globalPos) {
        return new org.bukkit.Location(((CraftServer) Bukkit.getServer()).getServer().getWorldServer(globalPos.getDimensionManager()).getWorld(), globalPos.getBlockPosition().getX(), globalPos.getBlockPosition().getY(), globalPos.getBlockPosition().getZ());
    }

    public static GlobalPos toNms(Location location) {
        return GlobalPos.create(((CraftWorld) location.getWorld()).getHandle().getWorldProvider().getDimensionManager(), new BlockPosition(location.getX(), location.getY(), location.getZ()));
    }
}
