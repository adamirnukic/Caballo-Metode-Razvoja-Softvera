package main.caballo.dao;

import main.caballo.model.Shift;
import main.caballo.model.ShiftType;

import java.time.LocalDate;
import java.util.Optional;

public interface ShiftDao {
    Optional<Shift> findByUserAndDate(long userId, LocalDate date);
    Optional<Shift> findLastBefore(long userId, LocalDate date);
    Shift upsert(long userId, LocalDate date, ShiftType type, String note);
}
