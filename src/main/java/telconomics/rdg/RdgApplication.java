package telconomics.rdg;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import telconomics.rdg.services.Orchestrator;
import telconomics.rdg.utils.AppConfig;

@SpringBootApplication
public class RdgApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RdgApplication.class, args);
	}


	@Autowired
	Orchestrator orchestrator;

	@Autowired
	AppConfig appConfig;

	@Bean
	public HttpTraceRepository httpTraceRepository() {
		return new InMemoryHttpTraceRepository();
	}

	@Override
	public void run(String... args) throws Exception {

		if(appConfig.isGenerateData()){
			orchestrator.createNewData();
		}

		orchestrator.launchRealTime();


	}
}
