package telconomics.rdg.model;

import lombok.Data;

import java.util.Date;
import java.util.Random;

@Data
public class ConnectionRecord implements CSVSerializable, QSerializable {

    public static float minDownloadQuality = 15;
    public static float maxDownloadQuality = 500;
    public static float minUploadQuality = 10F;
    public static float maxUploadQuality = 100;
    public static float normalRange = 8;
    public static float normalOldMin = -4;
    public static float normalNewMin = 0;

    CellState connectedCell;
    String customerIMSI;
    String customerIMEI;
    double downloadSpeed;
    double uploadSpeed;
    Coordinate userLocation;
    double distanceToCell;
    Date timestamp;



    /**
     * Interpolate values: https://stackoverflow.com/questions/67974238/how-to-interpolate-a-value-in-one-range-into-another
     *
     * @param customer
     * @param cell
     */
    public ConnectionRecord(Customer customer, Cell cell, double distanceToCell) {
        connectedCell = cell.getCurrentCellState();
        customerIMSI = customer.getImsi();
        customerIMEI = customer.getImei();

        double signalQuality = (customer.getDeviceSignalQuality() + cell.getSignalQuality()) / 2;
        Random random = new Random();
        double dspeedRnd = random.nextDouble()*5;
        double uspeedRnd = random.nextDouble()*3;
        CellState currentCellState = cell.getCurrentCellState();

        downloadSpeed = calculateNewValueInRange(signalQuality, minDownloadQuality, maxDownloadQuality) * currentCellState.getIntegrity() - distanceToCell*5 + dspeedRnd;
        uploadSpeed = calculateNewValueInRange(signalQuality, minUploadQuality, maxUploadQuality) * currentCellState.getIntegrity() - distanceToCell*2 + uspeedRnd;

        userLocation = customer.getCurrentLocation();
        this.timestamp = new Date();
        this.distanceToCell = distanceToCell;

    }


    private double calculateNewValueInRange(double signalQuality, float minQuality, float maxQuality){
        float qualityRange = maxQuality - minQuality;
        return (((signalQuality - normalNewMin) * qualityRange) / normalRange) + minQuality;
    }



    public String[] mapToCSVRecord(){
        return new String[]{
                connectedCell.getCellID().toString(),
                String.valueOf(connectedCell.getPhase()),
                customerIMSI,
                customerIMEI,
                String.valueOf(downloadSpeed),
                String.valueOf(uploadSpeed),
                String.valueOf(userLocation.getLatitude()),
                String.valueOf(userLocation.getLongitude()),
                timestamp.toString()
        };
    }


    public Object[] mapToQArray(){
        return new Object[]{
                timestamp, connectedCell.getCellID().toString(), connectedCell.getPhase(),
                customerIMSI, customerIMEI, downloadSpeed, uploadSpeed,
                userLocation.getLatitude(), userLocation.getLongitude(), distanceToCell
        };
    }

}
