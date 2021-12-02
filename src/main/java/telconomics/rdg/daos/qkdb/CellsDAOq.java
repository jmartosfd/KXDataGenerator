package telconomics.rdg.daos.qkdb;

import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CellsDAOInterface;
import telconomics.rdg.model.Cell;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CellsDAOq implements CellsDAOInterface {


    private QConnection qConnection;
    private AppConfig appConfig;

    private static final String QINSERT = "insert";
    private static String QSELECT = "select from ";
    private static final String QUPDATE = "u.upd";
    private String tableName;

    public CellsDAOq(QConnection qConnection, AppConfig appConfig) {
        this.qConnection = qConnection;
        this.appConfig = appConfig;
        tableName = appConfig.getCellsTableName();
        QSELECT+=tableName;
        if(appConfig.isGenerateData()){
            createSchema();
        }

    }


    private void createSchema(){
        String table = tableName + ":([cellID:`symbol$()] " +
                "lat: `float$();" +
                "lng: `float$();" +
                "signalQuality: `float$();" +
                "region: `symbol$())";

        try {
            qConnection.getQ().k(table);
        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveCell(Cell cell) {
        try {
            qConnection.getQ().ks(QINSERT, tableName, cell.mapToQArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchSaveCells(List<Cell> cells) {
        cells.forEach(this::saveCell);
    }

    @Override
    public List<Cell> batchReadCells() {
        try {
            c.Flip res = c.td(qConnection.getQ().k(QSELECT));
            Object[] columnData = res.y;
            String[] uuids = (String[]) columnData[0];
            double[] lats = (double[]) columnData[1];
            double[] lngs = (double[]) columnData[2];
            double[] squal = (double[]) columnData[3];
            String[] regions = (String[]) columnData[4];

            List<Cell> cells = new ArrayList<>();
            for (int i = 0; i < uuids.length; i++) {
                Cell c = new Cell(UUID.fromString(uuids[i]),
                        Coordinate.builder().latitude(lats[i]).longitude(lngs[i]).build(),
                        squal[i], regions[i]);
                cells.add(c);
            }
            return cells;
        } catch (IOException | c.KException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect");
        }
    }

    @Override
    public void updateCell(Cell cell) {
        try {
            qConnection.getQ().ks("upsert", tableName,  cell.mapToQArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
