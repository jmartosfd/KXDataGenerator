package telconomics.rdg.services;

import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import telconomics.rdg.daos.ConnectionRecordsDAOInterface;
import telconomics.rdg.daos.CustomersDAOInterface;
import telconomics.rdg.daos.RegionsDAOInterface;
import telconomics.rdg.model.ConnectionRecord;
import telconomics.rdg.model.Coordinate;
import telconomics.rdg.model.Customer;
import telconomics.rdg.model.Region;
import telconomics.rdg.utils.AppConfig;
import telconomics.rdg.utils.IMEIGenerator;
import telconomics.rdg.utils.IMSIGenerator;

import java.util.*;

@Service
public class CustomersManager {


    private CustomersDAOInterface customersDAOInterface;
    private RegionsDAOInterface regionsDAOInterface;
    private ConnectionRecordsDAOInterface connectionRecordsDAOInterface;
    private AppConfig appConfig;

    @Getter
    private List<Customer> customers;

    public CustomersManager(ApplicationContext applicationContext, AppConfig appConfig, RegionsDAOInterface regionsDAOInterface) {
        this.appConfig = appConfig;
        this.regionsDAOInterface = regionsDAOInterface;

        String customersDAOQualifier = appConfig.getCustomersDAOQualifier();
        this.customersDAOInterface = (CustomersDAOInterface) applicationContext.getBean(customersDAOQualifier);
        String connectionRecordsDAOQualifier = appConfig.getConnectionRecordsDAOQualifier();
        this.connectionRecordsDAOInterface = (ConnectionRecordsDAOInterface) applicationContext.getBean(connectionRecordsDAOQualifier);

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

    public void loadCustomers(){
        customers = customersDAOInterface.batchReadCustomers();
        Map<String, Coordinate> lastPositions = connectionRecordsDAOInterface.recoverLastPositions();
        if(!lastPositions.isEmpty()){
            for (Customer customer : customers){
                customer.setCurrentLocation(lastPositions.get(customer.getImei()));
            }
        }
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
