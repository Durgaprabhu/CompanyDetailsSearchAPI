package org.durgaprabhu.interview.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OfficerDetails {

    private String name;
    private String officer_role;
    private String appointed_on;
    private AddressDetails address;
}
