package telconomics.rdg.daos.qkdb;

import lombok.Getter;
import org.springframework.stereotype.Service;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;

@Service
public class QConnection {

    @Getter
    private c Q;

    @Getter
    private c generatorQ;

    private AppConfig appConfig;


    public QConnection(AppConfig appConfig) {
        this.appConfig = appConfig;
        try {
            this.Q = new c(appConfig.getHost(), appConfig.getTpPort(),
                    appConfig.getUsername() + ":" + appConfig.getPassword(), false);

            this.generatorQ = new c(appConfig.getHost(), appConfig.getAuxPort(),
                    appConfig.getUsername() + ":" + appConfig.getPassword(), false);

        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }

    }

}
