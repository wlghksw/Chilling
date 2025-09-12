package com.example.KDT.repository;

import com.example.KDT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    boolean existsByPhone(String phone);
    
    boolean existsByNicknameAndIdNot(String nickname, Long id);
    
    boolean existsByPhoneAndIdNot(String phone, Long id);
    
    @Query("select u from User u where u.loginId = :loginId and u.password = :password")
    Optional<User> findByLoginIdAndPassword(@Param("loginId") String loginId,
                                           @Param("password") String password);
    
    @Query("""
       select u from User u
       where u.realName = :realName
         and u.phone = :phone
         and u.birthYear = :birthYear
       """)
    Optional<User> findForFindId(@Param("realName") String realName,
                                  @Param("phone") String phone,
                                  @Param("birthYear") Integer birthYear);
}
