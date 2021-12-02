package telconomics.rdg.daos.qkdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CustomersDAOInterface;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.model.Customer;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomersDAOq implements CustomersDAOInterface {

    private QConnection qConnection;
    private static final String QUPDATE = "insert";
    private static String QSELECT = "select from ";
    private String tableName;


    public CustomersDAOq(QConnection qConnection, AppConfig appConfig){
        this.qConnection = qConnection;
        this.tableName = appConfig.getCustomersTableName();
        QSELECT+=tableName;
        if(appConfig.isGenerateData())
            createSchema();
    }



    private void createSchema(){
        String table = tableName + ":([] imsi:`float$();" +
                "imei: `symbol$();"+
                "signalQuality: `float$();" +
                "region: `symbol$();"+
                "lat: `float$();" +
                "lng: `float$())";

        try {
            qConnection.getQ().k(table);
        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveCustomer(Customer customer) {
        try {
            qConnection.getQ().ks(QUPDATE, tableName, customer.mapToQArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchSaveCustomers(List<Customer> customers) {
        customers.stream().forEach(this::saveCustomer);
    }

    @Override
    public List<Customer> batchReadCustomers() {
        try {
            c.Flip res = (c.Flip) qConnection.getQ().k(QSELECT);
            Object[] columnData = res.y;
            double[] imsis = (double[]) columnData[0];
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
