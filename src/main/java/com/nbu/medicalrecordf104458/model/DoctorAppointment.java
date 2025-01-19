package com.nbu.medicalrecordf104458.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "appointment")
public class DoctorAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Visit date cannot be null")
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @NotNull(message = "An appointment must have a patient")
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "An appointment must have a doctor")
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotEmpty(message = "An appointment must have at least one diagnose")
    @ManyToMany(mappedBy = "appointments")
    private List<Diagnose> diagnoses;

    @ManyToMany(mappedBy = "appointments")
    private List<Treatment> treatments = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "sick_leave_id", unique = true)
    private SickLeave sickLeave;

}
