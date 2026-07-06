package vn.edu.xyz.olms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.xyz.olms.entity.LoanRecord;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LoanRecordRepository extends JpaRepository<LoanRecord, UUID> {

    long countByMember_IdAndStatusIn(UUID memberId, Collection<LoanRecord.LoanStatus> statuses);

    List<LoanRecord> findByStatusAndCreatedAtBefore(LoanRecord.LoanStatus status, Instant cutoff);
}
