package telconomics.rdg.daos.qkdb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.ConnectionRecordsDAOInterface;
import telconomics.rdg.model.ConnectionRecord;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.util.List;

@Repository
@ConditionalOnProperty(
        value="q.connect",
        havingValue = "True",
        matchIfMissing = false
)
public class ConnectionRecordsDAOq implements ConnectionRecordsDAOInterface {

    private QConnection qConnection;
    private String tableName;
    private String QUPDATE;

    public ConnectionRecordsDAOq(QConnection qConnection, AppConfig appConfig) {
        this.qConnection = qConnection;
        this.QUPDATE = appConfig.getUpdate();
        this.tableName = appConfig.getRecordsTableName();

        if(appConfig.isDebug()){
            createSchema();
            QUPDATE = "insert";
        }
    }

    private void createSchema(){
        String table =tableName+":([] ts: `timestamp$();" +
                "cellID:`symbol$(); " +
                "phase:`int$(); " +
                "imsi:`symbol$();" +
                "imei: `symbol$();" +
                "dspeed: `float$();"+
                "uspeed: `float$();" +
                "lat: `float$();"+
                "lng: `float$();"+
                "cellDistance: `float$()"+
                ")";

        try {
            qConnection.getQ().k(table);
        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void addConnectionRecord(ConnectionRecord connectionRecord) {
        try {
            qConnection.getQ().ks(QUPDATE, tableName, connectionRecord.mapToQArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchInsertConnectionRecord(List records) {
        try {
            qConnection.getQ().ks(QUPDATE, tableName, records.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
