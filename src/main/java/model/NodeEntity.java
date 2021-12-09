package model;

import java.util.HashMap;
import java.util.Map;

public class NodeEntity {

    private final long id;
    private double lat;
    private double lon;
    private final Map<String, String> tags = new HashMap<>();

    public NodeEntity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

}
