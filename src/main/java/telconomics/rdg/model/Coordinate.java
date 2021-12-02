package telconomics.rdg.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coordinate {

    private double latitude;
    private double longitude;

    /**
     * Follows Harvesine formula to calculate distance
     * @param coordinate
     * @return distance between coordinates in kilometers
     */
    public double harvesineDistance(Coordinate coordinate){
        int earthRadius = 6371;
        double dLat= toRadian(coordinate.getLatitude()-latitude);
        double dLng = toRadian(coordinate.getLongitude()-longitude);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(toRadian(coordinate.getLatitude())) * Math.cos(toRadian(latitude))*
                Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius*c;

    }

    /**
     * Tarda casi 10 veces más que harvesineDistance...
     * @param coordinate
     * @return
     */
    public double simplifiedDistance(Coordinate coordinate){
        double c1[]=this.toXY();
        double c2[]= coordinate.toXY();
        double ac = Math.abs(c2[1]-c1[1]);
        double cb = Math.abs(c2[0]-c1[0]);
        return Math.hypot(ac,cb);

    }

    private double toRadian(double deg){
        return deg * Math.PI/180;
    }

    public double[] toXY(){
        int earthRadius = 6371;
        double x = earthRadius * Math.cos(latitude)*Math.cos(longitude);
        double y = earthRadius * Math.cos(latitude) * Math.sin(longitude);
        double ret[] = {x,y};
        return ret;

    }


    public double distance2D(Coordinate coordinate){
        double c1[] = toXY();
        double c2[] = coordinate.toXY();

        return Math.sqrt(Math.pow(c2[0]-c1[0],2) + Math.pow(c2[1]-c1[1],2));

    }

}
