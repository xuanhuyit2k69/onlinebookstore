package vn.edu.xyz.olms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.xyz.olms.dto.RegisterRequest;
import vn.edu.xyz.olms.dto.RegisterResponse;
import vn.edu.xyz.olms.entity.AppUser;
import vn.edu.xyz.olms.entity.Member;
import vn.edu.xyz.olms.exception.ApiException;
import vn.edu.xyz.olms.exception.DuplicateEmailException;
import vn.edu.xyz.olms.repository.AppUserRepository;
import vn.edu.xyz.olms.repository.MemberRepository;
import vn.edu.xyz.olms.util.PasswordValidator;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepo;
    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public RegisterResponse registerMember(RegisterRequest req) {
        Objects.requireNonNull(req, "register request must not be null");
        Objects.requireNonNull(req.getUsername(), "username must not be null");
        Objects.requireNonNull(req.getPassword(), "password must not be null");
        Objects.requireNonNull(req.getEmail(), "email must not be null");
        if (!PasswordValidator.isValid(req.getPassword())) {
            throw new ApiException(
                "Mật khẩu tối thiểu 8 ký tự, có ít nhất 1 chữ hoa và 1 chữ số",
                HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new ApiException("Username đã tồn tại", HttpStatus.CONFLICT);
        }
        if (memberRepo.existsByEmail(req.getEmail())) {
            throw new DuplicateEmailException("Email đã được sử dụng");
        }

        Member member = new Member();
        member.setMemberCode("MB" + System.currentTimeMillis());
        member.setFullName(req.getFullName());
        member.setEmail(req.getEmail());
        member.setPhone(req.getPhone());
        member.setMemberType(Member.MemberType.STUDENT);
        member.setExpiryDate(LocalDate.now().plusYears(1));
        member.setActive(true);
        member = memberRepo.save(member);

        AppUser user = new AppUser();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("READER");
        user.setActive(true);
        user.setMember(member);
        user = userRepo.save(user);

        try {
            notificationService.sendWelcomeEmail(member);
        } catch (Exception ex) {
            log.warn("Không gửi được email chào mừng: {}", ex.getMessage());
        }

        return new RegisterResponse(member.getId(), user.getUsername());
    }
}
