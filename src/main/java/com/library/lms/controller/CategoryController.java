package com.library.lms.controller;

import com.library.lms.entity.Category;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    @Operation(summary = "Get all root categories")
    public ResponseEntity<List<Category>> getRootCategories() {
        return ResponseEntity.ok(categoryRepository.findByParentIsNull());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id)));
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Get subcategories")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Long id) {
        return ResponseEntity.ok(categoryRepository.findByParentId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    @Operation(summary = "Create category")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryRepository.save(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    @Operation(summary = "Update category")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category updated) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        category.setName(updated.getName());
        category.setDescription(updated.getDescription());
        category.setSlug(updated.getSlug());
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
