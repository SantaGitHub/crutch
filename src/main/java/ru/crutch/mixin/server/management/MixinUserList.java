package ru.crutch.mixin.server.management;

import com.google.common.collect.Maps;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.crutch.interfaces.server.management.IMixinUserList;

import java.util.Collection;
import java.util.Map;

@Mixin(UserList.class)
public class MixinUserList<K, V extends UserListEntry<K>> implements IMixinUserList {

    @Shadow
    public final Map<String, V> values = Maps.<String, V>newHashMap();

    @Override
    public Collection<V> getValuesCB() {
        return this.values.values();
    }
}
