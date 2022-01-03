package telconomics.rdg.daos;

import telconomics.rdg.model.CellState;

import java.util.List;

public interface CellStatesDAOInterface {

    void batchSaveCellStates(List<CellState> cellStates);

    void saveCellState(CellState cellState);

}
