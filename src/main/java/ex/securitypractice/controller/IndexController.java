package ex.securitypractice.controller;

import ex.securitypractice.config.auth.PrincipalDetails;
import ex.securitypractice.model.User;
import ex.securitypractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 유저 세션 정보를 받는 방법
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
            Authentication authentication, // 1. Authentication 객체로 받아서 가져오기 (다운캐스팅 필요)
            @AuthenticationPrincipal PrincipalDetails userDetails // 2. 어노테이션으로 한번에 꺼내오기
    ) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // object 타입으로 나오기 때문에 다운캐스팅 해줘야함
        System.out.println("userDetails: "+userDetails.getUser());
        System.out.println("principalDetails: "+principalDetails.getUser());

        return "세션 정보 확인하기";
    }

    /*
    세션 정보를 가져오는 방법이 두가지 인데, 어노테이션으로 꺼내오는게 쉬워보임
     */
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth
    ) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getAuthorities();
        System.out.println("userDetails: "+oauth.getAttributes());
        System.out.println("principalDetails: "+oAuth2User.getAttributes());

        return "OAuth2 세션 정보 확인하기";
    }

    @GetMapping({"", "/"})
    public String index() {
        // 머스테치 기본폴더 src/main/resources/
        return "index";
    }

    // OAuth 로그인을 해도 PrincipalDetails 를 받을 수 있고
    // 일반 로그인을 해도 받을 수 있음
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails: "+principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        user.setRole("ROLE_USER");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user); // 패스워드 암호화가 되어있지 않으면 시큐리티 로그인을 할 수 없음
        return "redirect:/loginForm"; // 리다이렉트를 붙이면 해당 URL 핸들러 메소드를 호출함
    }

    @Secured("ROLE_ADMIN") // 한나만 걸고 싶을 때
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGE') or hasRole('ROLE_ADMIN')") // 여러개를 걸고 싶을 때
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "개인정보";
    }
}
