/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

import net.minecraft.state.EnumProperty;

public final class CraftSkullWall extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Directional {

    public CraftSkullWall() {
        super();
    }

    public CraftSkullWall(net.minecraft.block.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftDirectional

    private static final EnumProperty<?> FACING = getEnum(net.minecraft.block.SkullPlayerBlock.class, "facing");

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
