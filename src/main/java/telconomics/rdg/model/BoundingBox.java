package telconomics.rdg.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
public class BoundingBox {

    private Coordinate southWestCoordinate;
    private Coordinate northEastCoordinate;

    public Coordinate createInnerRandomCoordinate(){
        double diffLat = northEastCoordinate.getLatitude() - southWestCoordinate.getLatitude();
        double diffLng = northEastCoordinate.getLongitude() - southWestCoordinate.getLongitude();

        double lat = northEastCoordinate.getLatitude()-(ThreadLocalRandom.current().nextDouble()*diffLat);
        double lng = northEastCoordinate.getLongitude()-(ThreadLocalRandom.current().nextDouble()*diffLng);
        return new Coordinate.CoordinateBuilder().latitude(lat).longitude(lng).build();
    }

    public Coordinate calculateMiddlePoint(){
        double lng = (northEastCoordinate.getLongitude()+southWestCoordinate.getLongitude())/2;
        double lat = (northEastCoordinate.getLatitude()+southWestCoordinate.getLatitude())/2;

        return new Coordinate(lat,lng);


    }


}
