package com.drf.predictor.service;

import java.util.Date;

import com.drf.proservice.model.EntriesDetailsWrapper;

public interface PredictorService {
    
    EntriesDetailsWrapper getRacePredictor(Date date);
}
