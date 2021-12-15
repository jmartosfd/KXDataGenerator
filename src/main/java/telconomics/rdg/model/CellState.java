package telconomics.rdg.model;

import lombok.Getter;
import org.apache.commons.math3.util.Precision;

import java.util.Date;
import java.util.Random;
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

    public CellState(UUID cellID, int phase, Date timeStamp){
        this.cellID = cellID;
        this.phase = phase;
        this.timeStamp = timeStamp;

        Random rn = new Random();
        float tmpIntegrity = rn.nextFloat() * 0.9F;
        tmpIntegrity = Precision.round(tmpIntegrity, 1);
        this.integrity = Math.max(tmpIntegrity, 0.1F);

    }

    @Override
    public Object[] mapToQArray() {
        return new Object[]{
                cellID.toString(), phase, integrity, timeStamp
        };
    }
}
