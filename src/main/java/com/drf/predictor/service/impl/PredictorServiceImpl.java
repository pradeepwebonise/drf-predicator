package com.drf.predictor.service.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.drf.common.dto.results.RunnerDTO;
import com.drf.predictor.models.Predictor;
import com.drf.predictor.service.PredictorService;
import com.drf.proservice.model.EntriesDatesWrapper;
import com.drf.proservice.model.EntriesDetailsWrapper;
import com.drf.proservice.service.EntriesService;
import com.drf.proservice.service.ResultsService;

import drf.common.wrappers.analysis.ExpertsScore;
import drf.common.wrappers.analysis.HorseDetails;
import drf.common.wrappers.entries.RaceDTOWrapper;
import drf.common.wrappers.entries.TrackEntry;
import drf.common.wrappers.results.ResultDetailsWrapper;
import drf.common.wrappers.results.ResultEntry;
import drf.common.wrappers.results.ResultRaceDTOWrapper;
import drf.common.wrappers.results.ResultsWrapper;
import drf.common.wrappers.results.RunnerDTOWrapper;

@Service
public class PredictorServiceImpl implements PredictorService {

    private static final Logger LOG = LoggerFactory.getLogger(PredictorServiceImpl.class);

    @Autowired
    private EntriesService entriesService;

    @Autowired
    private ResultsService resultService;

    private static final String DATE_FORMAT = "MM/dd/yyyy";

    @Value("${breeds}")
    private String breeds;

    @Override
    public Predictor getRacePredictor(Date date) {

        HashMap<String, Long> predicatMap = buildPredicatMap();

        ResultsWrapper resultList = resultService.getResultList(date, breeds);
        long totalRaces = 0;

        for ( ResultEntry trackEntry : resultList.getResults() ) {
            HashMap<Integer, ResultRaceDTOWrapper> resultsMap = new HashMap<Integer, ResultRaceDTOWrapper>();

            ResultDetailsWrapper resultDetailsWrapper = this.fetchResultForRace(trackEntry.getTrackId(), trackEntry.getCountry(), date);
            if ( resultDetailsWrapper != null && resultDetailsWrapper.getResults() != null ) {
                totalRaces += resultDetailsWrapper.getResults().size();
                for ( ResultRaceDTOWrapper resultRaceDTOWrapper : resultDetailsWrapper.getResults() ) {
                    resultsMap.put(resultRaceDTOWrapper.getRaceKey().getRaceNumber(), resultRaceDTOWrapper);
                }

                EntriesDetailsWrapper entriesDetailsWrapper = entriesService.getEntriesDetailsWrapper(trackEntry.getTrackId(), trackEntry.getCountry(), date, breeds);
                if ( entriesDetailsWrapper != null && entriesDetailsWrapper.getRaces() != null ) {
                    for ( RaceDTOWrapper raceDTOWrapper : entriesDetailsWrapper.getRaces() ) {
                        int raceNumber = raceDTOWrapper.getRaceKey().getRaceNumber();
                        if ( resultsMap.containsKey(raceNumber) ) {
                            ResultRaceDTOWrapper resultRaceDTOWrapper = resultsMap.get(raceNumber);
                            List<HorseDetails> horseAnalysisList = this.fetchAnalysis(raceDTOWrapper);
                            this.predictForWinPlaceShow(predicatMap, resultRaceDTOWrapper.getRunnerDTOs(), horseAnalysisList);
                        }
                    }
                }

            }
        }
        Predictor predictor = new Predictor();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        predictor.setDate(dateFormat.format(date));
        predictor.setTotalRacesCount(totalRaces);
        predictor.setWagersPredicationMap(predicatMap);
        return predictor;
    }

    private HashMap<String, Long> buildPredicatMap() {
        HashMap<String, Long> predicatMap = new HashMap<String, Long>();
        predicatMap.put("WIN", 0L);
        predicatMap.put("PLACE", 0L);
        predicatMap.put("SHOW", 0L);
        predicatMap.put("EXACTA", 0L);
        predicatMap.put("TRIFECTA", 0L);
        return predicatMap;
    }

    private void predictForWinPlaceShow(HashMap<String, Long> predicatList, List<RunnerDTOWrapper> WinnersHorses, List<HorseDetails> horseAnalysisList) {
        if ( !horseAnalysisList.isEmpty() ) {
            int exactCount = 0;
            int trifectaCount = 0;
            if ( horseAnalysisList.get(0).getProgramNumber().trim().equals(WinnersHorses.get(0).getProgramNumber().trim()) ) {
                Long count = predicatList.get("WIN");
                count++;
                predicatList.put("WIN", count);
                exactCount++;
                trifectaCount++;
            }
            if ( horseAnalysisList.get(1).getProgramNumber().trim().equals(WinnersHorses.get(1).getProgramNumber().trim()) ) {
                Long count = predicatList.get("PLACE");
                count++;
                predicatList.put("PLACE", count);
                exactCount++;
                trifectaCount++;
            }
            if ( horseAnalysisList.get(2).getProgramNumber().trim().equals(WinnersHorses.get(2).getProgramNumber().trim()) ) {
                Long count = predicatList.get("SHOW");
                count++;
                predicatList.put("SHOW", count);
                trifectaCount++;
            }
            if ( exactCount == 2 ) {
                Long count = predicatList.get("EXACTA");
                count++;
                predicatList.put("EXACTA", count);
            }
            if ( trifectaCount == 3 ) {
                Long count = predicatList.get("TRIFECTA");
                count++;
                predicatList.put("TRIFECTA", count);
            }
        }
    }

    private List<HorseDetails> fetchAnalysis(RaceDTOWrapper raceDTOWrapper) {
        List<ExpertsScore> expertScore = raceDTOWrapper.getExpertScore();
        if ( expertScore != null && !expertScore.isEmpty() ) {
            for ( int i = 0; i < expertScore.size(); i++ ) {
                if ( expertScore.get(i).getHeading1().equals("CONSENSUS") ) {
                    return expertScore.get(i).getHorseDetails();
                }
            }
        }
        return Collections.emptyList();
    }

    private ResultDetailsWrapper fetchResultForRace(String trackId, String country, Date date) {
        return resultService.getResultDetailsWrapper(trackId, country, date, breeds);
    }

}
