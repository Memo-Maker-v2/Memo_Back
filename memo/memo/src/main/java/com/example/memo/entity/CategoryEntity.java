package com.example.memo.entity;


import com.example.memo.dto.CategoryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_table")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long categoryId;

    @Column
    private String categoryName;

    @Column
    private long categorySequence;

    public CategoryEntity(CategoryDto dto) {
        this.categoryId = dto.getCategoryId();
        this.categoryName = dto.getCategoryName();
        this.categorySequence = dto.getCategorySequence();
    }
}
