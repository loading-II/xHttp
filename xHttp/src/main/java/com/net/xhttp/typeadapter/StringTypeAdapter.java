package com.net.xhttp.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Created by ibm on 2017/10/27.
 */

public class StringTypeAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) {
        try {
            if (value == null) {
                value = "";
            }
            out.value(value.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String read(JsonReader in) {
        try {
            String value;
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return "";
            }
            if (in.peek() == JsonToken.BOOLEAN) {
                boolean b = in.nextBoolean();
                return Boolean.toString(b);
            }
            value = in.nextString();
            return value;
        } catch (Exception e) {
        }
        return "";
    }
}