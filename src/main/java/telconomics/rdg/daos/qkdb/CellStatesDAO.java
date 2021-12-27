package telconomics.rdg.daos.qkdb;

import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CellStatesDAOInterface;
import telconomics.rdg.model.CellState;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;

@Repository
public class CellStatesDAO implements CellStatesDAOInterface {

    private QConnection qConnection;

    private String QINSERT;
    private String tableName;

    public CellStatesDAO(QConnection qConnection, AppConfig appConfig) {
        this.qConnection = qConnection;
        this.QINSERT = appConfig.getUpdate();
        this.tableName = appConfig.getCellStates();
        /**
        if(appConfig.isGenerateData()){
            createSchema();
        }
         */

    }


    private void createSchema(){
        String table = tableName + ":([cellID:`symbol$(); phase:`int$()] " +
                "integrity: `real$();" +
                "momentOfChange: `datetime$())";

        try {
            qConnection.getQ().k(table);
        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }
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
