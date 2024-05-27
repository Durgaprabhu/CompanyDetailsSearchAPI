package org.durgaprabhu.interview.mapper;

import org.durgaprabhu.interview.dto.CompanyDetails;
import org.durgaprabhu.interview.dto.OfficerDetails;
import org.durgaprabhu.interview.thirdparty.provider.model.TrunArrativeCompanyOfficerRecord;
import org.durgaprabhu.interview.thirdparty.provider.model.TrunArrativeCompanyRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CompanyDetailsSearchResultMapper {

    private final static Logger logger = LoggerFactory.getLogger(CompanyDetailsSearchResultMapper.class);

    public static OfficerDetails mapTrunArrativeCompanyOfficerRecordToOfficerDetailsDto(TrunArrativeCompanyOfficerRecord trunArrativeCompanyOfficerRecord) {
        logger.trace("Entering mapTrunArrativeOfficerRecordToOfficerDetailsDto method");

        OfficerDetails officerDetails = new OfficerDetails();
        officerDetails.setName(trunArrativeCompanyOfficerRecord.getName());
        officerDetails.setOfficer_role(trunArrativeCompanyOfficerRecord.getRole());
        officerDetails.setAddress(trunArrativeCompanyOfficerRecord.getAddress());
        officerDetails.setAppointed_on(trunArrativeCompanyOfficerRecord.getAppointedOn());

        logger.trace("Leaving mapTrunArrativeOfficerRecordToOfficerDetailsDto method");
        return officerDetails;
    }

    public static CompanyDetails mapTrunArrativeCompanyRecordToCompanyDetailsDto(TrunArrativeCompanyRecord trunArrativeCompanyRecord, List<OfficerDetails> officerDetailsList) {
        logger.trace("Entering mapTrunArrativeCompanyRecordToCompanyDetailsDto method");

        CompanyDetails companyDetails = new CompanyDetails();
        companyDetails.setCompany_number(trunArrativeCompanyRecord.getCompanyNumber());
        companyDetails.setCompany_type(trunArrativeCompanyRecord.getCompanyType());
        companyDetails.setTitle(trunArrativeCompanyRecord.getTitle());
        companyDetails.setCompany_status(trunArrativeCompanyRecord.getCompanyStatus());
        companyDetails.setDate_of_creation(trunArrativeCompanyRecord.getCreatedDate());
        companyDetails.setAddress(trunArrativeCompanyRecord.getAddress());
        companyDetails.setOfficers(officerDetailsList);

        logger.trace("Leaving mapTrunArrativeCompanyRecordToCompanyDetailsDto method");
        return companyDetails;
    }

}
