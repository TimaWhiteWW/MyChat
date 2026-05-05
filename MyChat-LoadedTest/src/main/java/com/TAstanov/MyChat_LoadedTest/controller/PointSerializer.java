package com.TAstanov.MyChat_LoadedTest.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;

import java.io.IOException;

public class PointSerializer extends JsonSerializer<Point> {
    @Override
    public void serialize(Point point, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (point != null) {
            WKTWriter writer = new WKTWriter();
            String wkt = writer.write(point);  // преобразуем Point в строку WKT
            jsonGenerator.writeString(wkt);  // записываем строку WKT в JSON
        } else {
            jsonGenerator.writeNull();  // если точка null, записываем null в JSON
        }
    }
}
