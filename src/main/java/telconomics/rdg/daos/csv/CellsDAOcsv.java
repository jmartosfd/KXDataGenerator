package telconomics.rdg.daos.csv;

import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CellsDAOInterface;
import telconomics.rdg.model.Cell;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CellsDAOcsv implements CellsDAOInterface {

    private AppConfig appConfig;

    @Getter
    private String fileLocation;

    private static final int csvRecordUUID = 0;
    private static final int csvRecordLat = 1;
    private static final int csvRecordLong = 2;
    private static final int csvRecordSignalQuality = 3;
    private static final int csvRecordRegion = 4;


    public CellsDAOcsv(AppConfig appConfig){
        this.appConfig = appConfig;
        this.fileLocation = appConfig.getCellsFileLocation();
    }


    @Override
    public void saveCell(Cell cell) {
        try {
            Path path = Paths.get(fileLocation);
            Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE_NEW);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            csvPrinter.printRecord(cell.mapToCSVRecord());
            csvPrinter.flush();
            writer.close();
            System.out.println("Finished writing into " + path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchSaveCells(List<Cell> cells) {
        try {
            Path path = Paths.get(fileLocation);
            Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE_NEW);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            for(Cell c : cells){
                csvPrinter.printRecord(c.mapToCSVRecord());
            }
            csvPrinter.flush();
            writer.close();
            System.out.println("Finished writing into " + path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cell> batchReadCells() {
        List<Cell> parsedCells = new ArrayList<>();
        try {
            Path path = Paths.get(fileLocation);
            Reader reader = Files.newBufferedReader(path);

            Iterable<CSVRecord> fileCells = CSVFormat.DEFAULT.parse(reader);
            fileCells.forEach(csvRecord -> {
                Cell cell = mapCellFromCSVRecord(csvRecord);
                parsedCells.add(cell);
            });
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedCells;
    }

    @Override
    public void updateCell(Cell cell) {
        throw new UnsupportedOperationException();
    }


    private Cell mapCellFromCSVRecord(CSVRecord csvRecord) {
        Coordinate location = Coordinate.builder().latitude(Double.parseDouble(csvRecord.get(csvRecordLat))).longitude(Double.parseDouble(csvRecord.get(csvRecordLong))).build();
        UUID id = UUID.fromString(csvRecord.get(csvRecordUUID));
        double signalQuality = Double.parseDouble(csvRecord.get(csvRecordSignalQuality));
        String region = csvRecord.get(csvRecordRegion);
        return new Cell(id, location, signalQuality, region);


    }
}
