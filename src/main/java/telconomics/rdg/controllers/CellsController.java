package telconomics.rdg.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telconomics.rdg.services.CellsManager;
import telconomics.rdg.services.Orchestrator;

import java.util.Map;

@RestController
public class CellsController {

    private CellsManager cellsManager;
    private Orchestrator orchestrator;

    public CellsController(CellsManager cellsManager, Orchestrator orchestrator) {
        this.cellsManager = cellsManager;
        this.orchestrator = orchestrator;
    }


    @PatchMapping("/broken-cells")
    public ResponseEntity fixBrokenCells() {
        System.out.println("Called broken-cells");
        cellsManager.fixBrokenCells();
        return new ResponseEntity(HttpStatus.OK);

    }

    @PatchMapping("/broken-cells/{cellID}")
    public ResponseEntity fixSpecificBrokenCell(@PathVariable String cellID) {
        System.out.println("Called fixSpecificBrokenCell on " + cellID);
        cellsManager.fixBrokenCell(cellID);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/cells/{cellID}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity breakCell(@PathVariable String cellID, @RequestBody Map<String, Float> request) {
        float integrity = request.get("integrity");
        System.out.println("Called breakCell on " + cellID + " with new Integrity as: "+integrity);
        cellsManager.breakCell(cellID, integrity);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/automatic-repair", consumes = MediaType.ALL_VALUE)
    public ResponseEntity automaticFix(@RequestBody Map<String, Integer> request) {
        System.out.println("Activated automatic repair of cells");
        int fixInterval = request.get("fixInterval");
        //Flip automatic cell repair
        if(fixInterval < 0){
            orchestrator.setActivateAutomaticCellRepair(Boolean.FALSE);
        }else{
            orchestrator.setActivateAutomaticCellRepair(Boolean.TRUE);
            orchestrator.setFixCellInterval(fixInterval);

        }

        System.out.println("Automatic repair set as " + orchestrator.isActivateAutomaticCellRepair());
        return new ResponseEntity(HttpStatus.OK);
    }



}
