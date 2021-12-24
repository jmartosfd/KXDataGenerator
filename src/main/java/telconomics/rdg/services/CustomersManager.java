package telconomics.rdg.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import telconomics.rdg.daos.CustomersDAOInterface;
import telconomics.rdg.daos.RegionsDAOInterface;
import telconomics.rdg.model.ConnectionRecord;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.model.Customer;
import telconomics.rdg.model.Region;
import telconomics.rdg.utils.AppConfig;
import telconomics.rdg.utils.IMEIGenerator;
import telconomics.rdg.utils.IMSIGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CustomersManager {


    private CustomersDAOInterface customersDAOInterface;
    private RegionsDAOInterface regionsDAOInterface;
    private AppConfig appConfig;

    @Getter
    private List<Customer> customers;

    public CustomersManager(
            @Qualifier("customersDAOq")
            CustomersDAOInterface customersDAOInterface,
            RegionsDAOInterface regionsDAOInterface,
            AppConfig appConfig) {
        this.customersDAOInterface = customersDAOInterface;
        this.regionsDAOInterface = regionsDAOInterface;
        this.appConfig = appConfig;
    }

    public void generateNewCustomers() {
        List<Region> regions = new ArrayList<>(regionsDAOInterface.readRegions().values());
        Random random = new Random();
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < appConfig.getNumberOfCustomers(); i++) {
            Region selectedRegion = regions.get(random.nextInt(regions.size()));
            customers.add(generateRandomCustomer(selectedRegion,i));

        }
        System.out.println("Saving customers");
        customersDAOInterface.batchSaveCustomers(customers);

    }

    public void loadCustomersForRealTime(){
        customers = customersDAOInterface.batchReadCustomers();
    }


    private Customer generateRandomCustomer(Region r, int id){
        double signalQuality = new Random().nextGaussian();
        if (signalQuality < ConnectionRecord.normalOldMin){
            signalQuality = ConnectionRecord.normalOldMin;
        }else if(signalQuality > ConnectionRecord.normalRange/2){
            signalQuality = ConnectionRecord.normalRange/2;
        }
        signalQuality+=ConnectionRecord.normalRange/2;
        String imei = IMEIGenerator.generateIMEI();
        double imsi = IMSIGenerator.generateIMSI(id);
        Coordinate coordinate = r.getRegion().createInnerRandomCoordinate();
        Customer c = new Customer(String.valueOf(imsi),imei,signalQuality, r.getName(), coordinate);
        return c;
    }




}
