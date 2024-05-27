package org.durgaprabhu.interview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "wiremock-setup-config")
@Data
@Profile("integration")
public class CompanyDetailsApplicationWireMockConfig {
        private List<CompanyDetailsApplicationWireMockProxy> proxies;
}
