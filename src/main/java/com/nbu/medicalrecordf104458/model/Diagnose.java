package com.nbu.medicalrecordf104458.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Diagnose type cannot be null")
    @ManyToOne
    @JoinColumn(name = "diagnose_type_id", nullable = false)
    private DiagnoseType diagnoseType;

    @OneToMany(mappedBy = "diagnose")
    private Set<DoctorAppointment> doctorAppointments = new HashSet<>();
}
