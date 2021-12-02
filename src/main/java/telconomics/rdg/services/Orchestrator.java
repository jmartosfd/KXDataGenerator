package telconomics.rdg.services;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

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


        StopWatch stopWatch = new StopWatch();
        for(int i = 0;;i++){
            stopWatch.start();
            if(i%5 == 0){
                Random rn = new Random();
                int idx = rn.nextInt(cellsManager.getCells().size());
                double scale = Math.pow(10, 1);
                float integrity = (float) (Math.round(rn.nextFloat() * scale) / scale);
                cellsManager.breakCell(idx, integrity);
            }
            realTimeManager.generateRealtimeData(customersManager.getCustomers(), cellsManager.getRegions());
            stopWatch.stop();
            System.out.println("Finished loop, time: " + stopWatch.getTime(TimeUnit.MILLISECONDS));
            stopWatch.reset();

        }

    }


}
