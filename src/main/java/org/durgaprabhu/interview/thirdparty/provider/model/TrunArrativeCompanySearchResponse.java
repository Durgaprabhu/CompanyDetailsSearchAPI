package org.durgaprabhu.interview.thirdparty.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TrunArrativeCompanySearchResponse {

    @JsonProperty("total_results")
    private int totalResults;

    @JsonProperty("items")
    private List<TrunArrativeCompanyRecord> records;
}
