package vn.edu.xyz.olms.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.xyz.olms.entity.AppUser;
import vn.edu.xyz.olms.entity.Book;
import vn.edu.xyz.olms.entity.Copy;
import vn.edu.xyz.olms.entity.Member;
import vn.edu.xyz.olms.repository.AppUserRepository;
import vn.edu.xyz.olms.repository.BookRepository;
import vn.edu.xyz.olms.repository.CopyRepository;
import vn.edu.xyz.olms.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Profile({"docker", "local"})
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepo;
    private final CopyRepository copyRepo;
    private final MemberRepository memberRepo;
    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedBooks();
        seedUsers();
    }

    private void seedBooks() {
        if (bookRepo.count() > 0) return;

        Book b1 = new Book();
        b1.setIsbn("978-604-1-00001-1");
        b1.setTitle("Lập trình Java cơ bản");
        b1.setAuthor("Nguyễn Văn A");
        b1.setPublisher("NXB Bách Khoa");
        b1.setPubYear(2022);
        b1.setGenre("CNTT");

        Book b2 = new Book();
        b2.setIsbn("978-604-1-00002-2");
        b2.setTitle("Cấu trúc dữ liệu và giải thuật");
        b2.setAuthor("Trần Thị B");
        b2.setPublisher("NXB Giáo Dục");
        b2.setPubYear(2021);
        b2.setGenre("CNTT");

        Book b3 = new Book();
        b3.setIsbn("978-604-1-00003-3");
        b3.setTitle("Phân tích thiết kế hệ thống");
        b3.setAuthor("Lê Văn C");
        b3.setPublisher("NXB Bách Khoa");
        b3.setPubYear(2023);
        b3.setGenre("CNTT");

        Book b4 = new Book();
        b4.setIsbn("978-604-1-00004-4");
        b4.setTitle("Co so du lieu");
        b4.setAuthor("Pham Van D");
        b4.setPublisher("NXB DHQG");
        b4.setPubYear(2020);
        b4.setGenre("CNTT");

        Book b5 = new Book();
        b5.setIsbn("978-604-1-00005-5");
        b5.setTitle("Mang may tinh");
        b5.setAuthor("Hoang Thi E");
        b5.setPublisher("NXB GTVT");
        b5.setPubYear(2022);
        b5.setGenre("CNTT");
        List<Book> savedBooks = bookRepo.saveAll(List.of(b1, b2, b3, b4, b5));

        seedCopies(savedBooks.get(0), 2);
        seedCopies(savedBooks.get(1), 1);
        seedCopies(savedBooks.get(2), 3);
        seedCopies(savedBooks.get(3), 2);
        seedCopies(savedBooks.get(4), 1);
        log.info("[SEED] Da tao {} sach mau", bookRepo.count());
    }

    private void seedCopies(Book book, int quantity) {
        for (int i = 1; i <= quantity; i++) {
            Copy copy = new Copy();
            copy.setBook(book);
            copy.setBarcode(book.getIsbn() + "-C" + i);
            copy.setShelfLocation("Ke-A" + i);
            copy.setStatus(Copy.CopyStatus.AVAILABLE);
            copyRepo.save(copy);
        }
    }

    private void seedUsers() {
        if (userRepo.count() > 0) return;

        Member reader = new Member();
        reader.setMemberCode("MB-READER-001");
        reader.setFullName("Nguyen Van Doc Gia");
        reader.setEmail("reader@olms.edu.vn");
        reader.setPhone("0901234567");
        reader.setMemberType(Member.MemberType.STUDENT);
        reader.setExpiryDate(LocalDate.now().plusYears(1));
        reader.setActive(true);
        reader = memberRepo.save(reader);

        AppUser readerUser = new AppUser();
        readerUser.setUsername("reader");
        readerUser.setPasswordHash(passwordEncoder.encode("Reader123"));
        readerUser.setRole("READER");
        readerUser.setActive(true);
        readerUser.setMember(reader);
        userRepo.save(readerUser);

        Member librarianMember = new Member();
        librarianMember.setMemberCode("MB-LIB-001");
        librarianMember.setFullName("Tran Thi Thu Thu");
        librarianMember.setEmail("librarian@olms.edu.vn");
        librarianMember.setPhone("0907654321");
        librarianMember.setMemberType(Member.MemberType.TEACHER);
        librarianMember.setExpiryDate(LocalDate.now().plusYears(5));
        librarianMember.setActive(true);
        librarianMember = memberRepo.save(librarianMember);

        AppUser librarian = new AppUser();
        librarian.setUsername("librarian");
        librarian.setPasswordHash(passwordEncoder.encode("Librarian123"));
        librarian.setRole("LIBRARIAN");
        librarian.setActive(true);
        librarian.setMember(librarianMember);
        userRepo.save(librarian);

        log.info("========================================");
        log.info("  TAI KHOAN DEMO OLMS");
        log.info("  Doc gia : reader / Reader123");
        log.info("  Thu thu : librarian / Librarian123");
        log.info("========================================");
    }
}
