package telconomics.rdg.model;

import lombok.Data;
import telconomics.rdg.quadtree.Neighbour;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
public class Cell implements CSVSerializable, QSerializable, Neighbour {

    private UUID id;
    private Coordinate location;
    private double signalQuality;
    private String region;
    private Coordinate normalizedLocation; //Normalized location needed for quadtree
    private List<CellState> cellStates = new LinkedList<>();

    public Cell(UUID randomUUID, Coordinate coordinate, double signalQuality, String name) {
        this.id = randomUUID;
        this.location = coordinate;
        this.signalQuality = signalQuality;
        this.region = name;
        CellState cs = new CellState(id, 1, 1F, LocalDateTime.now());
        cellStates.add(cs);

    }

    public CellState addNewCellState(float integrity){
        int nextPhase = getCurrentCellState().phase+1;
        CellState cs = new CellState(id, nextPhase, integrity, LocalDateTime.now());
        cellStates.add(cs);
        return cs;
    }

    public CellState addNewCellState(){
        int nextPhase = getCurrentCellState().phase+1;
        CellState cs = new CellState(id, nextPhase, LocalDateTime.now());
        cellStates.add(cs);
        return cs;
    }


    public String[] mapToCSVRecord(){
        return new String[]{
                id.toString(),
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()),
                String.valueOf(signalQuality),
                region
        };
    }

    public CellState getCurrentCellState(){
        return cellStates.get(cellStates.size()-1);
    }


    @Override
    public Object[] mapToQArray() {
        return new Object[]{
                id.toString(), location.getLatitude(), location.getLongitude(), signalQuality, region
        };
    }

    @Override
    public UUID getId(){
        return this.id;
    }

    @Override
    public double getLatitude() {
        return getNormalizedLocation().getLatitude();
    }

    @Override
    public double getLongitude() {
        return getNormalizedLocation().getLongitude();
    }
}
