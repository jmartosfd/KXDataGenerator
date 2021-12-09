package telconomics.rdg.daos;

import telconomics.rdg.model.ConnectionRecord;

public interface ConnectionRecordsDAOInterface {

    void addConnectionRecord(ConnectionRecord connectionRecord);

    void batchInsertConnectionRecord(Object[] records);


}
