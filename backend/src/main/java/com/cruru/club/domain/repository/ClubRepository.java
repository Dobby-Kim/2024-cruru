package com.cruru.club.domain.repository;

import com.cruru.club.domain.Club;
import com.cruru.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByMember(Member member);

    @EntityGraph(attributePaths = "member")
    @Query("SELECT c FROM Club c WHERE c.id = :id")
    Optional<Club> findByIdFetchingMember(long id);
}
