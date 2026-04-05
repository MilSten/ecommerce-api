package com.ecommerce.entity;

import com.ecommerce.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "media_files", indexes = {
        @Index(name = "idx_media_filename", columnList = "filename"),
        @Index(name = "idx_media_original_filename", columnList = "original_filename")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MediaFile extends BaseEntity {

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String mimeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;
}
