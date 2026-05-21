package com.library.lms.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "shelves")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "shelf_code", unique = true, nullable = false, length = 20)
    private String shelfCode;

    @Column(name = "location_floor", length = 20)
    private String locationFloor;

    @Column(name = "location_section", length = 50)
    private String locationSection;

    @Column
    private Integer capacity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
