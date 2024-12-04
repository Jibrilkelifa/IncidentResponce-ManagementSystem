package com.example.Incident.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Userr user;

    @ManyToOne
    private Shift shift;

    private LocalDate date;

    private int hours;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;  // New field to differentiate regular job and day-off

    public Schedule(Userr user, Shift shift, LocalDate date, ScheduleType type) {
        this.user = user;
        this.shift = shift;
        this.date = date;
        this.type = type;
        this.hours = (type == ScheduleType.REGULAR_JOB) ? 8 : (shift != null ? shift.getHours() : 0);
    }

    public Schedule(Userr user, Object o, LocalDate dayOff, ScheduleType scheduleType, int i) {
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
