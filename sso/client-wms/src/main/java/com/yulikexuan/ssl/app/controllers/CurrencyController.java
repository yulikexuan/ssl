//: com.yulikexuan.ssl.app.controllers.CurrencyController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.domain.services.ICurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
@Controller
public class CurrencyController {

    private final ICurrencyService currencyService;

    @Autowired
    public CurrencyController(ICurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/currencyrateinfo")
    public ModelAndView getAuthentication() {

        String ratesInfo = this.currencyService.getExchangeRateInfomation();

        return new ModelAndView("currencyRatesPage",
                "ratesInfo", ratesInfo);
    }

}///:~