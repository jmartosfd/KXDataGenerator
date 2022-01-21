package telconomics.rdg.daos.qkdb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CellStatesDAOInterface;
import telconomics.rdg.model.CellState;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Repository
@ConditionalOnProperty(
        value="q.connect",
        havingValue = "True",
        matchIfMissing = false
)
public class CellStatesDAOq implements CellStatesDAOInterface {

    private QConnection qConnection;

    private String QINSERT;
    private String tableName;
    private String QSELECT;



    public CellStatesDAOq(QConnection qConnection, AppConfig appConfig) {
        this.qConnection = qConnection;
        this.QINSERT = appConfig.getUpdate();
        this.tableName = appConfig.getCellStatesTableName();
        this.QSELECT = "0!select by cellID from " + tableName +" where 1 < (last;phase) fby cellID";

        if(appConfig.isDebug()){
            createSchema();
            this.QINSERT = "insert";
        }
    }


    private void createSchema(){
        String table = tableName + ":([cellID:`symbol$(); phase:`int$()] " +
                "integrity: `real$();" +
                "momentOfChange: `timestamp$())";

        try {
            qConnection.getQ().k(table);
        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void batchSaveCellStates(List<CellState> cellStates) {
        for(CellState cs : cellStates)
            saveCellState(cs);
    }

    @Override
    public void saveCellState(CellState cellState) {
        try {
            qConnection.getQ().ks(QINSERT, tableName, cellState.mapToQArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, List<CellState>> batchReadCellStates() {

        try {
            c.Flip res = (c.Flip) qConnection.getRdbQ().k(QSELECT);
            Object[] columnData = res.y;
            String[] cellIDs = (String[]) columnData[0];
            int[] phases = (int[]) columnData[1];
            float[] integrities = (float[]) columnData[2];
            Timestamp[] momentsOfChange = (Timestamp[]) columnData[3];


            Map<UUID, List<CellState>> cellStates = new HashMap<>();
            for (int i = 0; i < cellIDs.length; i++) {
                CellState cs = new CellState(UUID.fromString(cellIDs[i]), phases[i], integrities[i], momentsOfChange[i].toLocalDateTime());
                System.out.println("CellState: "+cs.getCellID() + " integrity "+ cs.getIntegrity());
                List<CellState> cellStatesListTmp = new ArrayList<>();
                cellStatesListTmp.add(cs);
                cellStates.put(cs.getCellID(),cellStatesListTmp);

            }
            return cellStates;


        } catch (IOException | c.KException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect");
        }

    }
}
