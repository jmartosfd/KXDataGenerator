package telconomics.rdg.daos.qkdb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CustomersDAOInterface;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.model.Customer;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
@ConditionalOnProperty(
        value="q.connect",
        havingValue = "True",
        matchIfMissing = false
)
public class CustomersDAOq implements CustomersDAOInterface {

    private QConnection qConnection;
    private String QUPDATE;
    private String QINSERT = "insert";
    private static String QSELECT = "select from ";
    private String tableName;


    public CustomersDAOq(QConnection qConnection, AppConfig appConfig){
        this.qConnection = qConnection;
        this.tableName = appConfig.getCustomersTableName();
        this.QUPDATE = appConfig.getUpdate();
        QSELECT+=tableName;

        if(appConfig.isGenerateData())
            createSchema(qConnection.getGeneratorQ());

        if(appConfig.isDebug()){
            createSchema(qConnection.getQ());
            this.QUPDATE = "insert";
        }


    }

    private void createSchema(c connection){
        String table = tableName + ":([] imsi:`symbol$();" +
                "imei: `symbol$();"+
                "signalQuality: `float$();" +
                "region: `symbol$();"+
                "lat: `float$();" +
                "lng: `float$())";

        try {
            connection.k(table);
        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveCustomer(Customer customer) {
        try {
            qConnection.getQ().ks(QUPDATE, tableName, customer.mapToQArray());
            qConnection.getGeneratorQ().ks(QINSERT, tableName, customer.mapToQArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchSaveCustomers(List<Customer> customers) {
        int size = customers.size();
        String[] imsis = new String[size];
        String[] imeis = new String[size];
        double[] signalQuals = new double[size];
        String[] regions = new String[size];
        double[] lats = new double[size];
        double[] lngs = new double[size];

        for(int i = 0; i < size; i++){
            imsis[i] = customers.get(i).getImsi();
            imeis[i] = customers.get(i).getImei();
            signalQuals[i] = customers.get(i).getDeviceSignalQuality();
            regions[i] = customers.get(i).getAssignedRegion();
            lats[i] = customers.get(i).getCurrentLocation().getLatitude();
            lngs[i] = customers.get(i).getCurrentLocation().getLongitude();
        }

        Object[] customersArray = new Object[]{imsis,imeis,signalQuals,regions,lats,lngs};

        try {
            qConnection.getQ().ks(QUPDATE, tableName, customersArray);
            qConnection.getGeneratorQ().ks(QINSERT, tableName, customersArray);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Customer> batchReadCustomers() {

        try {
            c.Flip res = (c.Flip) qConnection.getGeneratorQ().k(QSELECT);
            Object[] columnData = res.y;
            String[] imsis = (String[]) columnData[0];
            String[] imeis = (String[]) columnData[1];
            double[] squals = (double[]) columnData[2];
            String[] regions = (String[]) columnData[3];
            double[] lats = (double[]) columnData[4];
            double[] lngs = (double[]) columnData[5];

            List<Customer> customers = new ArrayList<>();
            for (int i = 0; i < imsis.length; i++) {
                Customer customer = new Customer(imsis[i], imeis[i], squals[i], regions[i],
                        Coordinate.builder().latitude(lats[i]).longitude(lngs[i]).build());
                customers.add(customer);
            }
            return customers;
        } catch (IOException | c.KException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect");
        }
    }
}
