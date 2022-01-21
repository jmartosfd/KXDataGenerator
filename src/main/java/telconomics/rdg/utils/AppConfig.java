package telconomics.rdg.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    //External resources

    @Value("${resources.file.cells}")
    private String cellsFileLocation;

    @Value("${resources.file.customers}")
    private String customersFileLocation;

    @Value("${resources.file.records}")
    private String connectionRecordsFileLocation;

    @Value("${resources.file.cellStates}")
    private String cellStatesFileLocation;

    @Value("${resources.file.regions}")
    private String regionsFileLocation;


    @Value("${q.connect}")
    private boolean qConnect;

    @Value("${q.table.records}")
    private String recordsTableName;

    @Value("${q.table.cells}")
    private String cellsTableName;

    @Value("${q.table.customers}")
    private String customersTableName;

    @Value("${q.table.cellStates}")
    private String cellStatesTableName;

    @Value("${q.host}")
    private String host;

    @Value("${q.port.tp}")
    private int tpPort;

    @Value("${q.port.rdb}")
    private int rdbPort;

    @Value("${q.port.aux}")
    private int auxPort;

    @Value("${q.username}")
    private String username;

    @Value("${q.password}")
    private String password;

    @Value("${q.update}")
    private String update;



    @Value("${dao.qualifier.cells}")
    private String cellsDAOQualifier;

    @Value("${dao.qualifier.customers}")
    private String customersDAOQualifier;

    @Value("${dao.qualifier.cellStates}")
    private String cellStatesDAOQualifier;

    @Value("${dao.qualifier.records}")
    private String connectionRecordsDAOQualifier;



    @Value("${generator.activate}")
    private boolean generateData;

    @Value("${generator.size.brokencells}")
    private int numberOfBrokenCells;

    @Value("${generator.size.customers}")
    private int numberOfCustomers;

    @Value("${realtime.breakinterval}")
    private int breakInterval;

    @Value("${realtime.batchpartition}")
    private int batchpartition;

    @Value("${realtime.sleeptime}")
    private int sleeptime;

    @Value("${realtime.ticks}")
    private int numberOfTicks;

    @Value("${system.debug}")
    private boolean debug;






}

