package vn.edu.xyz.olms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fine_invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FineInvoice {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private LoanRecord loan;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FineStatus status = FineStatus.UNPAID;

    private Instant paidAt;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public enum FineStatus {
        UNPAID, PAID, WAIVED
    }

    public static FineInvoice create(LoanRecord loan, BigDecimal amount) {
        FineInvoice invoice = new FineInvoice();
        invoice.setLoan(loan);
        invoice.setAmount(amount);
        invoice.setStatus(FineStatus.UNPAID);
        invoice.setCreatedAt(Instant.now());
        return invoice;
    }

    public void markPaid() {
        this.status = FineStatus.PAID;
        this.paidAt = Instant.now();
    }
}
