package com.drf.predictor.service;

import java.util.Date;

import com.drf.predictor.models.Predictor;

public interface PredictorService {
    
    Predictor getRacePredictor(Date date);
}
