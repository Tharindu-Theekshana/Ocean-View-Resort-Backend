package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @jakarta.persistence.Id
    private Long id;

}
