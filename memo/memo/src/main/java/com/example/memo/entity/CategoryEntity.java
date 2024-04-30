package com.example.memo.entity;


import com.example.memo.dto.CategoryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    //양방향 매핑으로 videotable 정보 가져올 수 있음
    @OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VideoEntity> videos=new ArrayList<>();
    public CategoryEntity(CategoryDto dto) {
        this.categoryName = dto.getCategoryName();
        this.categorySequence = dto.getCategorySequence();
    }
}
