/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

import net.minecraft.state.BooleanProperty;

public final class CraftPressurePlateBinary extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Powerable {

    public CraftPressurePlateBinary() {
        super();
    }

    public CraftPressurePlateBinary(net.minecraft.block.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftPowerable

    private static final BooleanProperty POWERED = getBoolean(net.minecraft.block.PressurePlateBlock.class, "powered");

    @Override
    public boolean isPowered() {
        return get(POWERED);
    }

    @Override
    public void setPowered(boolean powered) {
        set(POWERED, powered);
    }
}
