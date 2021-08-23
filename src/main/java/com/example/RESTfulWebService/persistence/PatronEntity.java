package com.example.RESTfulWebService.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "patrons")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatronEntity {

    @Id
    @Column(name = "patron_id")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    @NotNull
    private UUID id;

    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "Date of Birth must not be null")
    private String dateOfBirth;

    @NotNull(message = "Phone Number must not be null")
    private String phoneNumber;
}
