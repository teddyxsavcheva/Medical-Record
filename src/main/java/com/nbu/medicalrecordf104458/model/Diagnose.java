package com.nbu.medicalrecordf104458.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "diagnose")
public class Diagnose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Diagnose name cannot be empty")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotEmpty(message = "Diagnose description cannot be empty")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @ManyToMany
    private Set<DoctorAppointment> appointments = new HashSet<>();

}
