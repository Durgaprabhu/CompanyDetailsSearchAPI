package org.durgaprabhu.interview.model;

import lombok.Getter;
import lombok.Setter;
import org.durgaprabhu.interview.dto.CompanyDetails;

import java.util.List;

@Setter
@Getter
public class CompanySearchResponse {

    public int total_results;
    public List<CompanyDetails> items;
}
