package telconomics.rdg.daos;

import org.springframework.stereotype.Repository;
import telconomics.rdg.model.Cell;

import java.util.List;

public interface CellsDAOInterface {

    void saveCell(Cell cell);

    void batchSaveCells(List<Cell> cells);

    List<Cell> batchReadCells();

    void updateCell(Cell cell);



}
