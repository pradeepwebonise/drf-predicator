package com.drf.predictor.controller;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.drf.predictor.models.Predictor;
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
        Date date = new Date( new Date().getTime() - 86400000 );
        Predictor predictor = predictorService.getRacePredictor(date);
        try {
            LOG.debug("racePredictor: {}", new ObjectMapper().writeValueAsString(predictor));
        } catch (IOException ex) {
            LOG.error("Exception occured during fetching data", ex);
        }
        model.addAttribute("predictor", predictor);

        return "predictor-results";
    }
}
