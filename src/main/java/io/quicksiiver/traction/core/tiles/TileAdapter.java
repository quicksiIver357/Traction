package main.java.io.quicksiiver.traction.core.tiles;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TileAdapter extends TypeAdapter<Tile> {
    // classifiers, prevents typos that don't compiler error
    // and therefore are harder to catch
    private static final String NAME = "name";

    @Override
    public void write(JsonWriter out, Tile value) throws IOException {
        JsonObject obj = new JsonObject();
        obj.add(NAME, JsonParser.parseString(value.getName())); // only load necessary information
        Streams.write(obj, out); // writes a complete JSON object safely
    }
    @Override
    public Tile read(JsonReader in) throws IOException {
        JsonObject obj = JsonParser.parseReader(in).getAsJsonObject();
        String name = obj.get(NAME).getAsString();
        return Tile.getTileFromName(name);
    }
}