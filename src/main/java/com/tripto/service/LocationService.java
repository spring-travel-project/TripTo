package com.tripto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.LocationDAO;
import com.tripto.dto.LocationDTO;

@Service
public class LocationService {

    @Autowired
    private LocationDAO dao;

    public int add(LocationDTO dto) {
        return dao.add(dto);
    }

    public LocationDTO get(int seqLocation) {
        return dao.get(seqLocation);
    }
}