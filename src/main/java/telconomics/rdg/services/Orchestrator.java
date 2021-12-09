package telconomics.rdg.services;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import telconomics.rdg.model.Customer;
import telconomics.rdg.utils.AppConfig;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class Orchestrator {

    private CellsManager cellsManager;
    private CustomersManager customersManager;
    private RealTimeManager realTimeManager;
    private AppConfig appConfig;

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
            realTimeManager.generateRealtimeDataWithBatchInsertion(batchPartitions, partitionSize, cellsManager.getRegions());
            stopWatch.stop();
            double partial = stopWatch.getTime(TimeUnit.MILLISECONDS);
            System.out.println("Finished loop, time: " + partial);
            stopWatch.reset();

            if(partial < sleepTime){
                try {
                    long missingSleepTime = (long) (sleepTime-partial);
                    Thread.sleep(missingSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }



}
