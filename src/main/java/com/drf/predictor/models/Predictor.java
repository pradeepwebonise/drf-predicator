package com.drf.predictor.models;

import java.io.Serializable;
import java.util.Map;

public class Predictor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String date;
    private Long totalRacesCount;
    private Map<String, Long> wagersPredicationMap;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTotalRacesCount() {
        return totalRacesCount;
    }

    public void setTotalRacesCount(Long totalRacesCount) {
        this.totalRacesCount = totalRacesCount;
    }

    public Map<String, Long> getWagersPredicationMap() {
        return wagersPredicationMap;
    }

    public void setWagersPredicationMap(Map<String, Long> wagersPredicationMap) {
        this.wagersPredicationMap = wagersPredicationMap;
    }

}
