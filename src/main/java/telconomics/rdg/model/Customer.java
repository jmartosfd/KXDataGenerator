package telconomics.rdg.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer implements CSVSerializable, QSerializable {

    private double imsi;
    private String imei;
    private double deviceSignalQuality;

    private String assignedRegion;
    private Coordinate currentLocation;
    private int steps = 0;
    private double laDelta = 0;
    private double lngDelta = 0;

    private Cell lastConnectedCell;


    public Customer(double imsi, String imei, double deviceSignalQuality, String assignedRegion) {
        this.imsi = imsi;
        this.imei = imei;
        this.deviceSignalQuality = deviceSignalQuality;
        this.assignedRegion = assignedRegion;

    }

    public Customer(double imsi, String imei, double signalQuality, String region, Coordinate location) {
        this.imsi = imsi;
        this.imei = imei;
        this.deviceSignalQuality = signalQuality;
        this.assignedRegion = region;
        this.currentLocation = location;

    }

    public void increaseSteps() {
        this.steps += 1;
    }


    @Override
    public String[] mapToCSVRecord() {
        return new String[]{
                String.valueOf(imsi),
                imei,
                String.valueOf(deviceSignalQuality),
                assignedRegion,
                String.valueOf(currentLocation.getLatitude()),
                String.valueOf(currentLocation.getLongitude())
        };
    }

    @Override
    public Object[] mapToQArray() {
        return new Object[]{
                imsi, imei, deviceSignalQuality, assignedRegion, currentLocation.getLatitude(), currentLocation.getLongitude()
        };
    }
}
