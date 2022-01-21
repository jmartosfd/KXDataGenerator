package telconomics.rdg.daos;

import telconomics.rdg.model.ConnectionRecord;
import telconomics.rdg.model.Coordinate;

import java.util.List;
import java.util.Map;

public interface ConnectionRecordsDAOInterface {

    void addConnectionRecord(ConnectionRecord connectionRecord);

    void batchInsertConnectionRecord(List<?> records);

    Map<String, Coordinate> recoverLastPositions();



}
