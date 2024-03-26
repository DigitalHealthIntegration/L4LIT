package ca.uhn.fhir.jpa.starter.common;

import ca.uhn.fhir.jpa.config.r4.JpaR4Config;
import ca.uhn.fhir.jpa.starter.annotations.OnR4Condition;
import ca.uhn.fhir.jpa.starter.cr.StarterCrR4Config;
import ca.uhn.fhir.jpa.starter.ips.StarterIpsConfig;
import org.springframework.context.annotation.*;

@Configuration
@Conditional(OnR4Condition.class)
@Import({
	JpaR4Config.class,
	StarterJpaConfig.class,
	StarterCrR4Config.class,
	ElasticsearchConfig.class,
	StarterIpsConfig.class
})
public class FhirServerConfigR4 {

//	@Bean
//	public RestfulServer fhirRestfulServer(FhirContext fhirContext) {
//		RestfulServer restfulServer = new RestfulServer(fhirContext);
//		restfulServer.registerProvider(new MyPatientResourceProvider()); // Register your resource provider
//		return restfulServer;
//	}

//	@Bean
//	public JpaR4Config fhirR4Config() {
//		return new JpaR4Config();
//	}

//	@Bean
//	public RestfulServer fhirRestfulServer(FhirContext fhirContext) {
//		RestfulServer restfulServer = new RestfulServer(fhirContext);
//		restfulServer.registerProvider(new MyPatientResourceProvider()); // Register your resource provider
//		return restfulServer;
//	}

}
