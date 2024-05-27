package org.durgaprabhu.interview.thirdparty.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.durgaprabhu.interview.dto.AddressDetails;

@Getter
@Setter
public class TrunArrativeCompanyOfficerRecord {

    @JsonProperty("name")
    private String name;

    @JsonProperty("officer_role")
    private String role;

    @JsonProperty("appointed_on")
    private String appointedOn;

    @JsonProperty("resigned_on")
    private String resignedOn;

    @JsonProperty("address")
    private AddressDetails address;
}
