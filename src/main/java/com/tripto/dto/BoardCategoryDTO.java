package com.tripto.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BoardCategoryDTO {

    private int seqCategory;
    private String categoryName;
    private int sortOrder;
    private String useYn;
}