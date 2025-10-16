package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.dto.WeatherDto;
import com.ThreeZem.three_zem_back.data.enums.WeatherStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExternalApiService {

    Dotenv dotenv;
    String weatherApiKey;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    @PostConstruct
    public void init(){
        dotenv = Dotenv.configure()
                //.directory("")
                .filename(".env")
                .load();
        weatherApiKey = dotenv.get("WEATHER_API_KEY");
    }

    public ResponseEntity<WeatherDto> getWeather(int regionCode) {
        try {
            int beforeHour = 6;
            LocalDateTime now = LocalDateTime.now();
            String formattedBefore = now.minusHours(beforeHour).format(formatter);
            String formattedNow = now.format(formatter);
            String dateStr = formattedNow.subSequence(0, 8).toString();

            URI uri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("apihub.kma.go.kr")
                    .path("/api/typ01/url/kma_sfctm3.php")
                    .queryParam("tm1", formattedBefore)
                    .queryParam("tm2", formattedNow)
                    .queryParam("stn", regionCode)
                    .queryParam("help", 0)
                    .queryParam("authKey", weatherApiKey)
                    .build().toUri();

            URL url = uri.toURL();

            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String output;

            Map<String, Integer> map = new HashMap<>();
            List<String> words = new ArrayList<>();

            for(int i = 0; (output = br.readLine()) != null; i++) {
                if (i == beforeHour + 3) {
                    words = new ArrayList<>(Arrays.stream(output.split(" ")).toList());
                    words.removeIf(word -> word.contains(" ") || word.isEmpty());
                }
            }

            // 4. WS : 풍속(m/s)
            float windSpeed = Float.parseFloat(words.get(3));
            // 12. TA : 기온(C)
            float temperature = Float.parseFloat(words.get(11));
            // 14. HM : 상대습도(%)
            float humidity = Float.parseFloat(words.get(13));
            // 17. RN_DAY : 일강수량 (mm)
            float rain = Float.parseFloat(words.get(16));
            // 21. SD_DAY : 일 신적설 (cm)
            float snow = Float.parseFloat(words.get(20));
            // 26. CA_TOT : 전운량
            float cloud = Float.parseFloat(words.get(25));

            // 맑음, 비, 흐림, 눈
            WeatherStatus weatherStatus = WeatherStatus.SUNNY;
            if (rain > 0) {
                weatherStatus = WeatherStatus.RAINY;
            }
            else if (snow > 0) {
                weatherStatus = WeatherStatus.SNOWY;
            }
            else if (cloud > 5) {
                weatherStatus = WeatherStatus.CLOUDY;
            }

            System.out.printf("[INFO] 현재 풍속: %f(m/s), 기온 %f(C), 습도 %f(%%), 현재 날씨 %s", windSpeed, temperature, humidity, weatherStatus);

            WeatherDto weatherDto = new WeatherDto();
            weatherDto.setNowTemperature(temperature);
            weatherDto.setHumidity(humidity);
            weatherDto.setWindSpeed(windSpeed);
            weatherDto.setWeatherStatus(weatherStatus.getValue());

            return ResponseEntity.status(HttpStatus.OK).body(weatherDto);
        }
        catch (Exception e) {
            log.error("[ERROR] 날씨 불러오기 에러 발생 {}", e.getMessage());
            return null;
        }
    }
}