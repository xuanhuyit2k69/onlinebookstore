package vn.edu.xyz.olms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.xyz.olms.entity.LoanRecord;
import vn.edu.xyz.olms.entity.Member;
import vn.edu.xyz.olms.entity.Reservation;

@Slf4j
@Service
public class NotificationService {

    public void sendWelcomeEmail(Member member) {
        log.info("[NOTIFICATION] Welcome email sent to {} ({})", member.getFullName(), member.getEmail());
    }

    public void sendLoanPendingNotice(LoanRecord loan) {
        log.info("[NOTIFICATION] Loan pending notice for {} - book: {}",
            loan.getMember().getFullName(), loan.getCopy().getBook().getTitle());
    }

    public void sendLoanConfirmNotice(LoanRecord loan) {
        log.info("[NOTIFICATION] Loan confirmed for {} - due: {}",
            loan.getMember().getFullName(), loan.getDueDate());
    }

    public void sendReservationNotice(Reservation reservation) {
        log.info("[NOTIFICATION] Reservation turn for {} - book: {}",
            reservation.getMember().getFullName(), reservation.getDocument().getTitle());
    }
}
