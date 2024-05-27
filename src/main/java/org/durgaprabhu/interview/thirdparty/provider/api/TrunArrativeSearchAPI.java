package org.durgaprabhu.interview.thirdparty.provider.api;

import org.durgaprabhu.interview.thirdparty.provider.model.TrunArrativeCompanyOfficersSearchResponse;
import org.durgaprabhu.interview.thirdparty.provider.model.TrunArrativeCompanySearchResponse;
import org.durgaprabhu.interview.thirdparty.provider.config.TrunArrativeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class TrunArrativeSearchAPI {
    private final static Logger logger = LoggerFactory.getLogger(TrunArrativeSearchAPI.class);

    private static final HttpMethod GET = HttpMethod.GET;
    private static final String X_API_KEY = "x-api-key";
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;

    private final RestTemplate restTemplate;
    private final TrunArrativeConfig trunArrativeConfig;

    public TrunArrativeSearchAPI(TrunArrativeConfig trunArrativeConfig,
                                 RestTemplate restTemplate){
        this.trunArrativeConfig = trunArrativeConfig;
        this.restTemplate = restTemplate;
    }

    public TrunArrativeCompanySearchResponse findAllCompaniesByCompanyNumber(String companyNumber) throws RestClientException{
        logger.trace("findCompanyDetailsByCompanyNumber method companyNumber :{}", companyNumber);
        return findCompanyDetails(companyNumber);
    }

    public TrunArrativeCompanySearchResponse findAllCompaniesByCompanyName(String companyName) throws RestClientException{
        logger.trace("findCompanyDetailsByCompanyName method companyName :{}", companyName);
        return findCompanyDetails(companyName);
    }

    public TrunArrativeCompanyOfficersSearchResponse findAllCompanyOfficersByCompanyNumber(String companyNumber) throws RestClientException{
        logger.trace("findCompanyOfficersDetailsByCompanyNumber method companyNumber :{}", companyNumber);

        String companyOfficersSearchUrl = String.format("%s/Companies/v1/Officers?CompanyNumber=%s", trunArrativeConfig.getTrunArrativeBaseUrl(), companyNumber);
        HttpEntity<String> entity = prepareRequestHttpEntity(trunArrativeConfig.getTrunArrativeApiKey());
        try{
            ResponseEntity<TrunArrativeCompanyOfficersSearchResponse> trunArrativeCompanyOfficerSearchResponse =
                    restTemplate.exchange(companyOfficersSearchUrl, GET, entity, TrunArrativeCompanyOfficersSearchResponse.class);
            if(trunArrativeCompanyOfficerSearchResponse.getStatusCode().is2xxSuccessful()){
                return trunArrativeCompanyOfficerSearchResponse.getBody();
            }else{
                return null;
            }
        }catch(RuntimeException ex){ //When the record is not found the end point is throwing 500 error, so need to handle it
            logger.info(" RuntimeException is thrown : {} ",ex.getMessage());
            return null;
        }
    }

    private TrunArrativeCompanySearchResponse findCompanyDetails(String searchParam) throws RestClientException{
        logger.trace("findCompanyDetails method");

        String companySearchUrl = String.format("%s/Companies/v1/Search?Query=%s",trunArrativeConfig.getTrunArrativeBaseUrl(), searchParam);

        HttpEntity<String> entity = prepareRequestHttpEntity(trunArrativeConfig.getTrunArrativeApiKey());
        ResponseEntity<TrunArrativeCompanySearchResponse> trunArrativeCompanySearchResponse =
                restTemplate.exchange(companySearchUrl, GET, entity, TrunArrativeCompanySearchResponse.class);
        if(trunArrativeCompanySearchResponse.getStatusCode().is2xxSuccessful()){
            return trunArrativeCompanySearchResponse.getBody();
        }else{
            return null;
        }
    }

    private static HttpEntity<String> prepareRequestHttpEntity(String apiKey) {
        logger.trace("Entering getRequestHttpEntity method");

        HttpHeaders headers = new HttpHeaders();
        headers.set(X_API_KEY,apiKey);
        headers.setContentType(APPLICATION_JSON);

        logger.trace("Leaving getRequestHttpEntity method");
        return new HttpEntity<>(headers);
    }
}
