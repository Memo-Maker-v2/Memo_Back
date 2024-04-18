
package com.example.memo.repository;


import com.example.memo.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository// 첫번째 인자 : 어떤 Entity인지, 두번째 인자 : pk 어떤 타입인지
public interface MemberRepository extends JpaRepository<MemberEntity,String> {
    //Optional<MemberEntity> findByMemberEmail(String memberEmail);
    boolean existsByMemberEmail(String memberEmail);
    boolean existsByMemberName(String memberName);

    MemberEntity findByMemberEmail(String memberEmail);
}

