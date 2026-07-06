package vn.edu.xyz.olms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.xyz.olms.entity.Copy;

import java.util.Optional;
import java.util.UUID;

public interface CopyRepository extends JpaRepository<Copy, UUID> {
    Optional<Copy> findFirstByBook_IdAndStatus(UUID bookId, Copy.CopyStatus status);
    int countByBook_IdAndStatus(UUID bookId, Copy.CopyStatus status);
}
