package com.example.Incident.controller;


import com.example.Incident.model.Schedule;
import com.example.Incident.model.ScheduleType;
import com.example.Incident.model.UserHoursDto;
import com.example.Incident.model.Userr;
import com.example.Incident.repo.ScheduleRepository;
import com.example.Incident.repo.UserrRepository;
import com.example.Incident.services.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class SchedulingController {
    @Autowired
    private SchedulingService schedulingService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserrRepository userrRepository;
    @Value("${sms.api.url}")
    private String smsApiUrl;
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Map<String, String>> generateSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam int days,
            @RequestParam boolean isThreeShiftScenario) {
        Map<String, String> response = new HashMap<>();
        try {
            schedulingService.generateSchedule(startDate, days, isThreeShiftScenario);
            response.put("status", "success");
            response.put("message", "Schedule generated successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Void> deleteAllSchedules() {
        schedulingService.deleteAllSchedules();

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/current-shift")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Map<String, String>> getCurrentShift() {
        LocalDate today = LocalDate.now();  // Get today's date
        String currentShift = schedulingService.getCurrentShiftForDate(today);  // Get current shift for today

        Map<String, String> response = new HashMap<>();
        response.put("shift", currentShift);

        return ResponseEntity.ok(response); // Return the current shift as JSON
    }

    @GetMapping("/next-shift")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Map<String, String>> getNextShift() {
        LocalDate today = LocalDate.now();  // Get today's date
        String nextShift = schedulingService.getNextShiftForDate(today);  // Get next shift for today

        Map<String, String> response = new HashMap<>();
        response.put("shift", nextShift);

        return ResponseEntity.ok(response); // Return the next shift as JSON
    }





    //    @GetMapping("/user-hours")
//    @PreAuthorize("hasAnyRole('SMS_ADMIN','SMS_USER')")
//    public ResponseEntity<List<UserHoursDto>> getUserWeeklyHours(
//            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        List<Userr> users = userrRepository.findAll();
//        Map<Userr, Long> userHoursMap = schedulingService.verifyWeeklyWorkingHourss(users, startDate, endDate);
//
//        // Convert the map to a list of UserHoursDto
//        List<UserHoursDto> response = userHoursMap.entrySet().stream()
//                .map(entry -> new UserHoursDto(entry.getKey().getName(), entry.getValue()))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(response);
//    }
@GetMapping("/user-hours")
@PreAuthorize("hasAnyRole('USER')")
public ResponseEntity<List<UserHoursDto>> getUserWeeklyHours(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<Userr> users = userrRepository.findAll();
    Map<Userr, Long> userHoursMap = schedulingService.verifyWeeklyWorkingHourss(users, startDate, endDate);


    List<UserHoursDto> response = userHoursMap.entrySet().stream()
            .map(entry -> new UserHoursDto(entry.getKey().getName(), 200L))  // Overwriting hours with 200
            .collect(Collectors.toList());

    return ResponseEntity.ok(response);
}



    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = schedulingService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }
    @GetMapping("/formatted")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Map<LocalDate, Map<String, List<String>>>> getFormattedSchedules() {
        Map<LocalDate, Map<String, List<String>>> formattedSchedules = schedulingService.getFormattedSchedules();
        return ResponseEntity.ok(formattedSchedules);
    }


@Scheduled(cron = "0 30 7 * * ?", zone = "Africa/Addis_Ababa")
 public void sendNotificationToAllAnalysts() {
       LocalDate todaytDay = LocalDate.now();

        List<Schedule> schedules = scheduleRepository.findByDate(todaytDay);
        if (schedules.isEmpty()) {
            System.out.println("No schedules found for tomorrow.");
            return;
        }


        String message = createScheduleMessage(schedules);


        List<String> phoneNumbers = Arrays.asList("0912357931","0942094473","0910084446");


        RestTemplate restTemplate = new RestTemplate();
        for (String phoneNumber : phoneNumbers) {
            String url = smsApiUrl + "&to=" + phoneNumber + "&text=" + message;
            try {
                restTemplate.getForObject(url, String.class);
                System.out.println("SMS sent to " + phoneNumber);
            } catch (Exception e) {
                // Handle error (e.g., log it)
                System.err.println("Error sending SMS to " + phoneNumber + ": " + e.getMessage());
            }
        }
    }

    private String createScheduleMessage(List<Schedule> schedules) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Assignments for Today (").append(LocalDate.now()).append("):\n");

        // SOC Shift assignments
        messageBuilder.append("SOC Shifts:\n");
        for (Schedule schedule : schedules) {
            if (schedule.getType() == ScheduleType.SOC_SHIFT && schedule.getUser() != null && schedule.getShift() != null) {
                messageBuilder.append(schedule.getUser().getName())
                        .append(" -> ").append(schedule.getShift().getName())
                        .append("\n");
            }
        }

        // Regular job assignments
        messageBuilder.append("\nOffice Hour:\n");
        for (Schedule schedule : schedules) {
            if (schedule.getType() == ScheduleType.REGULAR_JOB && schedule.getUser() != null) {
                messageBuilder.append(schedule.getUser().getName())
                        .append("\n");
            }
        }

        // Day-offs
        messageBuilder.append("\nDay Offs:\n");
        for (Schedule schedule : schedules) {
            if (schedule.getType() == ScheduleType.DAY_OFF && schedule.getUser() != null) {
                messageBuilder.append(schedule.getUser().getName())
                        .append("\n");
            }
        }

        return messageBuilder.toString();
    }





}



