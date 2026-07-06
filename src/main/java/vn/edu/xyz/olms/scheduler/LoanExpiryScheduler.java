package vn.edu.xyz.olms.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.xyz.olms.entity.LoanRecord;
import vn.edu.xyz.olms.repository.LoanRecordRepository;
import vn.edu.xyz.olms.service.LoanService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanExpiryScheduler {

    private final LoanRecordRepository loanRepo;
    private final LoanService loanService;

    @Value("${olms.loan-expiry-hours:48}")
    private int loanExpiryHours;

    @Scheduled(fixedRate = 600000)
    @Transactional
    public void cancelExpiredPendingLoans() {
        Instant cutoff = Instant.now().minus(loanExpiryHours, ChronoUnit.HOURS);
        List<LoanRecord> expired = loanRepo.findByStatusAndCreatedAtBefore(
            LoanRecord.LoanStatus.CHO_XAC_NHAN, cutoff);

        for (LoanRecord loan : expired) {
            loanService.cancelExpiredLoan(loan);
            log.info("[SCHEDULER] Auto-cancelled loan {} after {}h", loan.getId(), loanExpiryHours);
        }
    }
}
