package com.drf.predictor.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.drf.predictor.service.PredictorService;
import com.drf.proservice.model.EntriesDetailsWrapper;
import com.drf.proservice.service.EntriesService;

@Service
public class PredictorServiceImpl implements PredictorService {

    private static final Logger LOG = LoggerFactory.getLogger(PredictorServiceImpl.class);

    
    @Autowired
    private EntriesService entriesService;
    
    @Value("${breeds}")
    private String breeds;

    @Override
    public EntriesDetailsWrapper getRacePredictor() {        
        EntriesDetailsWrapper racesDetailsWrapper = entriesService.getEntriesDetailsWrapper("AQU", "USA", new Date(), breeds);
        
        
//        try {
//            LOG.debug("races: {}", new ObjectMapper().writeValueAsString(racesDetailsWrapper));
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return racesDetailsWrapper;
    }

}
