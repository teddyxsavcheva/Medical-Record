package com.nbu.medicalrecordf104458.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("GP") // Discriminator value for General Practitioner
public class GeneralPractitioner extends Doctor {

    @OneToMany(mappedBy = "familyDoctor")
    private Set<Patient> patients = new HashSet<>();

}