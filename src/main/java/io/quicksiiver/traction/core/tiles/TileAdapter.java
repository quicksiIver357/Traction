package main.java.io.quicksiiver.traction.core.tiles;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TileAdapter extends TypeAdapter<Tile> {
    // classifiers, prevents typos that don't compiler error
    // and therefore are harder to catch
    private static final String ID = "id";

    @Override
    public void write(JsonWriter out, Tile value) throws IOException {
        out.beginObject();
        out.name(ID).value(value.getName());
        out.endObject();
    }
    @Override
    public Tile read(JsonReader in) throws IOException {
        JsonObject obj = JsonParser.parseReader(in).getAsJsonObject();
        String name = obj.get(ID).getAsString();
        return Tile.getTileFromName(name);
    }
}