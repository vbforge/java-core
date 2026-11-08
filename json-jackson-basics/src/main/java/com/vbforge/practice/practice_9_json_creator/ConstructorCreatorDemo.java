package com.vbforge.practice.practice_9_json_creator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** @JsonCreator on a constructor lets an immutable class (final fields, no setters) be read. */
public class ConstructorCreatorDemo {

    public static final class GeoPoint {
        private final double lat;
        private final double lon;

        @JsonCreator
        public GeoPoint(@JsonProperty("lat") double lat, @JsonProperty("lon") double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() { return lat; }
        public double getLon() { return lon; }

        @Override
        public String toString() { return "GeoPoint[lat=" + lat + ", lon=" + lon + "]"; }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        GeoPoint point = mapper.readValue("{\"lat\":50.45,\"lon\":30.52}", GeoPoint.class);
        System.out.println("read : " + point);
        System.out.println("write: " + mapper.writeValueAsString(point));
    }
}
