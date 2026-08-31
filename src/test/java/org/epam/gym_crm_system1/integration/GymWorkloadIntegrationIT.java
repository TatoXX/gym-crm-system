package org.epam.gym_crm_system1.integration;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource(
        "features/integration/gym-workload-integration.feature"
)
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "org.epam.gym_crm_system1.integration"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty,summary"
)
public class GymWorkloadIntegrationIT {
}