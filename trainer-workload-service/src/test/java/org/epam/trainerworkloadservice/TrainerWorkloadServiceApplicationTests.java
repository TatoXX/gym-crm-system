package org.epam.trainerworkloadservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.data.mongodb.auto-index-creation=false",
		"eureka.client.enabled=false",
		"eureka.client.register-with-eureka=false",
		"eureka.client.fetch-registry=false",
		"spring.cloud.discovery.enabled=false"
})
class TrainerWorkloadServiceApplicationTests {

	@Test
	void contextLoads() {
	}
}