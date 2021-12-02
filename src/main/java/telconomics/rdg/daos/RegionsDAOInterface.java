package telconomics.rdg.daos;

import org.springframework.stereotype.Repository;
import telconomics.rdg.model.Region;

import java.util.Map;

public interface RegionsDAOInterface {

    Map<String, Region> readRegions();


}
