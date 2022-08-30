package ex.securitypractice.auth;

import ex.securitypractice.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/*
시큐리티가 /login 을 낚아채서 로그인을 진행시킴
로그인을 진행이 완료가 되면 시큐리티 session을 만들어줌 (SecurityContextHolder)
시큐리티 세션 공간에 들어갈수 있는 오브젝트는 정해져 있음 -> Authentication 타입 객체
Authentication 안에는 User 정보가 있어야 됨
User 오브젝트 타입도 정해져 있음 -> UserDetails 타입 객체

Security Session 에 들어갈 수 있는 객체 -> Authentication 객체 -> UserDetails 객체
*/
@AllArgsConstructor
public class PrincipalDetails implements UserDetails {

    private User user; // 콤포지션

    // 해당 유저의 권한을 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        /*
        ex. 우리 사이트에서 1년동안 회원이 로그인을 안하면 휴면계정으로 전환하고 싶으면?
        유저 엔티티에 로긴 시간을 저장하는 칼럼을 넣어놓고 이를 비교
        현재시간 - 로긴시간 => 1년을 초과하면 return false
         */
        return true;
    }
}
