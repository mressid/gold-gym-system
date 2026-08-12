package com.BackEnd.Master.GYM.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Snapshot of the pack picked at subscription time (pack is just a template, no FK kept)
    private String packName;
    private Integer nMonth;
    private Double price;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Double amountPaid;

    private String note;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private customer customer;

    @Transient
    public boolean isValid() {
        return dateFin != null && !dateFin.isBefore(LocalDate.now());
    }
}
