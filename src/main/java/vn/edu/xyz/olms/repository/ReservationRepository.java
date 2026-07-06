package vn.edu.xyz.olms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.xyz.olms.entity.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<Reservation> findFirstByDocument_IdAndStatusOrderByQueueOrderAsc(
        UUID documentId, Reservation.ReservationStatus status);

    List<Reservation> findByDocument_IdAndStatusOrderByQueueOrderAsc(
        UUID documentId, Reservation.ReservationStatus status);
}
