package vn.edu.xyz.olms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCriteria {
    private String keyword;
    private String genre;
    private String author;
    private Integer year;
    private String isbn;
    private int page = 0;
    private int size = 10;
}
