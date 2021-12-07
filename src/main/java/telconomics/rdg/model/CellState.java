package telconomics.rdg.model;

import lombok.Getter;

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
        double scale = Math.pow(10, 1);
        this.integrity = (float) Math.max((Math.round(rn.nextFloat() * (0.9F) * scale) / scale), 0.1F);

    }

    @Override
    public Object[] mapToQArray() {
        return new Object[]{
                cellID.toString(), phase, integrity, timeStamp
        };
    }
}
