package com.BackEnd.Master.GYM.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class PackDto {
    private Long id;
    private String name;
    private String description;
    @JsonProperty("nMonth")
    private Integer nMonth;
    private Double price;
}
