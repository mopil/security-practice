package ex.securitypractice.auth;

import ex.securitypractice.model.User;
import ex.securitypractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
시큐리티 설정에서 loginProcessingUrl("/login")
/login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어있는 loadUserByUsername 함수가 실행
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 시큐리티 session(내부 Authentication(내부 UserDetails))

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 여기 파라미터로 받는 username 은 form 에서 name="username" 
        // 에서 넘어오는 거임 이거를 바꾸고 싶으면 시큐리티 설정에서 usernameParameter() 을 설정 해 줘야함

        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new PrincipalDetails(user);
        }
        return null;
    }
}
