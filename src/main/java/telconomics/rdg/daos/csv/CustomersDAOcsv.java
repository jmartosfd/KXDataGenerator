package telconomics.rdg.daos.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import telconomics.rdg.daos.CustomersDAOInterface;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.model.Customer;
import telconomics.rdg.utils.AppConfig;
import telconomics.rdg.utils.FileUtils;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomersDAOcsv implements CustomersDAOInterface {

    @Autowired
    AppConfig appConfig;

    private static final int csvRecordImsi = 0;
    private static final int csvRecordImei = 1;
    private static final int csvRecordSignalQuality = 2;
    private static final int csvRecordRegion = 3;
    private static final int csvRecordLat = 4;
    private static final int csvRecordLong = 5;


    @Override
    public void saveCustomer(Customer customer) {

    }

    @Override
    public void batchSaveCustomers(List<Customer> customers) {

    }

    @Override
    public List<Customer> batchReadCustomers() {

        List<Customer> customersRet = new ArrayList<>();
        try {
            Path path = Paths.get(appConfig.getCustomersFileLocation());
            Reader reader = Files.newBufferedReader(path);
            Iterable<CSVRecord> customers = CSVFormat.DEFAULT.parse(reader);
            customers.forEach(csvRecord -> {
                customersRet.add(mapCustomerFromCSVRecord(csvRecord));
            });
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customersRet;
    }



    private Customer mapCustomerFromCSVRecord(CSVRecord csvRecord){
        double imsi = Double.parseDouble(csvRecord.get(csvRecordImsi));
        String imei = csvRecord.get(csvRecordImei);
        double signalQuality = Double.parseDouble(csvRecord.get(csvRecordSignalQuality));
        String region = csvRecord.get(csvRecordRegion);
        Coordinate location = Coordinate.builder().latitude(Double.parseDouble(csvRecord.get(csvRecordLat))).longitude(Double.parseDouble(csvRecord.get(csvRecordLong))).build();


        return new Customer(imsi,imei, signalQuality,region,location);

    }

}
