package org.durgaprabhu.interview.thirdparty.provider.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
public class TrunArrativeConfig {

    @Value("${truproxy.api.key}")
    private String trunArrativeApiKey;
    @Value("${truproxy.api.baseUrl}")
    private String trunArrativeBaseUrl;

}
