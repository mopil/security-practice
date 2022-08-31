package ex.securitypractice.config.oauth;

import ex.securitypractice.config.auth.PrincipalDetails;
import ex.securitypractice.config.oauth.provider.FacebookUserInfo;
import ex.securitypractice.config.oauth.provider.GoogleUserInfo;
import ex.securitypractice.config.oauth.provider.NaverUserInfo;
import ex.securitypractice.config.oauth.provider.OAuth2UserInfo;
import ex.securitypractice.model.User;
import ex.securitypractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    // @AuthenticationPrincipal 어노테이션이 함수가 종료될때 만들어짐
    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // registrationID 로 어떤 OAuth 로 로그인 했는지 확인 가능

        
        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> 코드를 리턴(OAuth-Client 라이브러리) -> 액세스 토큰 요청
        // -> userRequest 정보 -> 회원 프로필을 받아야함 (loadUser 함수) -> 회원 프로필
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("attributes = "+oAuth2User.getAttributes()); // attributes 에 유저 정보가 담겨있음

        String providerName = userRequest.getClientRegistration().getRegistrationId(); // google
        OAuth2UserInfo oAuth2UserInfo = null;
        switch (providerName) {
            case "google" -> {
                System.out.println("구글 로그인 요청");
                oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            }
            case "facebook" -> {
                System.out.println("페이스북 로그인 요청");
                oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
            }
            case "naver" -> {
                System.out.println("네이버 로그인 요청");
                oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
            }
            default -> System.out.println("우리는 구글과 페이스북, 네이버만 지원해요");
        }

        // OAuth2 로 받아온 정보로 강제 회원가입 진행
        assert oAuth2UserInfo != null;
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId; // google_5234523412341235 (유저네임 유니크화)
        String password = bCryptPasswordEncoder.encode("아무거나");
        String email = oAuth2UserInfo.getEmail();
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
