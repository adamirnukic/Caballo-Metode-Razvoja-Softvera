package main.caballo.dao;

import main.caballo.model.Reservation;
import java.time.LocalDate;
import java.util.List;

public interface ReservationDao {
    Reservation create(Reservation r);
    boolean update(Reservation r);
    boolean delete(long id);
    List<Reservation> findByDate(LocalDate date);
}