package vn.edu.xyz.olms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "copy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Copy {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Book book;

    @Column(nullable = false, unique = true)
    private String barcode;

    private String shelfLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status = CopyStatus.AVAILABLE;

    public enum CopyStatus {
        AVAILABLE, BORROWED, RESERVED, MAINTENANCE, LOST
    }
}
