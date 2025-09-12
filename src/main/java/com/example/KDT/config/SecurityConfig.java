package com.example.KDT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.KDT.entity.User;
import com.example.KDT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.ArrayList;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                System.out.println("=== 로그인 시도: " + username + " ===");
                
                User user = userRepository.findByLoginId(username)
                        .orElseThrow(() -> {
                            System.out.println("사용자를 찾을 수 없습니다: " + username);
                            return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                        });
                
                System.out.println("사용자 찾음: " + user.getLoginId() + ", 비밀번호: " + user.getPassword());
                
                if (!user.getIsActive()) {
                    System.out.println("비활성화된 사용자: " + username);
                    throw new UsernameNotFoundException("비활성화된 사용자입니다: " + username);
                }
                
                // 권한 설정 (ROLE_ 접두사 없이)
                ArrayList<String> authorities = new ArrayList<>();
                if (user.getIsAdmin()) {
                    authorities.add("ADMIN");
                    System.out.println("관리자 권한 추가");
                }
                authorities.add("USER");
                System.out.println("일반 사용자 권한 추가");
                
                System.out.println("최종 권한: " + authorities);
                
                return org.springframework.security.core.userdetails.User
                        .withUsername(user.getLoginId())
                        .password("{noop}" + user.getPassword()) // {noop}는 암호화되지 않은 비밀번호를 의미
                        .roles(authorities.toArray(new String[0]))
                        .build();
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/h2-console/**", "/test/**").permitAll()
                .requestMatchers("/match/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
//                .requestMatchers("/mypost/**").authenticated()
                            .requestMatchers(HttpMethod.POST,   "/mypost/**").authenticated()
                            .requestMatchers(HttpMethod.PUT,    "/mypost/**").authenticated()
                            .requestMatchers(HttpMethod.PATCH,  "/mypost/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/mypost/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/mypost/**").permitAll()
                .requestMatchers("/users/**").authenticated()
                .requestMatchers("/register", "/check-id", "/find-id").permitAll()


                            .requestMatchers(HttpMethod.GET, "/community/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/community/**").authenticated()


                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    res.sendRedirect("/login?needLogin=true");
                }))

            .csrf(csrf -> csrf.disable())
            .userDetailsService(userDetailsService());
        
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, 
                                            HttpServletResponse response, 
                                            Authentication authentication) 
                                            throws IOException, ServletException {
                
                // 데이터베이스에서 실제 User 엔티티 찾기
                User user = userRepository.findByLoginId(authentication.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));
                
                // 세션에 사용자 정보 저장
                request.getSession().setAttribute("loggedInUser", user);
                request.getSession().setAttribute("username", authentication.getName());
                
                // 사용자 역할에 따라 다른 페이지로 이동
                if (authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    response.sendRedirect("/admin");
                } else {
                    response.sendRedirect("/main");
                }
            }
        };
    }
}
