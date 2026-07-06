package vn.edu.xyz.olms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.xyz.olms.entity.Reservation;
import vn.edu.xyz.olms.repository.ReservationRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final NotificationService notificationService;

    public void notifyNextInQueue(UUID bookId) {
        reservationRepo.findFirstByDocument_IdAndStatusOrderByQueueOrderAsc(
                bookId, Reservation.ReservationStatus.WAITING)
            .ifPresent(reservation -> {
                reservation.setStatus(Reservation.ReservationStatus.NOTIFIED);
                reservationRepo.save(reservation);
                notificationService.sendReservationNotice(reservation);
            });
    }
}
