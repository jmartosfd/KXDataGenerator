package telconomics.rdg.daos.qkdb;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import telconomics.rdg.utils.AppConfig;

import java.io.IOException;

@Service
public class QConnection {

    @Getter
    private c Q;

    private AppConfig appConfig;


    public QConnection(AppConfig appConfig) {
        this.appConfig = appConfig;
        try {
            this.Q = new c(appConfig.getHost(), appConfig.getPort(),
                    appConfig.getUsername() + ":" + appConfig.getPassword(), false);
        } catch (c.KException | IOException e) {
            e.printStackTrace();
        }

    }

}
