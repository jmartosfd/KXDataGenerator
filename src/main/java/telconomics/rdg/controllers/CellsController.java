package telconomics.rdg.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import telconomics.rdg.services.CellsManager;

@RestController("/rdg")
public class CellsController {

    private CellsManager cellsManager;

    public CellsController(CellsManager cellsManager){
        this.cellsManager = cellsManager;
    }


    @PatchMapping("/broken-cells")
    public ResponseEntity fixBrokenCells(){
        System.out.println("Called broken-cells");
        cellsManager.fixBrokenCells();
        return new ResponseEntity(HttpStatus.OK);

    }

    @PatchMapping("/broken-cells/{cellID}")
    public ResponseEntity fixSpecificBrokenCell(@PathVariable String cellID){
        System.out.println("Called fixSpecificBrokenCell on "+cellID);
        cellsManager.fixBrokenCell(cellID);
        return new ResponseEntity(HttpStatus.OK);
    }


    //Consider adding a method to also break cells dinamically
    //May suppose storing in memory an additional data structure
    /**
    @PatchMapping("cells/{cellID}")
    public ResponseEntity breakCell(@PathVariable String cellID){
        cellsManager.
    }
    */




}
