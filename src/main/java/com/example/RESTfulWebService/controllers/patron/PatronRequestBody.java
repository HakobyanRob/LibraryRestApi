package com.example.RESTfulWebService.controllers.patron;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PatronRequestBody {
    private String uuid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("phoneNumber")
    private String phoneNumber;
}
