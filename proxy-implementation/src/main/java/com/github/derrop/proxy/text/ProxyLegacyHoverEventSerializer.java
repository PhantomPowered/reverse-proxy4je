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
package com.github.derrop.proxy.text;

import com.github.derrop.proxy.api.nbt.NBTTagCompound;
import com.github.derrop.proxy.nbt.JsonNbtUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import net.kyori.adventure.util.Codec;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.UUID;

public final class ProxyLegacyHoverEventSerializer implements LegacyHoverEventSerializer {

    public static final LegacyHoverEventSerializer INSTANCE = new ProxyLegacyHoverEventSerializer();

    private ProxyLegacyHoverEventSerializer() {
    }

    @Override
    public HoverEvent.@NonNull ShowItem deserializeShowItem(@NonNull Component input) throws IOException {
        if (!(input instanceof TextComponent)) {
            throw new IllegalStateException(input.getClass().getName() + " != " + TextComponent.class.getName());
        }

        NBTTagCompound compound = JsonNbtUtils.getTagFromJson(((TextComponent) input).content());
        return new HoverEvent.ShowItem(Key.of(compound.getString("id")), compound.getByte("Count"), this.showItemFromNBTTagHolder(compound));
    }

    @Override
    public HoverEvent.@NonNull ShowEntity deserializeShowEntity(@NonNull Component input, Codec.Decoder<Component, String, ? extends RuntimeException> componentDecoder) throws IOException {
        if (!(input instanceof TextComponent)) {
            throw new IllegalStateException(input.getClass().getName() + " != " + TextComponent.class.getName());
        }

        NBTTagCompound compound = JsonNbtUtils.getTagFromJson(((TextComponent) input).content());
        return new HoverEvent.ShowEntity(
                this.getShowItemKeyFromNbt(compound),
                UUID.fromString(compound.getString("id")),
                compound.hasKey("name", 8) ? TextComponent.of(compound.getString("name")) : null
        );
    }

    @Override
    public @NonNull Component serializeShowItem(HoverEvent.@NonNull ShowItem input) throws IOException {
        NBTTagCompound compound = input.nbt() == null ? new NBTTagCompound() : JsonNbtUtils.getTagFromJson(input.nbt().string());
        compound.setString("id", input.item().value());
        compound.setByte("Count", (byte) input.count());

        return TextComponent.of(compound.toString());
    }

    @Override
    public @NonNull Component serializeShowEntity(HoverEvent.@NonNull ShowEntity input, Codec.Encoder<Component, String, ? extends RuntimeException> componentEncoder) {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setString("id", input.id().toString());
        if (!input.type().value().equals("player")) {
            compound.setString("type", input.type().value());
        }

        if (input.name() != null && input.name() instanceof TextComponent) {
            compound.setString("name", ((TextComponent) input.name()).content());
        }

        return TextComponent.of(compound.toString());
    }

    private BinaryTagHolder showItemFromNBTTagHolder(NBTTagCompound compound) {
        compound.removeTag("id");
        compound.removeTag("count");
        return BinaryTagHolder.of(compound.toString());
    }

    private Key getShowItemKeyFromNbt(NBTTagCompound compound) {
        if (compound.hasKey("type", 8)) {
            return Key.of(compound.getString("type"));
        }

        return Key.of("player");
    }
}
