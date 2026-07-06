package vn.edu.xyz.olms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BookDTO {
    private UUID id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int pubYear;
    private String genre;
    private int availableCopies;
}
