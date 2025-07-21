package com.ict.protice01.jwt;

import java.util.ArrayList;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.ict.protice01.members.entity.MembersEntity;
import com.ict.protice01.members.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {
    private final MembersRepository membersRepository;

    /** ID 인증 확인 및 사용자 정보 넘기기 (보안 인증에 필요) */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MembersEntity membersEntity = membersRepository.findByMid(username)
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다."));
        return new User(membersEntity.getMid(), membersEntity.getMpw(), new ArrayList<>());
    }
}
