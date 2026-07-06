package vn.edu.xyz.olms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.xyz.olms.dto.CreateLoanRequest;
import vn.edu.xyz.olms.dto.LoanDTO;
import vn.edu.xyz.olms.entity.Copy;
import vn.edu.xyz.olms.entity.FineInvoice;
import vn.edu.xyz.olms.entity.LoanRecord;
import vn.edu.xyz.olms.entity.Member;
import vn.edu.xyz.olms.exception.LoanLimitExceededException;
import vn.edu.xyz.olms.exception.NoCopyAvailableException;
import vn.edu.xyz.olms.exception.UnpaidFineException;
import vn.edu.xyz.olms.repository.CopyRepository;
import vn.edu.xyz.olms.repository.FineInvoiceRepository;
import vn.edu.xyz.olms.repository.LoanRecordRepository;
import vn.edu.xyz.olms.repository.MemberRepository;
import vn.edu.xyz.olms.security.UserPrincipal;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanService {

    private final LoanRecordRepository loanRepo;
    private final MemberRepository memberRepo;
    private final CopyRepository copyRepo;
    private final FineInvoiceRepository fineInvoiceRepo;
    private final NotificationService notificationService;

    @Value("${olms.max-active-loans:5}")
    private int maxActiveLoans;

    public List<LoanDTO> findAll() {
        return loanRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public LoanDTO createLoan(CreateLoanRequest req) {
        Member member = getAuthenticatedMember();

        if (fineInvoiceRepo.existsByLoan_Member_IdAndStatus(member.getId(), FineInvoice.FineStatus.UNPAID)) {
            throw new UnpaidFineException();
        }

        long activeLoanCount = loanRepo.countByMember_IdAndStatusIn(
            member.getId(),
            List.of(
                LoanRecord.LoanStatus.CHO_XAC_NHAN,
                LoanRecord.LoanStatus.DANG_MUON,
                LoanRecord.LoanStatus.QUA_HAN));
        if (activeLoanCount >= maxActiveLoans) {
            throw new LoanLimitExceededException(
                "Bạn đã đạt hạn mức tối đa 5 cuốn sách đang mượn.");
        }

        Copy copy = copyRepo.findFirstByBook_IdAndStatus(req.getBookId(), Copy.CopyStatus.AVAILABLE)
            .orElseThrow(() -> new NoCopyAvailableException("Sách đã hết bản sao khả dụng"));

        LocalDate loanDate = LocalDate.now();
        int loanDays = member.getMemberType() == Member.MemberType.TEACHER ? 30 : 14;
        LocalDate dueDate = loanDate.plusDays(loanDays);

        copy.setStatus(Copy.CopyStatus.RESERVED);
        copyRepo.save(copy);

        LoanRecord loan = new LoanRecord();
        loan.setMember(member);
        loan.setCopy(copy);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setStatus(LoanRecord.LoanStatus.CHO_XAC_NHAN);
        loan.setFineAmount(0L);
        loan = loanRepo.save(loan);

        try {
            notificationService.sendLoanPendingNotice(loan);
        } catch (Exception ignored) {
            // email failure must not rollback loan
        }

        return toDTO(loan);
    }

    public LoanDTO confirmLoan(UUID loanId) {
        LoanRecord loan = getLoanOrThrow(loanId);
        if (loan.getStatus() != LoanRecord.LoanStatus.CHO_XAC_NHAN) {
            throw new vn.edu.xyz.olms.exception.ApiException(
                "Phiếu mượn không ở trạng thái chờ xác nhận", HttpStatus.BAD_REQUEST);
        }

        Copy copy = loan.getCopy();
        copy.setStatus(Copy.CopyStatus.BORROWED);
        copyRepo.save(copy);

        loan.setStatus(LoanRecord.LoanStatus.DANG_MUON);
        loan = loanRepo.save(loan);

        notificationService.sendLoanConfirmNotice(loan);
        return toDTO(loan);
    }

    public LoanDTO cancelExpiredLoan(LoanRecord loan) {
        loan.setStatus(LoanRecord.LoanStatus.HUY);
        Copy copy = loan.getCopy();
        copy.setStatus(Copy.CopyStatus.AVAILABLE);
        copyRepo.save(copy);
        return toDTO(loanRepo.save(loan));
    }

    private Member getAuthenticatedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new vn.edu.xyz.olms.exception.ApiException("Yêu cầu đăng nhập", HttpStatus.UNAUTHORIZED);
        }
        if (principal.memberId() == null) {
            throw new vn.edu.xyz.olms.exception.ApiException("Tài khoản chưa liên kết thành viên", HttpStatus.FORBIDDEN);
        }
        return memberRepo.findById(principal.memberId())
            .orElseThrow(() -> new vn.edu.xyz.olms.exception.MemberNotFoundException("Không tìm thấy thành viên"));
    }

    LoanRecord getLoanOrThrow(UUID id) {
        return loanRepo.findById(id)
            .orElseThrow(() -> new vn.edu.xyz.olms.exception.ApiException(
                "Không tìm thấy phiếu mượn", HttpStatus.NOT_FOUND));
    }

    private LoanDTO toDTO(LoanRecord loan) {
        return new LoanDTO(
            loan.getId(),
            loan.getMember().getFullName(),
            loan.getCopy().getBook().getTitle(),
            loan.getLoanDate(),
            loan.getDueDate(),
            loan.getReturnDate(),
            loan.getStatus().name(),
            loan.getFineAmount()
        );
    }
}
