package com.ict.protice01.guestbook.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ict.protice01.common.util.Util;
import com.ict.protice01.guestbook.dto.GuestBookDTO;
import com.ict.protice01.guestbook.dto.GuestBookDetailDTO;
import com.ict.protice01.guestbook.entity.GuestBookEntity;
import com.ict.protice01.guestbook.repository.GuestBookRepository;
import com.ict.protice01.jwt.JwtUtil;
import com.ict.protice01.members.entity.MembersEntity;
import com.ict.protice01.members.repository.MembersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestBookService {
    private final PasswordEncoder passwordEncoder;
    private final GuestBookRepository guestBookRepository;
    private final Util util;
    private final JwtUtil jwtUtil;
    private final MembersRepository membersRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public List<GuestBookDTO> GuestBookList() {
        List<GuestBookEntity> guestbookEntities = guestBookRepository
                .findAll(Sort.by("gbregdate").descending());
        List<GuestBookDTO> guestBookDTOs = guestbookEntities.stream().map(k -> new GuestBookDTO(
                k.getGb_idx(),
                k.getGb_name(),
                k.getGbsubject(),
                k.getGbregdate().format(formatter)))
                .collect(Collectors.toList());
        return guestBookDTOs;
    }

    public GuestBookEntity GuestBookInsert(GuestBookEntity guestBookEntity, MultipartFile file, String tokens) {
        if (guestBookEntity.getGb_idx() == null) {
            MembersEntity membersEntity = membersRepository.findByMid(jwtUtil.ValidateAndExtractUserID(tokens))
                    .orElseThrow(() -> new RuntimeException("일치하는 회원이 없습니다."));
            guestBookEntity.setGb_name(membersEntity.getM_name());
            guestBookEntity.setGb_pw(passwordEncoder.encode(guestBookEntity.getGb_pw()));
            guestBookEntity.setGbregdate(LocalDateTime.now());
            if (file != null) {
                guestBookEntity.setGbfname(util.UploadFile(file));
            }
            return guestBookRepository.save(guestBookEntity);
        } else {
            GuestBookEntity bookEntity = guestBookRepository.findById(guestBookEntity.getGb_idx())
                    .orElseThrow(() -> new RuntimeException("등록된 게시글이 없습니다."));

            if (file != null) {
                guestBookEntity.setGb_old_file_name(bookEntity.getGbfname());
                guestBookEntity.setGbfname(util.UploadFile(file));
            }else{
                guestBookEntity.setGbfname(bookEntity.getGbfname());
                guestBookEntity.setGb_old_file_name(bookEntity.getGb_old_file_name());
            }
            if (passwordEncoder.matches(guestBookEntity.getGb_pw(), bookEntity.getGb_pw())) {
                guestBookEntity.setGb_pw(bookEntity.getGb_pw());
                return guestBookRepository.save(guestBookEntity);
            } else {
                throw new RuntimeException("비밀번호가 틀렸습니다.");
            }
        }
    }
    public GuestBookDetailDTO GetGuestBookDetail(Long param) {
        GuestBookEntity bookEntity = guestBookRepository.findById(param)
                .orElseThrow(() -> new RuntimeException("일치하는 게시물이 없습니다."));
        GuestBookDetailDTO detailDTO = new GuestBookDetailDTO(param, bookEntity.getGb_name(), bookEntity.getGbsubject(),
                bookEntity.getGbcontent(), bookEntity.getGbregdate().format(formatter), bookEntity.getGbfname(),
                bookEntity.getGb_old_file_name());
        return detailDTO;
    }

    public void GuestBookDetailDelete(Map<String, String> paramsMap) {
        GuestBookEntity dto = guestBookRepository.findById(Long.parseLong(paramsMap.get("gb_idx")))
                .orElseThrow(() -> new RuntimeException("일치하는 게시물이 없습니다."));
        if (passwordEncoder.matches(paramsMap.get("gb_pw"), dto.getGb_pw())) {
            guestBookRepository.deleteById(dto.getGb_idx());
        }else{
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }
    }
}
