package com.example.demo.Controller;

import com.example.demo.Model.Country;
import com.example.demo.Model.LocationStats;
import com.example.demo.Service.TrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Controller
@EnableScheduling
public class HomeController {

    @Autowired
    TrackerService trackerService;

    @GetMapping("/")
    private String lastCount(Model model){

        List<LocationStats> totalSum = trackerService.getAllStat();
        int sum = totalSum.stream().mapToInt(stat -> stat.getLastCount()).sum();
        model.addAttribute("totalReportedCases",sum);
        model.addAttribute("locationStats",trackerService.getAllStat());
        model.addAttribute("Country",trackerService.dataForCountry());
        Country country = new Country();
        model.addAttribute("country",country);
        return "home";
    }


  @PostMapping("/country")
    private String searchWithCountry(@ModelAttribute Country country, Model model){
        String userInput = country.getCountry();
        LocationStats byCountry = trackerService.searchByCountry(userInput);
        model.addAttribute("byCountry",byCountry);
        return "country";
    }
}
