package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LabyPacketAddonMessage extends LabyPacket {
    private String key;
    private byte[] data;

    public LabyPacketAddonMessage(String key, byte[] data) {
        this.key = key;
        this.data = data;
    }

    public LabyPacketAddonMessage(String key, String json) {
        this.key = key;
        this.data = toBytes(json);
    }


    public LabyPacketAddonMessage() {
    }

    public String getKey() {
        return this.key;
    }

    public byte[] getData() {
        return this.data;
    }

    public void read(ProtoBuf buf) {
        this.key = LabyBufferUtils.readString(buf);

        byte[] data = new byte[buf.readInt()];
        buf.readBytes(data);
        this.data = data;
    }

    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.key);
        buf.writeInt(this.data.length);
        buf.writeBytes(this.data);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public String getJson() {
        try {
            StringBuilder outStr = new StringBuilder();
            if (this.data == null || this.data.length == 0) {
                return "";
            }
            if (isCompressed(this.data)) {
                GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(this.data));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    outStr.append(line);
                }
            } else {
                outStr.append(Arrays.toString(this.data));
            }
            return outStr.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private byte[] toBytes(String in) {
        byte[] str = in.getBytes(StandardCharsets.UTF_8);

        try {
            if (str == null || str.length == 0) {
                return new byte[0];
            }
            ByteArrayOutputStream obj = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(obj);
            gzip.write(str);
            gzip.flush();
            gzip.close();
            return obj.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean isCompressed(byte[] compressed) {
        return (compressed[0] == 31 && compressed[1] == -117);
    }
}


