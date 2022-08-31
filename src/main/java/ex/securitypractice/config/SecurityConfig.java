//package ex.securitypractice.config;
//
//import ex.securitypractice.config.oauth.PrincipalOauth2UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
//public class SecurityConfig {
//
//    private final PrincipalOauth2UserService principalOauth2UserService;
//
//    // 해당 메서드의 리턴 오브젝트를 IoC 컨테이너에 등록한다.
//    @Bean
//    public BCryptPasswordEncoder encodePwd() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/users/**").authenticated() // 인증만 되면 들어갈 수 있는 주소
//                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
//                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
//                .anyRequest().permitAll()
//
//                .and()
//                .formLogin()
//                .loginPage("/loginForm")
//                .loginProcessingUrl("/login") // /login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행함
//                .defaultSuccessUrl("/") // 로그인이 성공하면 이동
//
//                .and()
//                .oauth2Login()
//                .loginPage("/loginForm")
//                // 구글 로그인이 완료된 후 후처리가 필요함
//                // 1.코드받기 (인증) 2.액세스토큰(권한) 3.사용자프로필 정보를 가져와서 4.회원가입 자동으로 시키기
//                // 구글 로그인 팁 : 코드를 받는게 아니라, 액세스 토큰 + 사용자프로필정보를 한번에 받음
//                .userInfoEndpoint()
//                .userService(principalOauth2UserService)
//                .and()
//                .successHandler(null)
//                .build();
//    }
//
//
//}
