package com.drf.predictor.service.impl;

import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.drf.predictor.service.PredictorService;
import com.drf.proservice.model.EntriesDatesWrapper;
import com.drf.proservice.model.EntriesDetailsWrapper;
import com.drf.proservice.service.EntriesService;
import com.drf.proservice.service.ResultsService;

import drf.common.wrappers.entries.RaceDTOWrapper;
import drf.common.wrappers.entries.TrackEntry;
import drf.common.wrappers.results.ResultDetailsWrapper;
import drf.common.wrappers.results.ResultRaceDTOWrapper;

@Service
public class PredictorServiceImpl implements PredictorService {

    private static final Logger LOG = LoggerFactory.getLogger(PredictorServiceImpl.class);

    @Autowired
    private EntriesService entriesService;

    @Autowired
    private ResultsService resultService;

    @Value("${breeds}")
    private String breeds;

    @Override
    public EntriesDetailsWrapper getRacePredictor(Date date) {

        EntriesDatesWrapper entriesList = entriesService.getEntriesList(date, breeds);
        HashMap<String, Long> mapList = new HashMap<String, Long>();
        mapList.put("WIN", null);
        mapList.put("PLACE", null);
        mapList.put("SHOW", null);
        mapList.put("EXACTA", null);
        mapList.put("TRIFECTA", null);

        long totalRaces = 0;

        for ( TrackEntry trackEntry : entriesList.getEntries() ) {
            HashMap<Integer, ResultRaceDTOWrapper> resultsMap = new HashMap<Integer, ResultRaceDTOWrapper>();

            ResultDetailsWrapper resultDetailsWrapper = this.fetchResultForRace(trackEntry.getTrackId(), trackEntry.getCountry(), date);
            if ( resultDetailsWrapper != null && resultDetailsWrapper.getResults() != null ) {
                totalRaces += resultDetailsWrapper.getResults().size();
                for ( ResultRaceDTOWrapper resultRaceDTOWrapper : resultDetailsWrapper.getResults() ) {
                    resultsMap.put(resultRaceDTOWrapper.getRaceKey().getRaceNumber(), resultRaceDTOWrapper);
                }
                
                EntriesDetailsWrapper entriesDetailsWrapper = entriesService.getEntriesDetailsWrapper(trackEntry.getTrackId(), trackEntry.getCountry(), date, breeds);
                if(entriesDetailsWrapper != null && entriesDetailsWrapper.getRaces() != null) {
                    for ( RaceDTOWrapper raceDTOWrapper : entriesDetailsWrapper.getRaces() ) {
                        if(resultsMap.containsKey(raceDTOWrapper.getRaceKey().getRaceNumber())) {
                           // buildPredictionMap();
                        }
                    }
                }
                
            }
        }

        EntriesDetailsWrapper racesDetailsWrapper = entriesService.getEntriesDetailsWrapper("AQU", "USA", new Date(), breeds);
        // try {
        // LOG.debug("races: {}", new
        // ObjectMapper().writeValueAsString(racesDetailsWrapper));
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        return racesDetailsWrapper;
    }

    private ResultDetailsWrapper fetchResultForRace(String trackId, String country, Date date) {
        return resultService.getResultDetailsWrapper(trackId, country, date, breeds);
    }

}
