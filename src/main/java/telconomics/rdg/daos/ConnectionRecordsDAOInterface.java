package telconomics.rdg.daos;

import org.springframework.stereotype.Repository;
import telconomics.rdg.model.ConnectionRecord;

public interface ConnectionRecordsDAOInterface {

    void addConnectionRecord(ConnectionRecord connectionRecord);


}
