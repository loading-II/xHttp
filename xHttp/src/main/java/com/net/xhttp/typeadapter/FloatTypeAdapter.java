package com.net.xhttp.typeadapter;

import android.util.Log;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Created by ibm on 2017/10/27.
 */

public class FloatTypeAdapter extends TypeAdapter<Float> {
    @Override
    public void write(JsonWriter out, Float value) {
        try {
            if (value == null) {
                value = 0F;
            }
            out.value(value.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Float read(JsonReader in) {
        try {
            Float value;
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                Log.e("TypeAdapter", "null is not a number");
                return 0F;
            }
            if (in.peek() == JsonToken.BOOLEAN) {
                boolean b = in.nextBoolean();
                Log.e("TypeAdapter", b + " is not a number");
                return 0F;
            }
            if (in.peek() == JsonToken.STRING) {
                String str = in.nextString();
                try {
                    return Float.parseFloat(str);
                } catch (NumberFormatException e) {
                    return 0F;
                }
            } else {
                String str = in.nextString();
                value = Float.valueOf(str);
            }
            return value;
        } catch (Exception e) {
            Log.e("TypeAdapter", "Not a number", e);
        }
        return 0F;
    }
}