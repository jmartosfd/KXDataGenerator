package telconomics.rdg.daos.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.ConnectionRecordsDAOInterface;
import telconomics.rdg.model.ConnectionRecord;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

@Repository
public class ConnectionRecordsDAOcsv implements ConnectionRecordsDAOInterface {


    private int csvRecordCellID = 0;
    private int csvRecordCellPhase = 1;
    private int csvRecordIMSI = 2;
    private int csvRecordIMEI = 3;
    private int csvRecordDSpeed = 4;
    private int csvRecordUSpeed = 5;
    private int csvRecordLat = 6;
    private int csvRecordLng = 7;
    private int csvRecordTimeStamp = 8;

    private String fileLocation;

    public ConnectionRecordsDAOcsv(AppConfig appConfig) {
        this.fileLocation = appConfig.getConnectionRecordsFileLocation();
    }


    @Override
    public void addConnectionRecord(ConnectionRecord connectionRecord) {
        try {
            Path path = Paths.get(fileLocation);
            Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            csvPrinter.printRecord(connectionRecord.mapToCSVRecord());
            csvPrinter.flush();
            writer.close();
            System.out.println("Finished writing into " + path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchInsertConnectionRecord(List<?> records) {
        try {
            Path path = Paths.get(fileLocation);
            Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            int numberOfRecords = records.size();
            for (Object record : records) {
                ConnectionRecord connectionRecord = (ConnectionRecord) record;
                csvPrinter.printRecord(connectionRecord.mapToCSVRecord());
            }
            csvPrinter.flush();
            writer.close();
            System.out.println("Wrote: " + numberOfRecords + " into " + fileLocation);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Coordinate> recoverLastPositions() {
        return null;
    }
}
