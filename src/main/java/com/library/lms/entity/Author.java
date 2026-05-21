package com.library.lms.entity;

import javax.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "authors", indexes = {
    @Index(columnList = "first_name"),
    @Index(columnList = "last_name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 60)
    private String nationality;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;
}
