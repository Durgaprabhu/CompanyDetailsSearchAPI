package org.durgaprabhu.interview.thirdparty.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TrunArrativeCompanyOfficersSearchResponse {

    @JsonProperty("items")
    private List<TrunArrativeCompanyOfficerRecord> records;
}
