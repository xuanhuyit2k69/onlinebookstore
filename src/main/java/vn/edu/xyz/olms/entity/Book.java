package vn.edu.xyz.olms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import vn.edu.xyz.olms.util.TextUtils;

import java.util.UUID;

// Bảng vật lý vẫn tên "document" theo ERD gốc, class đặt tên Book theo class diagram thiết kế (Hình 4.3)
@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String publisher;

    private int pubYear;

    private String genre;

    private String titleNormalized;

    private String authorNormalized;

    @PrePersist
    @PreUpdate
    void normalizeFields() {
        titleNormalized = TextUtils.removeAccents(title);
        authorNormalized = TextUtils.removeAccents(author);
    }
}
