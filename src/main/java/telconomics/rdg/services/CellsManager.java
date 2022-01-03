package telconomics.rdg.services;


import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import telconomics.rdg.daos.CellStatesDAOInterface;
import telconomics.rdg.daos.CellsDAOInterface;
import telconomics.rdg.daos.RegionsDAOInterface;
import telconomics.rdg.model.*;
import telconomics.rdg.utils.AppConfig;

import java.util.*;

@Service
public class CellsManager {

    private CellsDAOInterface cellsDAOInterface;
    private CellStatesDAOInterface cellStatesDAOInterface;
    private AppConfig appConfig;

    @Getter
    private Map<String, Cell> brokenCells; //Maybe change to Map<String,Cell> if fixBrokenCell is more commonly used than his plural counterpart

    @Getter
    private List<Cell> cells = new ArrayList<> ();

    @Getter
    private Map<String, Region> regions;

    public CellsManager(ApplicationContext applicationContext, AppConfig appConfig, RegionsDAOInterface regionsDAOInterface){

        this.appConfig = appConfig;
        //Load regions on construction
        this.regions = regionsDAOInterface.readRegions();
        this.brokenCells = new HashMap<>();

        String cellsDAOQualifier = appConfig.getCellsDAOQualifier();
        String cellStatesDAOQualifier = appConfig.getCellStatesDAOQualifier();

        this.cellsDAOInterface = (CellsDAOInterface) applicationContext.getBean(cellsDAOQualifier);
        this.cellStatesDAOInterface = (CellStatesDAOInterface) applicationContext.getBean(cellStatesDAOQualifier);

    }


    public void generateNewCells(){
        List<Cell> cells = new ArrayList<>();
        for(Region r: regions.values()){
            for(int i = 0; i<r.getCellsLimit(); i++){
                cells.add(generateRandomCell(r));
            }
        }

        System.out.println("Saving cells");
        cellsDAOInterface.batchSaveCells(cells);

    }

    /**
     * Indexes cells in the corresponding region quadtree,
     * adding those that are broken to the brokenCells List
     */
    public void loadCells(){
        List<Cell> cells = cellsDAOInterface.batchReadCells();

        List<CellState> cellStates = new ArrayList<>();
        for(Cell c: cells){
            regions.get(c.getRegion()).assignCell(c);
            this.cells.add(c);
            cellStates.add(c.getCurrentCellState());
        }
        this.cellStatesDAOInterface.batchSaveCellStates(cellStates);


        Random r = new Random();
        Set<Integer> brokenCellsIndexes = new HashSet<>();
        while(brokenCellsIndexes.size()< appConfig.getNumberOfBrokenCells()){
            brokenCellsIndexes.add(r.nextInt(cells.size()));
        }

        brokenCellsIndexes.forEach(integer -> {
            Cell c = cells.get(integer);
            brokenCells.put(c.getId().toString(), c);
            CellState cs = c.addNewCellState();
            cellStatesDAOInterface.saveCellState(cs);
        });

    }


    public void fixBrokenCells(){
        brokenCells.keySet().forEach(this::fixBrokenCell);
    }

    public void fixBrokenCell(String cellID){
        Cell cell = brokenCells.get(cellID);
        if(cell.getCurrentCellState().getIntegrity() != 1F){
            CellState cs = cell.addNewCellState(1F);
            cellStatesDAOInterface.saveCellState(cs);
            brokenCells.remove(cellID);
        }

    }


    public void breakCell(int idx){
        Cell cell= cells.get(idx);
        CellState cs = cell.addNewCellState();
        brokenCells.put(cell.getId().toString(), cell);
        cellStatesDAOInterface.saveCellState(cs);
    }

    public void breakCell(String cellID, float integrity){
        for(Cell c : cells){
            if(c.getId().toString().equals(cellID)){
                CellState cs = c.addNewCellState(integrity);
                brokenCells.put(c.getId().toString(), c);
                cellStatesDAOInterface.saveCellState(cs);

            }
        }
    }



    private Cell generateRandomCell(Region r) {
        double signalQuality = new Random().nextGaussian();
        Math.max(signalQuality, ConnectionRecord.normalOldMin);
        Math.min(signalQuality, ConnectionRecord.normalRange/2);
        if (signalQuality < ConnectionRecord.normalOldMin){
            signalQuality = ConnectionRecord.normalOldMin;
        }else if(signalQuality > ConnectionRecord.normalRange/2){
            signalQuality = ConnectionRecord.normalRange/2;
        }
        signalQuality+=ConnectionRecord.normalRange/2;
        Coordinate coordinate = r.getRegion().createInnerRandomCoordinate();
        return new Cell(UUID.randomUUID(), coordinate, signalQuality, r.getName());
    }

}
