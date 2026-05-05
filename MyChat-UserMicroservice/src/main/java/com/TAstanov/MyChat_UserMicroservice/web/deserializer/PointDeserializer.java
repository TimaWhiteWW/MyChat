package com.TAstanov.MyChat_UserMicroservice.web.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

import java.io.IOException;

public class PointDeserializer extends JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String wkt = p.getText();
        try {
            WKTReader reader = new WKTReader();
            return (Point) reader.read(wkt);
        } catch (Exception e) {
            throw new IOException("Failed to parse Point", e);
        }
    }
}
