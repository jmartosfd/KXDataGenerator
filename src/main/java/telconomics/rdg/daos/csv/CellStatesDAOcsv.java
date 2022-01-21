package telconomics.rdg.daos.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CellStatesDAOInterface;
import telconomics.rdg.model.CellState;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class CellStatesDAOcsv implements CellStatesDAOInterface {

    private int csvRecordCellID = 0;
    private int csvRecordPhase = 1;
    private int csvRecordIntegrity = 2;
    private int csvRecordTimestamp = 3;

    private String fileLocation;


    public CellStatesDAOcsv(AppConfig appConfig){
        this.fileLocation = appConfig.getCellStatesFileLocation();
    }


    @Override
    public void batchSaveCellStates(List<CellState> cellStates) {
        try {
            Path path = Paths.get(fileLocation);
            Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE_NEW);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            for(CellState c : cellStates){
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
    public void saveCellState(CellState cellState) {
        try {
            Path path = Paths.get(fileLocation);
            Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            csvPrinter.printRecord(cellState.mapToCSVRecord());
            csvPrinter.flush();
            writer.close();
            System.out.println("Finished writing into " + path);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Map<UUID, List<CellState>> batchReadCellStates() {
        return null;
    }
}
