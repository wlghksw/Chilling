package com.example.users.service;

import com.example.users.dto.UsersDTO;
import com.example.users.entity.Users;
import com.example.users.entity.Users.Gender;
import com.example.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public void signup(UsersDTO dto) {
        String real_name  = dto.getReal_name()  != null ? dto.getReal_name().trim()  : null;
        String phone     = dto.getPhone()     != null ? dto.getPhone().trim()     : null;

        if (real_name == null || !real_name.matches("^[가-힣]{3,4}$")) {
            throw new RuntimeException("실명은 한글 3~4자만 가능합니다.");
        }

        if (phone == null || !phone.matches("^010\\d{8}$")) {
            throw new RuntimeException("전화번호는 010 포함 11자리 숫자만 가능합니다.");
        }

        if (dto.getLogin_id() != null && dto.getLogin_id().equals(dto.getPassword())) {
            throw new RuntimeException("아이디와 비밀번호는 서로 다르게 입력해야 합니다.");
        }

        if (usersRepository.existsByNickname(dto.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        if (usersRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("이미 등록된 전화번호입니다.");
        }

        Integer birthInt = null;
        if (dto.getBirth_year() != null && !dto.getBirth_year().isEmpty()) {
            birthInt = Integer.parseInt(dto.getBirth_year().replace("-", ""));
        }


        Users user = new Users();

        user.setLogin_id(dto.getLogin_id());
        user.setPassword(dto.getPassword());
        user.setReal_name(dto.getReal_name());
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setBirth_year(birthInt);
        user.setProfile_image(dto.getProfile_image());
        user.setIs_active(true);
        user.setIs_admin(false);
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());


        if (dto.getGender() != null) {
            try {
                user.setGender(Gender.valueOf(dto.getGender().toLowerCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("성별은 male 또는 female 중 하나여야 합니다.");
            }
        }

        usersRepository.save(user);
    }

    public Users login(String loginId, String password) {
        return usersRepository.findByLoginIdAndPassword(loginId, password)
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    public String findLoginId(String real_name, String phone, Integer birth_year) {
        Users user = usersRepository.findForFindId(real_name.trim(), phone.trim(), birth_year)
                .orElse(null);
        if (user != null) {
            return user.getLogin_id();
        }
        return null;
    }
}
