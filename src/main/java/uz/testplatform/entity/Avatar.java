package uz.testplatform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "avatars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avatar {

    @Id
    private String id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] data;
}