package com.tripto.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LocationDTO {

    private int seqLocation;

    private String placeName;
    private String address;
    private double latitude;
    private double longitude;
    private String mapProviderId;
    private int seqTravelPost;
}