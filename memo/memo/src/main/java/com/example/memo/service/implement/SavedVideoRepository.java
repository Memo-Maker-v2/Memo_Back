package com.example.memo.service.implement;

import com.example.memo.dto.SavedVideoDto;
import com.example.memo.entity.SavedVideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedVideoRepository extends JpaRepository<SavedVideoEntity, Long> {
    List<SavedVideoEntity> findByMember_MemberEmail(String memberEmail);
    // 특정 회원의 특정 비디오 삭제
    void deleteByVideo_VideoIdAndMember_MemberEmail(long videoId, String memberEmail);
    SavedVideoEntity findByVideo_VideoIdAndMember_MemberEmail(long videoId, String memberEmail);

}

