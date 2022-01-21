package telconomics.rdg.daos.qkdb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.ConnectionRecordsDAOInterface;
import telconomics.rdg.model.ConnectionRecord;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String QSELECT;

    public ConnectionRecordsDAOq(QConnection qConnection, AppConfig appConfig) {
        this.qConnection = qConnection;
        this.QUPDATE = appConfig.getUpdate();
        this.tableName = appConfig.getRecordsTableName();
        this.QSELECT = "0!select last(lat), last(lng) by imei from " + tableName;

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
    public void batchInsertConnectionRecord(List<?> records) {
        try {
            qConnection.getQ().ks(QUPDATE, tableName, records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Coordinate> recoverLastPositions() {

        try {
            c.Flip res = (c.Flip) qConnection.getRdbQ().k(QSELECT);
            Object[] columnData = res.y;
            String[] imeis = (String[]) columnData[0];
            double[] lats = (double[]) columnData[1];
            double[] lngs = (double[]) columnData[2];

            Map<String, Coordinate> lastCoordinatesKnown = new HashMap<>();
            for (int i = 0; i < imeis.length; i++) {
                lastCoordinatesKnown.put(imeis[i], Coordinate.builder().latitude(lats[i]).longitude(lngs[i]).build());
            }

            return lastCoordinatesKnown;
        }catch (IOException | c.KException e){
            e.printStackTrace();
            throw new RuntimeException("Failed to connect");
        }
    }
}
