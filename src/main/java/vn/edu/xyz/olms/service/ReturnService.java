package vn.edu.xyz.olms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.xyz.olms.dto.ReturnResult;
import vn.edu.xyz.olms.entity.Copy;
import vn.edu.xyz.olms.entity.FineInvoice;
import vn.edu.xyz.olms.entity.LoanRecord;
import vn.edu.xyz.olms.exception.ApiException;
import vn.edu.xyz.olms.repository.CopyRepository;
import vn.edu.xyz.olms.repository.FineInvoiceRepository;
import vn.edu.xyz.olms.repository.LoanRecordRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReturnService {

    private final LoanRecordRepository loanRepo;
    private final FineInvoiceRepository fineInvoiceRepo;
    private final CopyRepository copyRepo;
    private final ReservationService reservationService;
    private final LoanService loanService;

    public ReturnResult processReturn(UUID loanId, boolean damaged) {
        LoanRecord loan = loanService.getLoanOrThrow(loanId);

        if (loan.getStatus() != LoanRecord.LoanStatus.DANG_MUON
            && loan.getStatus() != LoanRecord.LoanStatus.QUA_HAN) {
            throw new ApiException("Phiếu mượn không ở trạng thái đang mượn", HttpStatus.BAD_REQUEST);
        }

        LocalDate returnDate = LocalDate.now();
        loan.setReturnDate(returnDate);

        long fineAmount = FineCalculator.calculate(loan);
        loan.setFineAmount(fineAmount);

        if (fineAmount > 0) {
            fineInvoiceRepo.save(FineInvoice.create(loan, BigDecimal.valueOf(fineAmount)));
        }

        loan.setStatus(LoanRecord.LoanStatus.DA_TRA);

        Copy copy = loan.getCopy();
        copy.setStatus(damaged ? Copy.CopyStatus.MAINTENANCE : Copy.CopyStatus.AVAILABLE);
        copyRepo.save(copy);
        loanRepo.save(loan);

        reservationService.notifyNextInQueue(copy.getBook().getId());

        String message = fineAmount > 0
            ? String.format("Trả sách thành công. Phí phạt: %,d đ", fineAmount)
            : "Trả sách thành công. Không có phí phạt.";

        return new ReturnResult(fineAmount, fineAmount > 0, message);
    }
}
