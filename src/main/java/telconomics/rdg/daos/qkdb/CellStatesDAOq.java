package telconomics.rdg.daos.qkdb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CellStatesDAOInterface;
import telconomics.rdg.model.CellState;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.util.List;

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

    public CellStatesDAOq(QConnection qConnection, AppConfig appConfig) {
        this.qConnection = qConnection;
        this.QINSERT = appConfig.getUpdate();
        this.tableName = appConfig.getCellStatesTableName();

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
}
