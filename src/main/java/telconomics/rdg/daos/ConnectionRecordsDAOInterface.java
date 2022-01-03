package telconomics.rdg.daos;

import telconomics.rdg.model.ConnectionRecord;

import java.util.List;

public interface ConnectionRecordsDAOInterface {

    void addConnectionRecord(ConnectionRecord connectionRecord);

    void batchInsertConnectionRecord(List records);


}
