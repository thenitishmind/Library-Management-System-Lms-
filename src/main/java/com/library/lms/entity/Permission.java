package com.library.lms.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(length = 50)
    private String resource;

    @Column(length = 20)
    private String action;
}
