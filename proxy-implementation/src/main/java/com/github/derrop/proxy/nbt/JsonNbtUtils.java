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
package com.github.derrop.proxy.nbt;

import com.github.derrop.proxy.api.nbt.*;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Pattern;

public final class JsonNbtUtils {

    private static final Pattern DOUBLE_PATTERN_NO_SUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", Pattern.CASE_INSENSITIVE);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", Pattern.CASE_INSENSITIVE);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", Pattern.CASE_INSENSITIVE);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", Pattern.CASE_INSENSITIVE);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");

    public static NBTTagCompound getTagFromJson(String jsonString) throws NBTException {
        return new JsonNbtUtils(jsonString).readSingleStruct();
    }

    // END OF STATIC METHODS

    private JsonNbtUtils(String stringIn) {
        this.string = stringIn;
    }

    private final String string;
    private int cursor;

    private NBTTagCompound readSingleStruct() throws NBTException {
        NBTTagCompound nbttagcompound = this.readStruct();
        this.skipWhitespace();

        if (this.canRead()) {
            ++this.cursor;
            throw this.exception("Trailing data found");
        } else {
            return nbttagcompound;
        }
    }

    protected String readKey() throws NBTException {
        this.skipWhitespace();

        if (!this.canRead()) {
            throw this.exception("Expected key");
        } else {
            return this.peek() == '"' ? this.readQuotedString() : this.readString();
        }
    }

    private NBTException exception(String message) {
        return new NBTException(message, this.string, this.cursor);
    }

    protected NBTBase readTypedValue() throws NBTException {
        this.skipWhitespace();

        if (this.peek() == '"') {
            return new NBTTagString(this.readQuotedString());
        } else {
            String s = this.readString();

            if (s.isEmpty()) {
                throw this.exception("Expected value");
            } else {
                return this.type(s);
            }
        }
    }

    private NBTBase type(String stringIn) {
        try {
            if (FLOAT_PATTERN.matcher(stringIn).matches()) {
                return new NBTTagFloat(Float.parseFloat(stringIn.substring(0, stringIn.length() - 1)));
            }

            if (BYTE_PATTERN.matcher(stringIn).matches()) {
                return new NBTTagByte(Byte.parseByte(stringIn.substring(0, stringIn.length() - 1)));
            }

            if (LONG_PATTERN.matcher(stringIn).matches()) {
                return new NBTTagLong(Long.parseLong(stringIn.substring(0, stringIn.length() - 1)));
            }

            if (SHORT_PATTERN.matcher(stringIn).matches()) {
                return new NBTTagShort(Short.parseShort(stringIn.substring(0, stringIn.length() - 1)));
            }

            if (INT_PATTERN.matcher(stringIn).matches()) {
                return new NBTTagInt(Integer.parseInt(stringIn));
            }

            if (DOUBLE_PATTERN.matcher(stringIn).matches()) {
                return new NBTTagDouble(Double.parseDouble(stringIn.substring(0, stringIn.length() - 1)));
            }

            if (DOUBLE_PATTERN_NO_SUFFIX.matcher(stringIn).matches()) {
                return new NBTTagDouble(Double.parseDouble(stringIn));
            }

            if ("true".equalsIgnoreCase(stringIn)) {
                return new NBTTagByte((byte) 1);
            }

            if ("false".equalsIgnoreCase(stringIn)) {
                return new NBTTagByte((byte) 0);
            }
        } catch (NumberFormatException ignored) {
        }

        return new NBTTagString(stringIn);
    }

    private String readQuotedString() throws NBTException {
        int i = ++this.cursor;
        StringBuilder stringbuilder = null;
        boolean flag = false;

        while (this.canRead()) {
            char c0 = this.pop();

            if (flag) {
                if (c0 != '\\' && c0 != '"') {
                    throw this.exception("Invalid escape of '" + c0 + "'");
                }

                flag = false;
            } else {
                if (c0 == '\\') {
                    flag = true;

                    if (stringbuilder == null) {
                        stringbuilder = new StringBuilder(this.string.substring(i, this.cursor - 1));
                    }

                    continue;
                }

                if (c0 == '"') {
                    return stringbuilder == null ? this.string.substring(i, this.cursor - 1) : stringbuilder.toString();
                }
            }

            if (stringbuilder != null) {
                stringbuilder.append(c0);
            }
        }

        throw this.exception("Missing termination quote");
    }

    private String readString() {
        int i;
        for (i = this.cursor; this.canRead() && this.isAllowedInKey(this.peek()); ++this.cursor) {
        }

        return this.string.substring(i, this.cursor);
    }

    protected NBTBase readValue() throws NBTException {
        this.skipWhitespace();

        if (!this.canRead()) {
            throw this.exception("Expected value");
        } else {
            char c0 = this.peek();

            if (c0 == '{') {
                return this.readStruct();
            } else {
                return c0 == '[' ? this.readList() : this.readTypedValue();
            }
        }
    }

    protected NBTBase readList() throws NBTException {
        return this.canRead(2) && this.peek(1) != '"' && this.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
    }

    protected NBTTagCompound readStruct() throws NBTException {
        this.expect('{');
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.skipWhitespace();

        while (this.canRead() && this.peek() != '}') {
            String s = this.readKey();
            if (s.isEmpty()) {
                throw this.exception("Expected non-empty key");
            }

            this.expect(':');
            nbttagcompound.setTag(s, this.readValue());
            if (!this.hasElementSeparator()) {
                break;
            }

            if (!this.canRead()) {
                throw this.exception("Expected key");
            }
        }

        this.expect('}');
        return nbttagcompound;
    }

    private NBTBase readListTag() throws NBTException {
        this.expect('[');
        this.skipWhitespace();

        if (!this.canRead()) {
            throw this.exception("Expected value");
        } else {
            NBTTagList nbttaglist = new NBTTagList();
            int i = -1;

            while (this.peek() != ']') {
                NBTBase nbtbase = this.readValue();
                int j = nbtbase.getId();

                if (i < 0) {
                    i = j;
                } else if (j != i) {
                    throw this.exception("Unable to insert " + NBTBase.getTagTypeName(j) + " into ListTag of type " + NBTBase.getTagTypeName(i));
                }

                nbttaglist.appendTag(nbtbase);

                if (!this.hasElementSeparator()) {
                    break;
                }

                if (!this.canRead()) {
                    throw this.exception("Expected value");
                }
            }

            this.expect(']');
            return nbttaglist;
        }
    }

    private NBTBase readArrayTag() throws NBTException {
        this.expect('[');
        char c0 = this.pop();
        this.pop();
        this.skipWhitespace();

        if (!this.canRead()) {
            throw this.exception("Expected value");
        } else if (c0 == 'B') {
            return new NBTTagByteArray(NBTTagByteArray.toArray(this.readArray((byte) 7, (byte) 1)));
        } else if (c0 == 'I') {
            return new NBTTagIntArray(NBTTagIntArray.toArray(this.readArray((byte) 11, (byte) 3)));
        } else {
            throw this.exception("Invalid array type '" + c0 + "' found");
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Number> List<T> readArray(byte arrayType, byte expectedId) throws NBTException {
        List<T> list = Lists.newArrayList();

        while (true) {
            if (this.peek() != ']') {
                NBTBase nbtbase = this.readValue();
                int i = nbtbase.getId();

                if (i != expectedId) {
                    throw this.exception("Unable to insert " + NBTBase.getTagTypeName(i) + " into " + NBTBase.getTagTypeName(arrayType));
                }

                if (expectedId == 1) {
                    list.add((T) Byte.valueOf(((NBTBase.NBTPrimitive) nbtbase).getByte()));
                } else if (expectedId == 4) {
                    list.add((T) Long.valueOf(((NBTBase.NBTPrimitive) nbtbase).getLong()));
                } else {
                    list.add((T) Integer.valueOf(((NBTBase.NBTPrimitive) nbtbase).getInt()));
                }

                if (this.hasElementSeparator()) {
                    if (!this.canRead()) {
                        throw this.exception("Expected value");
                    }

                    continue;
                }
            }

            this.expect(']');
            return list;
        }
    }

    private void skipWhitespace() {
        while (this.canRead() && Character.isWhitespace(this.peek())) {
            ++this.cursor;
        }
    }

    private boolean hasElementSeparator() {
        this.skipWhitespace();

        if (this.canRead() && this.peek() == ',') {
            ++this.cursor;
            this.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    private void expect(char expected) throws NBTException {
        this.skipWhitespace();
        boolean flag = this.canRead();

        if (flag && this.peek() == expected) {
            ++this.cursor;
        } else {
            throw new NBTException("Expected '" + expected + "' but got '" + (flag ? this.peek() : "<EOF>") + "'", this.string, this.cursor + 1);
        }
    }

    protected boolean isAllowedInKey(char charIn) {
        return charIn >= '0' && charIn <= '9' || charIn >= 'A' && charIn <= 'Z' || charIn >= 'a' && charIn <= 'z' || charIn == '_' || charIn == '-' || charIn == '.' || charIn == '+';
    }

    private boolean canRead(int p_193608_1_) {
        return this.cursor + p_193608_1_ < this.string.length();
    }

    boolean canRead() {
        return this.canRead(0);
    }

    private char peek(int p_193597_1_) {
        return this.string.charAt(this.cursor + p_193597_1_);
    }

    private char peek() {
        return this.peek(0);
    }

    private char pop() {
        return this.string.charAt(this.cursor++);
    }
}
