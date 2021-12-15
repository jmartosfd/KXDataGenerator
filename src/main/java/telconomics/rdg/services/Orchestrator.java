package telconomics.rdg.services;

import lombok.Setter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import telconomics.rdg.model.Customer;
import telconomics.rdg.utils.AppConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class Orchestrator {

    private CellsManager cellsManager;
    private CustomersManager customersManager;
    private RealTimeManager realTimeManager;
    private AppConfig appConfig;

    @Setter
    private boolean activateAutomaticCellRepair=false;

    @Setter
    private int fixCellInterval=1;

    public Orchestrator(CellsManager cellsManager, CustomersManager customersManager,
                        RealTimeManager realTimeManager, AppConfig appConfig){
        this.cellsManager = cellsManager;
        this.customersManager = customersManager;
        this.realTimeManager = realTimeManager;
        this.appConfig = appConfig;
    }

    public void createNewData(){
        cellsManager.generateNewCells();
        customersManager.generateNewCustomers();
    }

    public void launchRealTime(){
        cellsManager.loadCellsForRealTime();
        customersManager.loadCustomersForRealTime();
        int partitionSize = Math.min(appConfig.getBatchpartition(), customersManager.getCustomers().size());
        int breakCellInterval = appConfig.getBreakInterval();
        int sleepTime = appConfig.getSleeptime();

        List<List<Customer>> batchPartitions = ListUtils.partition(customersManager.getCustomers(), partitionSize);



        StopWatch stopWatch = new StopWatch();
        for(int i = 0;;i++){
            stopWatch.start();
            if(i%breakCellInterval == 0){
                Random rn = new Random();
                int idx = rn.nextInt(cellsManager.getCells().size());
                cellsManager.breakCell(idx);
            }

            if(activateAutomaticCellRepair && i%fixCellInterval == 0 && !cellsManager.getBrokenCells().isEmpty()){
                Random rn = new Random();
                int idx = rn.nextInt(cellsManager.getBrokenCells().size());
                String cellIDtoFix = new ArrayList<>(cellsManager.getBrokenCells().values()).get(idx).getId().toString();
                System.out.println("Fixing cell with ID: "+cellIDtoFix);
                cellsManager.fixBrokenCell(cellIDtoFix);

            }


            realTimeManager.generateRealtimeDataWithBatchInsertion(batchPartitions, partitionSize, cellsManager.getRegions());
            stopWatch.stop();
            double partial = stopWatch.getTime(TimeUnit.MILLISECONDS);
            System.out.println("Finished loop, time: " + partial);
            stopWatch.reset();

            if(partial < sleepTime){
                try {
                    long missingSleepTime = (long) (sleepTime-partial);
                    System.out.println("Sleeping "+missingSleepTime);
                    Thread.sleep(missingSleepTime);
                } catch (InterruptedException e) {
                    System.out.println("Error in sleep");
                    e.printStackTrace();
                }
            }

        }

    }



}
