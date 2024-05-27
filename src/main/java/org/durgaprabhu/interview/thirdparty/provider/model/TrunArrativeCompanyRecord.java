package org.durgaprabhu.interview.thirdparty.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.durgaprabhu.interview.dto.AddressDetails;

@Setter
@Getter
public class TrunArrativeCompanyRecord {

    @JsonProperty("company_status")
    private String companyStatus;

    @JsonProperty("date_of_creation")
    private String createdDate;
    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("address")
    private AddressDetails address;

    @JsonProperty("description")
    private String description;
}
