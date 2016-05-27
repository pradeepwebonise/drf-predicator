package com.drf.predictor.models;

import java.io.Serializable;
import java.text.DecimalFormat;

public class PredictionDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private long count;
    private double totalPayout;
    private double profit;
    
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getProfit() {
        return Double.valueOf(decimalFormat.format(profit));
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getTotalPayout() {
        return Double.valueOf(decimalFormat.format(totalPayout));
    }

    public void setTotalPayout(double totalPayout) {
        this.totalPayout = totalPayout;
    }

}
