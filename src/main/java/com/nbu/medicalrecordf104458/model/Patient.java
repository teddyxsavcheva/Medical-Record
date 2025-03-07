package com.nbu.medicalrecordf104458.model;

import com.nbu.medicalrecordf104458.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Patient name cannot be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Unified civil number cannot be empty")
    @Column(name = "unified_civil_number", unique = true, nullable = false)
    private Long unifiedCivilNumber;

    @NotNull(message = "Last insurance payment date cannot be null")
    @Column(name = "last_insurance_payment_date", nullable = false)
    private LocalDate lastInsurancePayment;

    @Column(name = "for_deletion", nullable = false)
    private boolean deleted = false;

    @NotNull(message = "Each patient should be assigned to a family doctor.")
    @ManyToOne
    @JoinColumn(name = "family_doctor_id", nullable = false)
    private GeneralPractitioner familyDoctor;

    @OneToMany(mappedBy = "patient")
    @Column(name = "doctor_appointments_id")
    private Set<DoctorAppointment> appointments = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}