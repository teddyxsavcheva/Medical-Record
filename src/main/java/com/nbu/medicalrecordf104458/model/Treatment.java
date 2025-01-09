package com.nbu.medicalrecordf104458.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "treatment")
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Medicine name cannot be empty")
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @NotEmpty(message = "Dosage amount cannot be empty")
    @Column(name = "dosage_amount", nullable = false)
    private String dosageAmount;

    @NotEmpty(message = "Frequency cannot be empty")
    @Column(name = "frequency", nullable = false)
    private String frequency;

    @NotNull(message = "Doctor appointment cannot be null")
    @ManyToOne
    private DoctorAppointment doctorAppointment;
}
