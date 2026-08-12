package com.BackEnd.Master.GYM.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriptionDto {
    private Long id;
    private Long customerId;
    private String packName;
    @JsonProperty("nMonth")
    private Integer nMonth;
    private Double price;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double amountPaid;
    private String note;
    private Boolean valid;
}
