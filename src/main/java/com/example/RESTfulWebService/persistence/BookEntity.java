package com.example.RESTfulWebService.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookEntity implements Serializable {
    private static final long serialVersionUID = 5159957046482384351L;

    @Id
    @Column(name = "book_id")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID id;

    @NotNull(message = "Title must not be null")
    private String title;

    @NotNull(message = "ISBN must not be null")
    private String ISBN;

    @NotNull
    private double cost;

    @Setter
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID takenByUserId;
}