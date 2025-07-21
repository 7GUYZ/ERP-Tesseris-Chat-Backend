package com.ict.protice01.members.service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ict.protice01.common.entity.RefreshEntity;
import com.ict.protice01.common.repository.RefreshRepository;
import com.ict.protice01.jwt.JwtUtil;
import com.ict.protice01.jwt.MyUserDetailService;
import com.ict.protice01.members.dto.MembersMyPageDTO;
import com.ict.protice01.members.entity.MembersEntity;
import com.ict.protice01.members.repository.MembersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MembersService {
    private final PasswordEncoder passwordEncoder;
    private final MyUserDetailService myUserDetailService;
    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;
    private final MembersRepository membersRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<String, String> LoginCheck(MembersEntity membersEntity) {
        UserDetails userDetails = myUserDetailService.loadUserByUsername(membersEntity.getMid());
        if (!passwordEncoder.matches(membersEntity.getMpw(), userDetails.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
        }
        String accessToken = jwtUtil.GenerateAccessToken(membersEntity.getMid());
        String refreshToken = jwtUtil.GenerateRefreshToken(membersEntity.getMid());
        refreshRepository
                .save(new RefreshEntity(membersEntity.getMid(), refreshToken, jwtUtil.ExtractExpiration(refreshToken)));
        Map<String, String> tokens = new HashMap<>();
        tokens.put("refreshToken", refreshToken);
        tokens.put("accessToken", accessToken);
        return tokens;
    }

    public Map<String, String> GetRefreshToken(Map<String, String> token) {
        if (jwtUtil.IsTokenExpired(token.get("refreshToken"))) {
            throw new RuntimeException("RT 만료토큰");
        }
        String m_id = jwtUtil.ValidateAndExtractUserID(token.get("refreshToken"));
        refreshRepository.findByMid(m_id).orElseThrow(() -> new RuntimeException("RefreshToken No Match"));
        String newaccessToken = jwtUtil.GenerateAccessToken(m_id);
        String newrefreshToken = jwtUtil.GenerateRefreshToken(m_id);
        refreshRepository.save(new RefreshEntity(m_id, newrefreshToken, jwtUtil.ExtractExpiration(newrefreshToken)));
        return Map.of("accessToken", newaccessToken, "refreshToken", newrefreshToken);
    }

    public MembersMyPageDTO GetMyPage(String token) {
        String m_id = jwtUtil.ValidateAndExtractUserID(token);
        MembersEntity membersEntity = membersRepository.findByMid(m_id)
                .orElseThrow(() -> new RuntimeException("일치하는 계정이 없습니다."));
        MembersMyPageDTO myPageDTO = new MembersMyPageDTO(membersEntity.getMid(), membersEntity.getM_name(),
                membersEntity.getM_phone(), membersEntity.getM_addr(),
                membersEntity.getM_addr2(), membersEntity.getM_email(), membersEntity.getM_reg().format(formatter));
            return myPageDTO;
            }


}
