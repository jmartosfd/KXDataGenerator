package telconomics.rdg.services;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import telconomics.rdg.model.Customer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class Orchestrator {

    private CellsManager cellsManager;
    private CustomersManager customersManager;
    private RealTimeManager realTimeManager;

    public Orchestrator(CellsManager cellsManager, CustomersManager customersManager, RealTimeManager realTimeManager){
        this.cellsManager = cellsManager;
        this.customersManager = customersManager;
        this.realTimeManager = realTimeManager;
    }

    public void createNewData(){
        cellsManager.generateNewCells();
        customersManager.generateNewCustomers();
    }

    public void launchRealTime(){
        cellsManager.loadCellsForRealTime();
        customersManager.loadCustomersForRealTime();
        int partitionSize = 250000;
        List<List<Customer>> batchPartitions = ListUtils.partition(customersManager.getCustomers(), partitionSize);



        double totalTime = 0;
        StopWatch stopWatch = new StopWatch();
        for(int i = 0;i<10;i++){
            stopWatch.start();
            if(i%1 == 0){
                Random rn = new Random();
                int idx = rn.nextInt(cellsManager.getCells().size());
                cellsManager.breakCell(idx);
            }
            realTimeManager.generateRealtimeDataWithBatchInsertion(batchPartitions, partitionSize, cellsManager.getRegions());
            stopWatch.stop();
            double partial = stopWatch.getTime(TimeUnit.MILLISECONDS);
            totalTime+=partial;
            System.out.println("Finished loop, time: " + stopWatch.getTime(TimeUnit.MILLISECONDS));
            stopWatch.reset();

        }
        System.out.println("Average time: "+totalTime/10);

    }



}
