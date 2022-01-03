package telconomics.rdg.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import telconomics.rdg.quadtree.QuadTree;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Region {

    private BoundingBox region;
    private List<Cell> cells = new ArrayList<>();
    private String name;
    int cellsLimit;
    QuadTree quadTree = new QuadTree();

    public Region(BoundingBox region, String name, int cellsLimit){
        this.region = region;
        this.name = name;
        this.cellsLimit = cellsLimit;
    }


    public void buildQuadTree(){

        for(Cell c : cells){
            quadTree.addNeighbour(c);
        }
        cells.clear();
    }


    public void assignCell(Cell cell){
         quadTree.addNeighbour(cell);
    }


}
