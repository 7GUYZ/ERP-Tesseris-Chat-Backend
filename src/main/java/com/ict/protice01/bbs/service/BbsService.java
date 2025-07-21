package com.ict.protice01.bbs.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ict.protice01.bbs.dto.BbsDTO;
import com.ict.protice01.bbs.dto.BbsDetailDTO;
import com.ict.protice01.bbs.entity.BbsEntity;
import com.ict.protice01.bbs.repository.BbsRepository;
import com.ict.protice01.common.util.Util;
import com.ict.protice01.jwt.JwtUtil;
import com.ict.protice01.members.entity.MembersEntity;
import com.ict.protice01.members.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BbsService {
    private final BbsRepository bbsRepository;
    private final MembersRepository membersRepository;
    private final JwtUtil jwtUtil;
    private final Util util;
    private final PasswordEncoder passwordEncoder;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Page<BbsDTO> GuestBookList(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("writedate").descending());
           log.info("페이저블 : {}" , pageable);
            Page<BbsEntity> guestbook = bbsRepository.findByActive(0, pageable);
            log.info("값 : {}",guestbook);
            Page<BbsDTO> guestbookdto = guestbook.map(entity -> new BbsDTO(
                    entity.getB_idx(),
                    entity.getSubject(),
                    entity.getWriter(),
                    entity.getContent(),
                    entity.getWritedate(),
                    entity.getHit()));
            return guestbookdto;
        } catch (Exception e) {
            log.error("findby에서 오류남", e);
            return null;
        }
    }
    public BbsEntity BbsInsert(BbsEntity bbsEntity, MultipartFile file, String tokens) {
        if (bbsEntity.getB_idx() == null) {
            MembersEntity membersEntity = membersRepository.findByMid(jwtUtil.ValidateAndExtractUserID(tokens))
                    .orElseThrow(() -> new RuntimeException("일치하는 회원이 없습니다."));
            bbsEntity.setWriter(membersEntity.getM_name());
            bbsEntity.setPwd(passwordEncoder.encode(bbsEntity.getPwd()));
            bbsEntity.setWritedate(LocalDateTime.now());
            if (file != null) {
                bbsEntity.setF_name(util.UploadFile(file));
            }
            return bbsRepository.save(bbsEntity);
        } else {
            BbsEntity entity = bbsRepository.findById(bbsEntity.getB_idx())
                    .orElseThrow(() -> new RuntimeException("등록된 게시글이 없습니다."));
            if (file != null) {
                bbsEntity.setF_name(util.UploadFile(file));
            }
            if (passwordEncoder.matches(bbsEntity.getPwd(), entity.getPwd())) {
                bbsEntity.setPwd(entity.getPwd());
                return bbsRepository.save(bbsEntity);
            } else {
                throw new RuntimeException("비밀번호가 틀렸습니다.");
            }

        }
    }

    public BbsDetailDTO BbsDetail(String b_idx) {
        BbsEntity bbsEntity = bbsRepository.findById(Long.parseLong(b_idx))
                .orElseThrow(() -> new RuntimeException("일치하는 게시물이 없습니다."));

        BbsDetailDTO detailDTO = new BbsDetailDTO(String.valueOf(bbsEntity.getB_idx()), bbsEntity.getSubject(),
                bbsEntity.getWriter(), bbsEntity.getContent(), bbsEntity.getF_name(),
                bbsEntity.getWritedate().format(formatter), bbsEntity.getCommentEntities());
        return detailDTO;
    }
}
