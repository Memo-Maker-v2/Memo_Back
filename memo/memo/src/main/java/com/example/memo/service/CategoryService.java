package com.example.memo.service;

import com.example.memo.dto.CategoryDto;
import com.example.memo.entity.CategoryEntity;
import com.example.memo.entity.MemberEntity;
import com.example.memo.repository.CategoryRepository;
import com.example.memo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoryService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    @Autowired
    public CategoryService(MemberRepository memberRepository, CategoryRepository categoryRepository)
    {
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
    }
    //category 추가
    @Transactional
    public void addCategory(CategoryDto categoryDto) throws Exception {
        CategoryEntity categoryEntity = convertDtoToEntity(categoryDto);
        categoryRepository.save(categoryEntity);
    }

    // 카테고리 이름 수정 메서드
    @Transactional
    public void updateCategoryName(String memberEmail, String oldCategoryName, String newCategoryName) {
        CategoryEntity categoryEntity = categoryRepository.findByMemberEmailAndCategoryName(memberEmail, oldCategoryName);
        if (categoryEntity == null) {
            throw new IllegalArgumentException("Category not found with name: " + oldCategoryName + " and member email: " + memberEmail);
        }
        categoryEntity.setCategoryName(newCategoryName);
        categoryRepository.save(categoryEntity);
    }
    //category 삭제
    @Transactional
    public boolean deleteCategory(long categoryId) {
        if (categoryRepository.existsById(categoryId)) {
            categoryRepository.deleteById(categoryId);
            return true;
        } else {
            return false;
        }
    }
    private CategoryEntity convertDtoToEntity(CategoryDto categoryDto) throws Exception {
        String memberEmail = categoryDto.getMemberEmail();
        if (memberEmail == null) {
            throw new IllegalArgumentException("Member email is missing in the request.");
        }

        MemberEntity member = memberRepository.findByMemberEmail(memberEmail);  // 멤버 찾기
        if (member == null) {
            /*throw new UserNotFoundException("User not found: " + memberEmail);  // 멤버가 없을 때 예외 처리*/
            throw new Exception();
        }

        // DTO에서 엔티티를 생성
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategoryName(categoryDto.getCategoryName()); // 카테고리 이름 설정
        categoryEntity.setMemberEmail(categoryDto.getMemberEmail());   // 멤버 이메일 설정
        return categoryEntity;
    }

}
