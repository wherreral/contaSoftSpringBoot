package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.AfpFactorNickname;

@Repository
public interface AfpFactorNicknameRepository extends CrudRepository<AfpFactorNickname, Long> {

    List<AfpFactorNickname> findByFamilyId(String familyId);

    AfpFactorNickname findByAfpFactorIdAndFamilyId(long afpFactorId, String familyId);

    // Lookup nicknames case-insensitive
    List<AfpFactorNickname> findByNicknameIgnoreCase(String nickname);
}
