package telconomics.rdg.services;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private Map<String, Cell> brokenCells; //Maybe change to Map<String,Cell> if fixBrokenCell is more commonly used than his plural counterpart

    @Getter
    private List<Cell> cells = new ArrayList<> ();

    @Getter
    private Map<String, Region> regions;

    public CellsManager(
            @Qualifier("cellsDAOq")
            CellsDAOInterface cellsDAOInterface,
            RegionsDAOInterface regionsDAOInterface,
            CellStatesDAOInterface cellStatesDAOInterface,
            AppConfig appConfig){
        this.cellsDAOInterface = cellsDAOInterface;
        this.appConfig = appConfig;
        //Load regions on construction
        this.regions = regionsDAOInterface.readRegions();
        this.cellStatesDAOInterface = cellStatesDAOInterface;
        this.brokenCells = new HashMap<>();

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
    public void loadCellsForRealTime(){
        List<Cell> cells = cellsDAOInterface.batchReadCells();

        for(Cell c: cells){
            regions.get(c.getRegion()).assignCell(c);
            this.cells.add(c);
            cellStatesDAOInterface.saveCellState(c.getCurrentCellState());
        }

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
        CellState cs = cell.addNewCellState(1F);
        cellStatesDAOInterface.saveCellState(cs);
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
