package org.durgaprabhu.interview.service;

import org.apache.logging.log4j.util.Strings;
import org.durgaprabhu.interview.dto.CompanyDetails;
import org.durgaprabhu.interview.dto.OfficerDetails;
import org.durgaprabhu.interview.mapper.CompanyDetailsSearchResultMapper;
import org.durgaprabhu.interview.model.*;
import org.durgaprabhu.interview.thirdparty.provider.api.TrunArrativeSearchAPI;
import org.durgaprabhu.interview.thirdparty.provider.model.TrunArrativeCompanyOfficerRecord;
import org.durgaprabhu.interview.thirdparty.provider.model.TrunArrativeCompanyOfficersSearchResponse;
import org.durgaprabhu.interview.thirdparty.provider.model.TrunArrativeCompanyRecord;
import org.durgaprabhu.interview.thirdparty.provider.model.TrunArrativeCompanySearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CompanyDetailsSearchServiceImpl implements CompanyDetailsSearchService {

    private final static Logger logger = LoggerFactory.getLogger(CompanyDetailsSearchServiceImpl.class);

    private static final String ACTIVE = "active";
    private final TrunArrativeSearchAPI trunArrativeSearchAPI;

    public CompanyDetailsSearchServiceImpl(TrunArrativeSearchAPI trunArrativeSearchAPI) {
        this.trunArrativeSearchAPI = trunArrativeSearchAPI;
    }

    @Override
    public CompanySearchResponse readCompanies(CompanySearchRequest companySearchDetailsRequest, boolean isActive) throws RestClientException {
        logger.trace("Entering searchCompany method");

        TrunArrativeCompanySearchResponse trunArrativeCompanySearchResponse;
        if(Strings.isNotBlank(companySearchDetailsRequest.getCompanyNumber())){
            trunArrativeCompanySearchResponse = trunArrativeSearchAPI.findAllCompaniesByCompanyNumber(companySearchDetailsRequest.getCompanyNumber());
        }else{
            trunArrativeCompanySearchResponse = trunArrativeSearchAPI.findAllCompaniesByCompanyName(companySearchDetailsRequest.getCompanyName());
        }

        if(Objects.nonNull(trunArrativeCompanySearchResponse) && Objects.nonNull(trunArrativeCompanySearchResponse.getRecords())){
            return prepareCompanySearchResponse(isActive, trunArrativeCompanySearchResponse.getRecords());
        }

        logger.trace("Leaving searchCompany method");
        return null;
    }

    private CompanySearchResponse prepareCompanySearchResponse(boolean isActive, List<TrunArrativeCompanyRecord> trunArrativeCompanyRecordList) {
        logger.trace("Entering prepareCompanySearchResponse method");

        trunArrativeCompanyRecordList = isActive ? extractActiveCompaniesOnly(trunArrativeCompanyRecordList) : trunArrativeCompanyRecordList;
        CompanySearchResponse companySearchResponse = new CompanySearchResponse();
        companySearchResponse.setTotal_results(trunArrativeCompanyRecordList.size());
        companySearchResponse.setItems(trunArrativeCompanyRecordList.stream().map(this::prepareCompanyDetailsDto).toList());
        logger.trace("Leaving prepareCompanySearchResponse method");
        return companySearchResponse;
    }

    private CompanyDetails prepareCompanyDetailsDto(TrunArrativeCompanyRecord trunArrativeCompanyRecord) throws RestClientException{
        logger.trace("prepareCompanyDetailsDto method");

        TrunArrativeCompanyOfficersSearchResponse trunArrativeCompanyOfficersSearchResponse = trunArrativeSearchAPI.findAllCompanyOfficersByCompanyNumber(
                trunArrativeCompanyRecord.getCompanyNumber());
        if(Objects.nonNull(trunArrativeCompanyOfficersSearchResponse) && Objects.nonNull(trunArrativeCompanyOfficersSearchResponse.getRecords())){
            List<OfficerDetails> officerDetailsList = extractActiveOfficersOnly(trunArrativeCompanyOfficersSearchResponse.getRecords());
            return CompanyDetailsSearchResultMapper.mapTrunArrativeCompanyRecordToCompanyDetailsDto(trunArrativeCompanyRecord,
                    officerDetailsList);
        }else{
            return CompanyDetailsSearchResultMapper.mapTrunArrativeCompanyRecordToCompanyDetailsDto(trunArrativeCompanyRecord,
                    new ArrayList<>());
        }
    }

    private static List<OfficerDetails> extractActiveOfficersOnly(
            List<TrunArrativeCompanyOfficerRecord> trunArrativeCompanyOfficerRecordList) {
        logger.trace("extractActiveOfficersOnly method");

        return trunArrativeCompanyOfficerRecordList.stream()
                .filter(o -> Objects.isNull(o.getResignedOn())).map(CompanyDetailsSearchResultMapper::mapTrunArrativeCompanyOfficerRecordToOfficerDetailsDto).toList();
    }

    private static List<TrunArrativeCompanyRecord> extractActiveCompaniesOnly(
            List<TrunArrativeCompanyRecord> trunArrativeCompanyRecords) {
        logger.trace("extractActiveCompaniesOnly method");

        return trunArrativeCompanyRecords.stream().filter(c->
                ACTIVE.equalsIgnoreCase(c.getCompanyStatus())).toList();
    }
}
