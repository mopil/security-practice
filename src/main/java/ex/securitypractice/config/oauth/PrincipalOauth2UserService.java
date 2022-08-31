package ex.securitypractice.config.oauth;

import ex.securitypractice.config.auth.PrincipalDetails;
import ex.securitypractice.model.User;
import ex.securitypractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    // @AuthenticationPrincipal 어노테이션이 함수가 종료될때 만들어짐
    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest: "+userRequest);
        // registrationID 로 어떤 OAuth 로 로그인 했는지 확인 가능
        
        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> 코드를 리턴(OAuth-Client 라이브러리) -> 액세스 토큰 요청
        // -> userRequest 정보 -> 회원 프로필을 받아야함 (loadUser 함수) -> 회원 프로필
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // 구글로 부터 받아온 정보로 강제 회원가입 진행

        String provider = userRequest.getClientRegistration().getClientId(); // google
        String providerId = oAuth2User.getAttribute("sub"); // 구글에서 지정한 유저 PK 값임
        String username = provider+"_"+providerId; // google_5234523412341235 (유저네임 유니크화)
        String password = bCryptPasswordEncoder.encode("아무거나");
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
