package vn.edu.xyz.olms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.xyz.olms.dto.BookDetailDTO;
import vn.edu.xyz.olms.dto.BookDTO;
import vn.edu.xyz.olms.dto.PageResponse;
import vn.edu.xyz.olms.dto.SearchCriteria;
import vn.edu.xyz.olms.service.BookSearchService;

import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping
    public ResponseEntity<PageResponse<BookDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String isbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setGenre(genre);
        criteria.setAuthor(author);
        criteria.setYear(year);
        criteria.setIsbn(isbn);
        criteria.setPage(page);
        criteria.setSize(size);
        return ResponseEntity.ok(bookSearchService.search(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookSearchService.getById(id));
    }
}
