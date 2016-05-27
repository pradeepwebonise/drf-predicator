package com.drf.predictor.models;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Predictor implements Serializable {

    private static final double BASE_BET = 2.0;

    private static final long serialVersionUID = 1L;

    private String date;
    private Long totalRacesCount;
    private double baseBet;
    private double totalAmountWagered;
    private Map<String, PredictionDetails> wagersPredicationMap;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

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

    public Map<String, PredictionDetails> getWagersPredicationMap() {
        return wagersPredicationMap;
    }

    public void setWagersPredicationMap(HashMap<String, PredictionDetails> predicatMap) {
        this.wagersPredicationMap = predicatMap;
    }

    public double getBaseBet() {
        return BASE_BET;
    }

    public void setBaseBet(double baseBet) {
        this.baseBet = baseBet;
    }

    public double getTotalAmountWagered() {
        return Double.valueOf(decimalFormat.format(totalAmountWagered));
    }

    public void setTotalAmountWagered(double totalAmountWagered) {
        this.totalAmountWagered = totalAmountWagered;
    }

}
