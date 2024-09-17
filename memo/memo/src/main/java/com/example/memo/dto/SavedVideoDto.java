package com.example.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedVideoDto {

    private long id;
    private long videoId;
    private String memberEmail;
    private LocalDate savedDate;

}
