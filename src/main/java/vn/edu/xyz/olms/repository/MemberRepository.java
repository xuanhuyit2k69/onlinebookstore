package vn.edu.xyz.olms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.xyz.olms.entity.Member;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
