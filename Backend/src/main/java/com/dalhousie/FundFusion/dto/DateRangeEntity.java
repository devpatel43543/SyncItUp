package com.dalhousie.FundFusion.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRangeEntity {

    private Integer userId;
    private LocalDate fromDate;
    private  LocalDate toDate;

}
