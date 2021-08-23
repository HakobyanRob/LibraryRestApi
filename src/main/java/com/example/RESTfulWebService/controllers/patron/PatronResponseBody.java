package com.example.RESTfulWebService.controllers.patron;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class PatronResponseBody {
    private UUID uuid;

    private String name;

    private String dateOfBirth;

    private String phoneNumber;
}
