package org.durgaprabhu.interview.config;

import lombok.Data;

@Data
public class CompanyDetailsApplicationWireMockProxy {
        private String name;
        private int port;
        private String url;
        private boolean stubbing;
}
