package telconomics.rdg.model;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
public class CellState implements QSerializable{

    UUID cellID;
    int phase;
    float integrity;
    Date timeStamp;

    protected CellState(UUID cellID, int phase, float integrity, Date timeStamp){
        this.cellID = cellID;
        this.phase = phase;
        this.integrity = integrity;
        this.timeStamp = timeStamp;
    }

    @Override
    public Object[] mapToQArray() {
        return new Object[]{
                cellID.toString(), phase, integrity, timeStamp
        };
    }
}
