package com.example.memo.service.implement;

import com.example.memo.entity.SavedVideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedVideoRepository extends JpaRepository<SavedVideoEntity, Long> {
    List<SavedVideoEntity> findByMember_MemberEmail(String memberEmail);
}
