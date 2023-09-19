package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DataController {

    private List<Map<String, String>> testResultDataList = new ArrayList<>();

    @PostMapping("/processData")
    public String processData(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("Data");

            String failPercent = (String) data.get("Fail percent");
            String passPercent = (String) data.get("Pass percent");
            String applicationName = (String) data.get("Application name");
            String tag = (String) data.get("Tag");
            String env = (String) data.get("ENV");
            String featureName = (String) data.get("Feature name");
            String date = (String) data.get("Date");
            String time = (String) data.get("Time");

            Map<String, String> testResult = new HashMap<>();
            testResult.put("Fail percent", failPercent);
            testResult.put("Pass percent", passPercent);
            testResult.put("Application name", applicationName);
            testResult.put("Tag", tag);
            testResult.put("ENV", env);
            testResult.put("Feature name", featureName);
            testResult.put("Date", date);
            testResult.put("Time", time);

            testResultDataList.add(testResult);

            return "Data processed successfully";
        } catch (Exception e) {
            return "Error processing data: " + e.getMessage();
        }
    }

    // This method will return the list of test results
    @GetMapping("/getTestResults")
    public List<Map<String, String>> getTestResults() {
        return testResultDataList;
    }
}
