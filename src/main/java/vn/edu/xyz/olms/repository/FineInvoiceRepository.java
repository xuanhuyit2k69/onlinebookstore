package vn.edu.xyz.olms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.xyz.olms.entity.FineInvoice;

import java.util.UUID;

public interface FineInvoiceRepository extends JpaRepository<FineInvoice, UUID> {

    boolean existsByLoan_Member_IdAndStatus(UUID memberId, FineInvoice.FineStatus status);
}
