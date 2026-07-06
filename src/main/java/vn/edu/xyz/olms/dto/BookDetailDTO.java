package vn.edu.xyz.olms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BookDetailDTO {
    private UUID id;
    private String title;
    private String author;
    private int availableCopies;
    private String isbn;
    private String publisher;
    private int pubYear;
    private String genre;
}
