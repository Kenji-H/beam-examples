package com.byam.beam.examples;

import com.google.gson.Gson;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

@DefaultCoder(AvroCoder.class)
public class StorageNotification {

    @Nullable String kind;
    @Nullable String id;
    @Nullable String selfLink;
    @Nullable String name;
    @Nullable String bucket;
    @Nullable String generation;
    @Nullable String metageneration;
    @Nullable String contentType;
    @Nullable String timeCreated;
    @Nullable String updated;
    @Nullable String storageClass;
    @Nullable String timeStorageClassUpdated;
    @Nullable Integer size;
    @Nullable String md5Hash;
    @Nullable String mediaLink;
    @Nullable String contentLanguage;
    @Nullable String crc32c;
    @Nullable String etag;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
