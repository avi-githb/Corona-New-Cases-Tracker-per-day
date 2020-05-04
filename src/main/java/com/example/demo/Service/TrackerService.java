package com.example.demo.Service;

import com.example.demo.Model.LocationStats;
import jdk.jfr.DataAmount;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.validator.constraints.URL;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Max;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class TrackerService {

    private static String covid19_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    List<LocationStats> allStat = new ArrayList<>();

    public List<LocationStats> getAllStat() {
        return allStat;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")

    private void totalCovidCases() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest build = HttpRequest.newBuilder().uri(URI.create(covid19_URL)).build();

        HttpResponse<String> send = client.send(build, HttpResponse.BodyHandlers.ofString());

        StringReader stringReader = new StringReader(send.body());

        List<LocationStats> newStat = new ArrayList<>();

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);
        for (CSVRecord record : records) {

            LocationStats locationStats = new LocationStats();
            locationStats.setState(record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));
            int today = Integer.parseInt(record.get(record.size() - 1));
            int yesterday = Integer.parseInt(record.get(record.size() - 2));
            locationStats.setLastCount(today - yesterday);
            newStat.add(locationStats);

        }
        this.allStat = newStat;

    }


    public LocationStats dataForCountry() {

        int match = 0;
        for (int i = 0; i < allStat.size(); i++) {
            if (allStat.get(i).getCountry().equals("India")) {
                match = i;
                break;
            }
        }
        return allStat.get(match);
    }


    public LocationStats searchByCountry(String country) {

        int match = 0;
        for (int i = 0; i < allStat.size(); i++) {
            if (allStat.get(i).getCountry().equals(country)) {
                match = i;
                break;
            }

        }
        return allStat.get(match);
    }

}


