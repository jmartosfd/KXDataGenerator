package telconomics.rdg.daos.qkdb;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;

@Service
@ConditionalOnProperty(
        value = "q.connect",
        havingValue = "True",
        matchIfMissing = false
)
public class QConnection {

    @Getter
    private c Q;

    @Getter
    private c generatorQ;

    @Getter
    private c rdbQ;

    public QConnection(AppConfig appConfig) {
        try {
            this.Q = new c(appConfig.getHost(), appConfig.getTpPort(),
                    appConfig.getUsername() + ":" + appConfig.getPassword(), false);

            this.generatorQ = new c(appConfig.getHost(), appConfig.getAuxPort(),
                    appConfig.getUsername() + ":" + appConfig.getPassword(), false);

            this.rdbQ = new c(appConfig.getHost(), appConfig.getRdbPort(),
                    appConfig.getUsername() + ":" + appConfig.getPassword(), false);

        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }

    }

}
