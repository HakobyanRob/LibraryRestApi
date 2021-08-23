package com.example.RESTfulWebService.controllers.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class BookResponseBody {
    private UUID id;
    private String title;
    private String ISBN;
    private double cost;
    private UUID takenByUserID;
}
