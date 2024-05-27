package org.durgaprabhu.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.durgaprabhu.interview.model.CompanySearchRequest;
import org.durgaprabhu.interview.model.CompanySearchResponse;
import org.durgaprabhu.interview.service.CompanyDetailsSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.Objects;

@RestController
@RequestMapping("/companies")
@Tag(name = "Company", description = "Read Company Details API")
public class CompanySearchDetailsController {

    private final static Logger logger = LoggerFactory.getLogger(CompanySearchDetailsController.class);

    private CompanyDetailsSearchService companyDetailsSearchService;

    @Autowired
    public CompanySearchDetailsController(CompanyDetailsSearchService companyDetailsSearchService){
        this.companyDetailsSearchService = companyDetailsSearchService;
    }

    @PostMapping(value = "/v1/search", produces="application/json", consumes = "application/json")
    @Operation(summary = "Read Company Details for the given company number or company name, company number considered when both are present")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Company details",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanySearchResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Company Details Not Found",
                    content = @Content),
            @ApiResponse(responseCode = "503", description = "Service Unavailable",
                    content = @Content)})
    public ResponseEntity<Object> readCompanies(@RequestBody CompanySearchRequest companySearchDetailsRequest,
                                                @RequestParam(name = "status", defaultValue = "false") boolean isActive) {
        logger.trace("readCompanies method");

        try{
            CompanySearchResponse response = companyDetailsSearchService.readCompanies(companySearchDetailsRequest, isActive);
            if(Objects.nonNull(response)){
                return ResponseEntity.ok(response);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch(RestClientException restClientException){
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
