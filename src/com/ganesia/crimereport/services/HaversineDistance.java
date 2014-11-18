package com.ganesia.crimereport.services;


import com.google.android.gms.maps.model.LatLng;

/**
 * This is the implementation Haversine Distance Algorithm between two places
 * @author ananth
 * @takenFrom http://bigdatanerd.wordpress.com/2011/11/03/java-implementation-of-haversine-formula-for-distance-calculation-between-two-points/
 * @modifiedBy Raymond Lukanta
 * 
 *  R = earth’s radius (mean radius = 6,371km)
    Δlat = lat2− lat1
    Δlong = long2− long1
    a = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)
    c = 2.atan2(√a, √(1−a))
    d = R.c
 *
 */
 
public class HaversineDistance {
 
    public static double calculateDistance(LatLng startPosition, LatLng endPosition) {
        final int R = 6371; // Radius of the earth
        Double lat1 = startPosition.latitude;
        Double lon1 = startPosition.longitude;
        Double lat2 = endPosition.latitude;
        Double lon2 = endPosition.longitude;
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                   Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * 
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        Double distance = R * c;
     // simplify haversine:
        return (2 * R * 1000 * Math.asin(Math.sqrt(a)));
    }
     
    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }
 
}