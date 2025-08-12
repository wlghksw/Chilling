package com.example.KDT.repository;

import com.example.KDT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByLoginId(String loginId);
    
    Optional<User> findByNickname(String nickname);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByIsAdmin(Boolean isAdmin);
    
    boolean existsByLoginId(String loginId);
    
    boolean existsByNickname(String nickname);
    
    boolean existsByEmail(String email);
}
