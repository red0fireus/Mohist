package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.state.BooleanProperty;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftHopper extends CraftBlockData implements Hopper {

    private static final BooleanProperty ENABLED = getBoolean("enabled");

    @Override
    public boolean isEnabled() {
        return get(ENABLED);
    }

    @Override
    public void setEnabled(boolean enabled) {
        set(ENABLED, enabled);
    }
}
