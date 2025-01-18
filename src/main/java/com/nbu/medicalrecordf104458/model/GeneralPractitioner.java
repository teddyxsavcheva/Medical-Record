package com.nbu.medicalrecordf104458.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("GP") // Discriminator value for General Practitioner
public class GeneralPractitioner extends Doctor {

    @OneToMany(mappedBy = "familyDoctor")
    // To prevent NullPointerException as GP may not have patients
    private List<Patient> patients = new ArrayList<>();

}