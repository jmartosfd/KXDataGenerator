package telconomics.rdg.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${resources.cells.file}")
    private String cellsFileLocation;

    @Value("${resources.customers.file}")
    private String customersFileLocation;

    @Value("${q.table.records}")
    private String recordsTableName;

    @Value("${q.table.cells}")
    private String cellsTableName;

    @Value("${q.table.customers}")
    private String customersTableName;

    @Value("${q.host}")
    private String host;

    @Value("${q.port}")
    private int port;

    @Value("${q.username}")
    private String username;

    @Value("${q.password}")
    private String password;

    @Value("${generator.activate}")
    private boolean generateData;

    @Value("${generator.brokencells.size}")
    private int numberOfBrokenCells;


}

