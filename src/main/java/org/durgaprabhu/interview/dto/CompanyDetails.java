package org.durgaprabhu.interview.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CompanyDetails {

    private String company_number;
    private String company_type;
    private String title;
    private String company_status;
    private String date_of_creation;
    private List<OfficerDetails> officers;
    private AddressDetails address;
}
