package com.example.users.repository;

import com.example.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);



    @Query("select (count(u) > 0) from Users u where u.login_id = :loginId")
    boolean existsByLoginId(@Param("loginId") String loginId);

    @Query("select u from Users u where u.login_id = :loginId and u.password = :password")
    Optional<Users> findByLoginIdAndPassword(@Param("loginId") String loginId,
                                             @Param("password") String password);

    @Query("""
       select u from Users u
       where u.real_name = :realName
         and u.phone = :phone
         and u.birth_year = :birthYear
       """)

    Optional<Users> findForFindId(@Param("realName") String realName,
                                  @Param("phone") String phone,
                                  @Param("birthYear") Integer birthYear
                                  );

    @Query("select u from Users u where u.user_id = :userId")
    Optional<Users> findByUserId(@Param("userId") Long userId);


}