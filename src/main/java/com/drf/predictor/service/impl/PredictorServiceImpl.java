package com.drf.predictor.service.impl;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.drf.predictor.models.PredictionDetails;
import com.drf.predictor.models.Predictor;
import com.drf.predictor.service.PredictorService;
import com.drf.proservice.model.EntriesDetailsWrapper;
import com.drf.proservice.service.EntriesService;
import com.drf.proservice.service.ResultsService;

import drf.common.wrappers.analysis.ExpertsScore;
import drf.common.wrappers.analysis.HorseDetails;
import drf.common.wrappers.entries.RaceDTOWrapper;
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

        HashMap<String, PredictionDetails> predicatMap = this.buildPredicatMap();

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
                            this.validateProgramNumberForHorse(horseAnalysisList, resultRaceDTOWrapper.getRunnerDTOs());
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
        predictor.setTotalAmountWagered(predictor.getBaseBet() * totalRaces);        
        predictor.setWagersPredicationMap(this.calculateProfit(predicatMap, predictor.getBaseBet() * totalRaces));
        return predictor;
    }

    private HashMap<String,PredictionDetails> calculateProfit(HashMap<String, PredictionDetails> predicatMap, double totalAmountWagered) {
       HashMap<String, PredictionDetails> updatedMap = new HashMap<>();
       for ( Entry<String, PredictionDetails> entry : predicatMap.entrySet() ) {
           PredictionDetails predictionDetails = entry.getValue();
           predictionDetails.setProfit(predictionDetails.getTotalPayout()/ totalAmountWagered );
           updatedMap.put(entry.getKey(), predictionDetails);
       }
       return updatedMap;
    }

    private void validateProgramNumberForHorse(List<HorseDetails> horseAnalysisList, List<RunnerDTOWrapper> runnerDTOs) {
        for ( HorseDetails horseDetails : horseAnalysisList ) {
            if ( horseDetails.getProgramNumber() == null ) {
                horseDetails.setProgramNumber(searchProgramNumber(runnerDTOs, horseDetails));
            }
        }
    }

    private String searchProgramNumber(List<RunnerDTOWrapper> runnerDTOs, HorseDetails horseDetails) {
        for ( RunnerDTOWrapper runnerDTOWrapper : runnerDTOs ) {
            if ( runnerDTOWrapper.getHorseName().equals(horseDetails.getHorseName()) ) {
                return runnerDTOWrapper.getProgramNumber();
            }
        }
        return null;
    }

    private HashMap<String, PredictionDetails> buildPredicatMap() {
        HashMap<String, PredictionDetails> predicatMap = new HashMap<String, PredictionDetails>();
        predicatMap.put("WIN", new PredictionDetails());
        predicatMap.put("PLACE", new PredictionDetails());
        predicatMap.put("SHOW", new PredictionDetails());
        predicatMap.put("EXACTA", new PredictionDetails());
        predicatMap.put("TRIFECTA", new PredictionDetails());
        return predicatMap;
    }

    private void predictForWinPlaceShow(HashMap<String, PredictionDetails> predicatMap, List<RunnerDTOWrapper> WinnersHorses, List<HorseDetails> horseAnalysisList) {
        if ( !horseAnalysisList.isEmpty() ) {
            int exactCount = 0;
            double totalExactaPayout = 0.0;
            int trifectaCount = 0;
            double totalTrifectaPayout = 0.0;
            if ( horseAnalysisList.get(0).getProgramNumber() != null && horseAnalysisList.get(0).getProgramNumber().trim().equals(WinnersHorses.get(0).getProgramNumber().trim()) ) {
                setPredictorDetails(predicatMap, WinnersHorses, "WIN");
                exactCount++;
                double winPayoff = WinnersHorses.get(0).getWinPayoff();
                totalExactaPayout += winPayoff;
                trifectaCount++;
                totalTrifectaPayout += winPayoff;
            }
            if ( horseAnalysisList.get(1).getProgramNumber() != null && horseAnalysisList.get(1).getProgramNumber().trim().equals(WinnersHorses.get(1).getProgramNumber().trim()) ) {
                setPredictorDetails(predicatMap, WinnersHorses, "PLACE");
                double placePayoff = WinnersHorses.get(0).getPlacePayoff();
                exactCount++;
                totalExactaPayout += placePayoff;
                trifectaCount++;
                totalTrifectaPayout += placePayoff;
            }
            if ( horseAnalysisList.get(2).getProgramNumber() != null && horseAnalysisList.get(2).getProgramNumber().trim().equals(WinnersHorses.get(2).getProgramNumber().trim()) ) {
                setPredictorDetails(predicatMap, WinnersHorses, "SHOW");
                double showoff = WinnersHorses.get(0).getShowPayoff();
                trifectaCount++;
                totalTrifectaPayout += showoff;
            }
            if ( exactCount == 2 ) {
                PredictionDetails predictionDetails = predicatMap.get("EXACTA");
                predictionDetails.setCount(predictionDetails.getCount() + 1);
                predictionDetails.setTotalPayout(predictionDetails.getTotalPayout() + totalExactaPayout);
                predicatMap.put("EXACTA", predictionDetails);
            }
            if ( trifectaCount == 3 ) {
                PredictionDetails predictionDetails = predicatMap.get("TRIFECTA");
                predictionDetails.setCount(predictionDetails.getCount() + 1);
                predictionDetails.setTotalPayout(predictionDetails.getTotalPayout() + totalTrifectaPayout);
                predicatMap.put("TRIFECTA", predictionDetails);
            }
        }
    }

    private void setPredictorDetails(HashMap<String, PredictionDetails> predicatMap, List<RunnerDTOWrapper> WinnersHorses, String wagerType) {
        PredictionDetails predictionDetails = predicatMap.get(wagerType);
        predictionDetails.setCount(predictionDetails.getCount() + 1);
        predictionDetails.setTotalPayout(predictionDetails.getTotalPayout() + WinnersHorses.get(0).getWinPayoff());

        predicatMap.put(wagerType, predictionDetails);
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
