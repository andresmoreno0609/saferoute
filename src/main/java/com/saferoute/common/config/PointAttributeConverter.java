package com.saferoute.common.config;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte Point de JTS a WKT (Well-Known Text) para PostgreSQL.
 */
@Converter
public class PointAttributeConverter implements AttributeConverter<Point, String> {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final WKTReader READER = new WKTReader(GF);

    @Override
    public String convertToDatabaseColumn(Point point) {
        if (point == null) {
            return null;
        }
        return point.toText(); // "POINT(long lat)"
    }

    @Override
    public Point convertToEntityAttribute(String wkt) {
        if (wkt == null || wkt.isBlank()) {
            return null;
        }
        try {
            Point parsed = (Point) READER.read(wkt);
            return GF.createPoint(new Coordinate(parsed.getX(), parsed.getY()));
        } catch (Exception e) {
            return null;
        }
    }
}