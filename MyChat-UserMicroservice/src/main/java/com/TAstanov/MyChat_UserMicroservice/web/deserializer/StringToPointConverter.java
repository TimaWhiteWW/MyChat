package com.TAstanov.MyChat_UserMicroservice.web.deserializer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPointConverter implements Converter<String, Point> {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public Point convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            String[] coordinates = source.replace("POINT (", "").replace(")", "").split(" ");
            double lon = Double.parseDouble(coordinates[0]);
            double lat = Double.parseDouble(coordinates[1]);
            return geometryFactory.createPoint(new Coordinate(lon, lat));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Point format. Expected format: 'POINT (longitude latitude)'");
        }
    }
}
