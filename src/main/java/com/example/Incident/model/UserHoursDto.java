package com.example.Incident.model;

public class UserHoursDto {
    private String userName;
    private Long totalHours;

    public UserHoursDto(String userName, Long totalHours) {
        this.userName = userName;
        this.totalHours = totalHours;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Long totalHours) {
        this.totalHours = totalHours;
    }
}

