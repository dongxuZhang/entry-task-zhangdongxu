package utils;

import static constant.CommonConstant.EARTH_RADIUS;

public class CommonUtils {

    public static Double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double h = haverSin(latDistance) + Math.cos(lat1) * Math.cos(lat2) * haverSin(lonDistance);
        return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(h));
    }

    private static double haverSin(double theta) {
        return Math.pow(Math.sin(theta / 2), 2);
    }

}
