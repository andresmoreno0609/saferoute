package com.saferoute.common.config;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte Point de JTS a WKT (Well-Known Text) para PostgreSQL.
 */
@Converter
public class PointAttributeConverter implements AttributeConverter<Point, String> {

    private static final GeometryFactory GF = new GeometryFactory();

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
            return GF.read(wkt).getFactory().createPoint(new Coordinate(
                    ((Point) GF.read(wkt)).getX(),
                    ((Point) GF.read(wkt)).getY()
            ));
        } catch (Exception e) {
            return null;
        }
    }
}