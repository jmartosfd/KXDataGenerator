package telconomics.rdg.daos.qkdb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(
        value="q.connect",
        havingValue = "True",
        matchIfMissing = false
)
public class CellsDAOq implements CellsDAOInterface {

    private AppConfig appConfig;
    private QConnection qConnection;
    private String QUPDATE;
    private String QINSERT = "insert";
    private String QSELECT = "select from ";
    private String tableName;

    public CellsDAOq(QConnection qConnection, AppConfig appConfig) {
        this.appConfig = appConfig;
        this.qConnection = qConnection;
        this.QUPDATE = appConfig.getUpdate();
        this.tableName = appConfig.getCellsTableName();
        this.QSELECT += tableName;

        if(appConfig.isGenerateData()){
            createSchema(qConnection.getGeneratorQ());
        }
        if(appConfig.isDebug()){
            createSchema(qConnection.getQ());
            this.QUPDATE = "insert";

        }

    }


    private void createSchema(c connection) {
        String table = tableName + ":([cellID:`symbol$()] " +
                "lat: `float$();" +
                "lng: `float$();" +
                "signalQuality: `float$();" +
                "region: `symbol$())";

        try {
            connection.k(table);

        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveCell(Cell cell) {
        try {
            qConnection.getQ().ks(QUPDATE, tableName, cell.mapToQArray());
            //qConnection.getGeneratorQ().ks(QINSERT, tableName, cell.mapToQArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchSaveCells(List<Cell> cells) {
        int size = cells.size();
        String[] cellIDs = new String[size];
        double[] lats = new double[size];
        double[] lngs = new double[size];
        double[] squals = new double[size];
        String[] regions = new String[size];

        for (int i = 0; i < cells.size(); i++) {
            cellIDs[i] = String.valueOf(cells.get(i).getId());
            lats[i] = cells.get(i).getLocation().getLatitude();
            lngs[i] = cells.get(i).getLocation().getLongitude();
            squals[i] = cells.get(i).getSignalQuality();
            regions[i] = cells.get(i).getRegion();
        }

        Object[] cellsArray = new Object[]{
                cellIDs, lats, lngs, squals, regions
        };

        try {
            qConnection.getQ().ks(QUPDATE, tableName, cellsArray);
            qConnection.getGeneratorQ().ks(QINSERT, tableName, cellsArray);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Cell> batchReadCells() {
        try {
            c.Flip res = c.td(qConnection.getGeneratorQ().k(QSELECT));
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
            qConnection.getQ().ks("upsert", tableName, cell.mapToQArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
