package com.frameforward.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

class DataSourceConfigurationTests {

    @Test
    void productionAndDevelopmentDataSourcesRequireExplicitCredentials() throws IOException {
        assertThat(resource("application.yml")).doesNotContain("${FRAME_FORWARD_DB_USERNAME:")
                .doesNotContain("${FRAME_FORWARD_DB_PASSWORD:");
        assertThat(resource("application-dev.yml")).contains("${FRAME_FORWARD_DB_URL}")
                .contains("${FRAME_FORWARD_DB_USERNAME}").contains("${FRAME_FORWARD_DB_PASSWORD}");
        assertThat(resource("application-prod.yml")).contains("${FRAME_FORWARD_DB_URL}")
                .contains("${FRAME_FORWARD_DB_USERNAME}").contains("${FRAME_FORWARD_DB_PASSWORD}");
    }

    private String resource(String name) throws IOException {
        return StreamUtils.copyToString(new ClassPathResource(name).getInputStream(), StandardCharsets.UTF_8);
    }
}
