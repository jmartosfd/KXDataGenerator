package telconomics.rdg.daos.qkdb;

import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.ConnectionRecordsDAOInterface;
import telconomics.rdg.model.ConnectionRecord;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;

@Repository
public class ConnectionRecordsDAO implements ConnectionRecordsDAOInterface {

    private QConnection qConnection;
    private String tableName;
    private String QUPDATE;

    public ConnectionRecordsDAO(QConnection qConnection, AppConfig appConfig) {
        this.qConnection = qConnection;
        this.QUPDATE = appConfig.getUpdate();
        this.tableName = appConfig.getRecordsTableName();
        String table =tableName+":([] ts: `datetime$();" +
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
        /**
        try {
            qConnection.getQ().k(table);
        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }
         */

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
    public void batchInsertConnectionRecord(Object[] records) {
        try {
            qConnection.getQ().ks(QUPDATE, tableName, records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
