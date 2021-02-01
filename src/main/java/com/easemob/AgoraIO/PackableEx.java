package com.easemob.AgoraIO;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
