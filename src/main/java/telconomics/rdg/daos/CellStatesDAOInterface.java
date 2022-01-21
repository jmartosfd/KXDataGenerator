package telconomics.rdg.daos;

import telconomics.rdg.model.CellState;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CellStatesDAOInterface {

    void batchSaveCellStates(List<CellState> cellStates);

    void saveCellState(CellState cellState);

    Map<UUID, List<CellState>> batchReadCellStates();

}
