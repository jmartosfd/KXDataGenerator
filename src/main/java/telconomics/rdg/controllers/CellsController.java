package telconomics.rdg.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telconomics.rdg.services.CellsManager;

import java.util.Map;

@RestController
public class CellsController {

    private CellsManager cellsManager;

    public CellsController(CellsManager cellsManager) {
        this.cellsManager = cellsManager;
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

    ;

    //Consider adding a method to also break cells dinamically
    //May suppose storing in memory an additional data structure
    /**
     @PatchMapping("cells/{cellID}") public ResponseEntity breakCell(@PathVariable String cellID){
     cellsManager.
     }
     */


}
