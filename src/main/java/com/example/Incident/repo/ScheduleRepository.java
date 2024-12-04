package com.example.Incident.repo;


import com.example.Incident.model.Schedule;
import com.example.Incident.model.ScheduleType;
import com.example.Incident.model.Shift;
import com.example.Incident.model.Userr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDate(LocalDate date);
    boolean existsByUserAndDate(Userr user, LocalDate date);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.user = :user AND s.date BETWEEN :startDate AND :endDate AND s.type = 'DAY_OFF'")
    Long countDayOffHoursByUser(@Param("user") Userr user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT DISTINCT s.user FROM Schedule s WHERE s.date >= :startDate AND s.date <= :endDate AND s.type = :shiftType")
    List<Userr> findUsersAssignedOnSundays(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("shiftType") ScheduleType shiftType);

    long countByUserAndShift(Userr user, Shift shift);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.user = :user AND s.type = 'REGULAR_JOB' AND s.date BETWEEN :startDate AND :endDate")
    Long countRegularJobDaysByUser(@Param("user") Userr user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.user = :user AND s.type = 'REGULAR_JOB' AND s.date BETWEEN :startOfWeek AND :endOfWeek")
    Long countRegularJobsByUserAndWeek(@Param("user") Userr user, @Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);

    @Query("SELECT DISTINCT s.user FROM Schedule s WHERE s.date = :date")
    List<Userr> findUsersScheduledForDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(s.shift.hours) FROM Schedule s WHERE s.user = :user AND s.date BETWEEN :startDate AND :endDate AND s.type = 'SOC_SHIFT'")
    Long countSocHoursByUser(@Param("user") Userr user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(s.shift.hours) FROM Schedule s WHERE s.user = :user AND s.date BETWEEN :startDate AND :endDate AND s.type = 'REGULAR_JOB'")
    Long countRegularHoursByUser(@Param("user") Userr user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT s.date FROM Schedule s WHERE s.date BETWEEN :startDate AND :endDate")
    List<LocalDate> findDistinctDatesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.user = :user AND s.type = 'SOC_SHIFT' AND s.date BETWEEN :startOfWeek AND :endOfWeek")
    Long countSOCShiftsByUserAndWeek(@Param("user") Userr user, @Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);

    boolean existsByUserAndDateAndShift(@Param("user") Userr user, @Param("date") LocalDate date, @Param("shift") Shift shift);

    boolean existsByUserAndDateAndType(Userr user, LocalDate date, ScheduleType scheduleType);

    @Query("SELECT COALESCE(SUM(s.hours), 0) FROM Schedule s WHERE s.user = :user AND s.date BETWEEN :startDate AND :endDate")
    Long sumHoursByUser(@Param("user") Userr user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsByUserAndShiftAndDateBetween(Userr user, Shift shift, LocalDate startOfWeek, LocalDate endOfWeek);

    @Query("SELECT COALESCE(SUM(s.shift.hours), 0) FROM Schedule s WHERE s.user = :user AND s.date BETWEEN :startOfWeek AND :endOfWeek")
    Long getTotalShiftHoursForUser(@Param("user") Userr user, @Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);

    @Query("SELECT COALESCE(SUM(s.hours), 0) FROM Schedule s WHERE s.user = :user AND s.date BETWEEN :startOfWeek AND :endOfWeek AND s.type = 'REGULAR_JOB'")
    Long getTotalRegularJobHoursForUser(@Param("user") Userr user, @Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);
//    @Query("SELECT s.user FROM Schedule s WHERE s.date BETWEEN :startDate AND :endDate AND s.type = :shiftType GROUP BY s.user")
//    List<Userr> findUsersAssignedOnSundays(@Param("startDate") LocalDate startDate,
//                                           @Param("endDate") LocalDate endDate,
//
//                                           @Query("SELECT s.user FROM Schedule s WHERE s.date BETWEEN :startDate AND :endDate AND s.type = :shiftType GROUP BY s.user")
@Query("SELECT s.user FROM Schedule s WHERE s.date BETWEEN :startDate AND :endDate AND s.type = :shiftType GROUP BY s.user")
List<Userr> findUsersAssignedOnLastThreeSundays(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("shiftType") ScheduleType shiftType);

    @Query("SELECT s.user FROM Schedule s WHERE s.date IN (:lastSundays) AND s.type = :shiftType")
    List<Userr> findUsersAssignedOnLastSundays(@Param("lastSundays") List<LocalDate> lastSundays, @Param("shiftType") ScheduleType shiftType);


//    @Query("SELECT s.user FROM Schedule s WHERE s.date = :lastSunday AND s.type = :shiftType")
//    List<Userr> findUsersAssignedOnLastSunday(@Param("lastSunday") LocalDate lastSunday, @Param("shiftType") ScheduleType shiftType);

}
