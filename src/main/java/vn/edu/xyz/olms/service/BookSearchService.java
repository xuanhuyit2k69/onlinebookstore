package vn.edu.xyz.olms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.xyz.olms.dto.*;
import vn.edu.xyz.olms.entity.Book;
import vn.edu.xyz.olms.entity.Copy;
import vn.edu.xyz.olms.entity.FineInvoice;
import vn.edu.xyz.olms.exception.ApiException;
import vn.edu.xyz.olms.exception.NoCopyAvailableException;
import vn.edu.xyz.olms.repository.BookRepository;
import vn.edu.xyz.olms.repository.CopyRepository;
import vn.edu.xyz.olms.util.TextUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookSearchService {

    private final BookRepository bookRepo;
    private final CopyRepository copyRepo;

    public PageResponse<BookDTO> search(SearchCriteria criteria) {
        Specification<Book> spec = buildSpec(criteria);
        PageRequest pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<Book> page = bookRepo.findAll(spec, pageable);

        List<BookDTO> content = page.getContent().stream().map(this::toDTO).toList();
        String message = null;
        List<String> suggestions = null;
        if (content.isEmpty()) {
            message = "Không tìm thấy tài liệu phù hợp";
            suggestions = bookRepo.findDistinctGenres().stream().limit(5).toList();
        }

        return new PageResponse<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            message,
            suggestions
        );
    }

    public BookDetailDTO getById(UUID id) {
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new ApiException("Không tìm thấy tài liệu", HttpStatus.NOT_FOUND));
        int available = copyRepo.countByBook_IdAndStatus(book.getId(), Copy.CopyStatus.AVAILABLE);
        return new BookDetailDTO(
            book.getId(), book.getTitle(), book.getAuthor(), available,
            book.getIsbn(), book.getPublisher(), book.getPubYear(), book.getGenre()
        );
    }

    private Specification<Book> buildSpec(SearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (c.getKeyword() != null && !c.getKeyword().isBlank()) {
                String kw = TextUtils.removeAccents(c.getKeyword());
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("titleNormalized")), "%" + kw + "%"),
                    cb.like(cb.lower(root.get("authorNormalized")), "%" + kw + "%")
                ));
            }
            if (c.getGenre() != null && !c.getGenre().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("genre")), c.getGenre().toLowerCase()));
            }
            if (c.getAuthor() != null && !c.getAuthor().isBlank()) {
                String author = TextUtils.removeAccents(c.getAuthor());
                predicates.add(cb.like(cb.lower(root.get("authorNormalized")), "%" + author + "%"));
            }
            if (c.getYear() != null) {
                predicates.add(cb.equal(root.get("pubYear"), c.getYear()));
            }
            if (c.getIsbn() != null && !c.getIsbn().isBlank()) {
                predicates.add(cb.like(root.get("isbn"), "%" + c.getIsbn() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private BookDTO toDTO(Book book) {
        int available = copyRepo.countByBook_IdAndStatus(book.getId(), Copy.CopyStatus.AVAILABLE);
        return new BookDTO(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(),
            book.getPublisher(), book.getPubYear(), book.getGenre(), available);
    }
}
