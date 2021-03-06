package telconomics.rdg.daos.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.RegionsDAOInterface;
import telconomics.rdg.model.BoundingBox;
import telconomics.rdg.model.Cell;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.model.Region;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RegionsDAOcsv implements RegionsDAOInterface {

    @Value("${resources.regions.file}")
    private String fileLocation;

    private static final int csvRecordMinLong = 0;
    private static final int csvRecordMinLat = 1;
    private static final int csvRecordMaxLong = 2;
    private static final int csvRecordMaxLat = 3;
    private static final int csvRecordNumberOfCells = 4;
    private static final int csvRecordRegionName = 5;


    @Override
    public Map<String, Region> readRegions() {
        Map<String, Region> regionsMap = new HashMap<>();

        try {
            Path path = Paths.get(fileLocation);
            Reader reader = Files.newBufferedReader(path);

            Iterable<CSVRecord> regions = CSVFormat.DEFAULT.parse(reader);
            regions.forEach(csvRecord -> {
                Region region = mapRegionFromCSVRecord(csvRecord);
                regionsMap.put(region.getName(), region);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return regionsMap;
    }


    /**
     * Maps a CSV row into a Region object
     *
     * @param csvRecord
     * @return the region representing that particular row.
     */
    private Region mapRegionFromCSVRecord(CSVRecord csvRecord) {
        Coordinate southWest = Coordinate.builder().latitude(Double.parseDouble(csvRecord.get(csvRecordMinLat))).longitude(Double.parseDouble(csvRecord.get(csvRecordMinLong))).build();
        Coordinate northEast = Coordinate.builder().latitude(Double.parseDouble(csvRecord.get(csvRecordMaxLat))).longitude(Double.parseDouble(csvRecord.get(csvRecordMaxLong))).build();
        int numberOfCells = Integer.parseInt(csvRecord.get(csvRecordNumberOfCells));
        String name = csvRecord.get(csvRecordRegionName);
        BoundingBox boundingBox = new BoundingBox(southWest, northEast);
        return new Region(boundingBox, name, numberOfCells);


    }

}
