/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftWitherSkullWall extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Directional {

    public CraftWitherSkullWall() {
        super();
    }

    public CraftWitherSkullWall(net.minecraft.block.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftDirectional

    private static final net.minecraft.state.PropertyEnum<?> FACING = getEnum(net.minecraft.block.WitherSkeletonSkullBlockWall.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(FACING, org.bukkit.block.BlockFace.class);
    }
}
