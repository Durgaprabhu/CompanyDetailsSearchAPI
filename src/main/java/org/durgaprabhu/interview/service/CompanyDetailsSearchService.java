package org.durgaprabhu.interview.service;

import org.durgaprabhu.interview.model.CompanySearchRequest;
import org.durgaprabhu.interview.model.CompanySearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


public interface CompanyDetailsSearchService {

    CompanySearchResponse readCompanies(CompanySearchRequest companySearchDetailsRequest, boolean isActive) throws RestClientException;
}
