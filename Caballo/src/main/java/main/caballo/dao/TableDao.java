package main.caballo.dao;

import main.caballo.model.DiningTable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TableDao {
    List<DiningTable> findAll();

    List<DiningTable> findAvailableAt(LocalDate date, LocalTime time, Duration blockDuration);

    List<DiningTable> findAvailableAtExcludingReservation(LocalDate date, LocalTime time, Duration blockDuration, long excludeReservationId);
}
