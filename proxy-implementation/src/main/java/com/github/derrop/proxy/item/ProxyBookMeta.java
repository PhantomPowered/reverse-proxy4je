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
package com.github.derrop.proxy.item;

import com.github.derrop.proxy.api.item.BookMeta;
import com.github.derrop.proxy.api.nbt.NBTTagCompound;
import com.github.derrop.proxy.api.nbt.NBTTagList;
import com.github.derrop.proxy.api.nbt.NBTTagString;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProxyBookMeta extends ProxyItemMeta implements BookMeta {

    static {
        ProxyItemMeta.HANDLED.addAll(Arrays.asList(
                BookMetaKeys.AUTHOR,
                BookMetaKeys.GENERATION,
                BookMetaKeys.PAGES,
                BookMetaKeys.RESOLVED,
                BookMetaKeys.TITLE
        ));
    }

    public ProxyBookMeta(@NotNull NBTTagCompound source, boolean assertResolved) {
        super(source);

        if (source.hasKey(BookMetaKeys.TITLE)) {
            this.title = limit(source.getString(BookMetaKeys.TITLE), MAX_TITLE_LENGTH); // vanilla limit
        }

        if (source.hasKey(BookMetaKeys.AUTHOR)) {
            this.author = limit(source.getString(BookMetaKeys.AUTHOR), 1024); // vanilla limit
        }

        boolean resolved = assertResolved;
        if (source.hasKey(BookMetaKeys.RESOLVED)) {
            resolved = source.getBoolean(BookMetaKeys.RESOLVED);
        }

        if (source.hasKey(BookMetaKeys.GENERATION)) {
            this.generation = source.getInteger(BookMetaKeys.GENERATION);
        }

        if (source.hasKey(BookMetaKeys.PAGES)) {
            NBTTagList pages = source.getTagList(BookMetaKeys.PAGES, NbtTagNumbers.TAG_STRING);
            for (int i = 0; i < Math.min(Short.MAX_VALUE, pages.tagCount()); i++) {
                String page = pages.getStringTagAt(i);
                if (resolved) {
                    try {
                        this.pages.add((TextComponent) GsonComponentSerializer.INSTANCE.deserialize(page));
                    } catch (Throwable ignored) {
                    }
                } else {
                    this.pages.add(TextComponent.of(limit(page, 2048)));
                }
            }
        }
    }

    private static final int MAX_TITLE_LENGTH = 65535;

    private String title;
    private String author;
    private List<TextComponent> pages = new ArrayList<>();
    private Integer generation;

    @Override
    public boolean hasTitle() {
        return this.title != null;
    }

    @Override
    public @Nullable String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(@Nullable String title) {
        this.title = title == null ? null : limit(title, MAX_TITLE_LENGTH);
    }

    @Override
    public boolean hasAuthor() {
        return this.author != null;
    }

    @Override
    public @Nullable String getAuthor() {
        return this.author;
    }

    @Override
    public void setAuthor(@Nullable String author) {
        this.author = author;
    }

    @Override
    public boolean hasPages() {
        return this.pages.size() > 0;
    }

    @Override
    public @Nullable TextComponent getPage(int page) {
        return this.pages.get(page);
    }

    @Override
    public void setPage(int page, @NotNull TextComponent data) {
        if (this.pages.size() < page) {
            return;
        }

        this.pages.set(page, data);
    }

    @Override
    public @NotNull List<TextComponent> getPages() {
        return this.pages;
    }

    @Override
    public void setPages(@NotNull List<TextComponent> pages) {
        this.pages = pages;
    }

    @Override
    public void setPages(TextComponent... pages) {
        this.pages = new ArrayList<>(Arrays.asList(pages));
    }

    @Override
    public void addPage(TextComponent... pages) {
        this.pages.addAll(Arrays.asList(pages));
    }

    @Override
    public boolean hasGeneration() {
        return this.generation != null;
    }

    @Override
    public @Nullable Integer getGeneration() {
        return this.generation;
    }

    @Override
    public void setGeneration(@Nullable Integer generation) {
        this.generation = generation == null ? null : generation < 0 ? 0 : generation;
    }

    @Override
    public @NotNull NBTTagCompound write() {
        NBTTagCompound compound = super.write();

        if (this.hasTitle()) {
            compound.setString(BookMetaKeys.TITLE, this.title);
        }

        if (this.hasAuthor()) {
            compound.setString(BookMetaKeys.AUTHOR, this.author);
        }

        if (this.hasPages()) {
            compound.setTag(BookMetaKeys.PAGES, new NBTTagList());
            for (TextComponent page : this.pages) {
                compound.getTagList(BookMetaKeys.PAGES, NbtTagNumbers.TAG_STRING).appendTag(new NBTTagString(GsonComponentSerializer.INSTANCE.serialize(page)));
            }

            compound.setBoolean(BookMetaKeys.RESOLVED, true);
        }

        if (this.hasGeneration()) {
            compound.setInteger(BookMetaKeys.GENERATION, this.generation);
        }

        return compound;
    }

    public interface BookMetaKeys {

        String TITLE = "title";
        String AUTHOR = "author";
        String PAGES = "pages";
        String RESOLVED = "resolved";
        String GENERATION = "generation";
    }
}
