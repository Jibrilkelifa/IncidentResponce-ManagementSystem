package com.example.Incident.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
@Data
@NoArgsConstructor
@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private int hours;

    @Enumerated(EnumType.STRING)
    private ShiftType shiftType;

    // Enum for Shift Type to differentiate between two-shift and three-shift scenarios
    public enum ShiftType {
        TWO_SHIFT, THREE_SHIFT
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
