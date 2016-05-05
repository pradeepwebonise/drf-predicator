package com.drf.predictor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.drf.predictor.service.PredictorService;

/**
 * Created on 27 April 2016
 * 
 * @author webonise
 *
 */
@Controller
public class PredictorController {

    private static final Logger LOG = LoggerFactory.getLogger(PredictorController.class);

    @Autowired
    private PredictorService predictorService;

    @RequestMapping(value = "/predictor-results", method = RequestMethod.GET)
    String predictorResults(Model model) {

        /*EntriesDetailsWrapper racePredictor = predictorService.getRacePredictor(new Date());
        try {
            LOG.debug("racePredictor: {}", new ObjectMapper().writeValueAsString(racePredictor));
        } catch (IOException ex) {
            LOG.error("Exception occured during fetching data", ex);
        }*/
        model.addAttribute("predictorResults", "predictor-results");

        return "predictor-results";
    }
}
