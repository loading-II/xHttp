package com.net.xhttp.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Created by ibm on 2017/10/26.
 */

public class DoubleTypeAdapter extends TypeAdapter<Double> {
    @Override
    public void write(JsonWriter out, Double value) {
        try {
            if (value == null) {
                value = 0D;
            }
            out.value(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Double read(JsonReader in) {
        try {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return 0D;
            }
            if (in.peek() == JsonToken.BOOLEAN) {
                boolean b = in.nextBoolean();
                return 0D;
            }
            if (in.peek() == JsonToken.STRING) {
                String str = in.nextString();
                try {
                    return Double.parseDouble(str);
                } catch (NumberFormatException e) {
                    return 0D;
                }
            } else {
                Double value = in.nextDouble();
                return value == null ? 0D : value;
            }
        } catch (NumberFormatException e) {
        } catch (Exception e) {
        }
        return 0D;
    }
}
