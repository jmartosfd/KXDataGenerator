package telconomics.rdg.services;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import telconomics.rdg.daos.ConnectionRecordsDAOInterface;
import telconomics.rdg.model.Cell;
import telconomics.rdg.model.ConnectionRecord;
import telconomics.rdg.model.Customer;
import telconomics.rdg.model.Region;
import telconomics.rdg.quadtree.Neighbour;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RealTimeManager {

    private ConnectionRecordsDAOInterface connectionRecordsDAO;


    public RealTimeManager(ConnectionRecordsDAOInterface connectionRecordsDAOInterface){
        this.connectionRecordsDAO = connectionRecordsDAOInterface;
    }

    public void generateRealtimeData(List<Customer> customers, Map<String, Region> loadedRegions) {

        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            Pair<Cell, Double> pair = findClosestCellToCustomer(c, loadedRegions.get(c.getAssignedRegion()));
            ConnectionRecord connectionRecord = new ConnectionRecord(c, pair.getLeft(), pair.getRight());
            connectionRecordsDAO.addConnectionRecord(connectionRecord);
            customers.set(i, customerNextStep(customers.get(i)));
        }
    }


    private Customer customerNextStep(Customer customer) {
        double lat = customer.getCurrentLocation().getLatitude();
        double lng = customer.getCurrentLocation().getLongitude();
        int changeMovement = ThreadLocalRandom.current().nextInt(10 - 5) + 5;


        if (changeMovement < customer.getSteps() || customer.getSteps() == 0) {
            customer.setSteps(0);
            double latDelta = ThreadLocalRandom.current().nextInt((10 - (-10))) + (-10);
            customer.setLaDelta(latDelta / 10000);
            double lngDelta = ThreadLocalRandom.current().nextInt((10 - (-10))) + (-10);
            customer.setLngDelta(lngDelta / 10000);
        }
        customer.increaseSteps();
        customer.getCurrentLocation().setLatitude(lat + customer.getLaDelta());
        customer.getCurrentLocation().setLongitude(lng + customer.getLngDelta());
        return customer;
    }


    private Pair<Cell, Double> findClosestCellToCustomer(Customer customer, Region region) {
        Set<Neighbour> neighbours = new HashSet<>();
        double initialRank = 1;

        while (neighbours.isEmpty()) {

            neighbours = region.getQuadTree().findNeighbours(
                    customer.getCurrentLocation().getLatitude(),
                    customer.getCurrentLocation().getLongitude(),
                    initialRank);
            initialRank = initialRank * 2;
        }

        //System.out.println(initialRank/2);
        double minDistance = Double.MAX_VALUE;
        Cell closestNeighbour = null;
        Object[] cells = neighbours.toArray();
        for (int i = 0; i < cells.length; i++) {
            Cell tmpCell = (Cell) cells[i];
            double tmpDistance = tmpCell.getLocation().harvesineDistance(customer.getCurrentLocation());
            if (tmpDistance < minDistance) {
                closestNeighbour = tmpCell;
                minDistance = tmpDistance;
            }
        }


        return Pair.of(closestNeighbour, minDistance);

    }

}
