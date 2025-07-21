package com.ict.protice01.guestbook.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ict.protice01.guestbook.dto.GuestBookDTO;
import com.ict.protice01.guestbook.dto.GuestBookDetailDTO;
import com.ict.protice01.guestbook.entity.GuestBookEntity;
import com.ict.protice01.guestbook.service.GuestBookService;
import com.ict.protice01.vo.DataVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guestbook")
public class GuestBookController {
    private final GuestBookService guestBookService;

    @GetMapping("/guestbooklist")
    public DataVO GuestBookList() {
        try {
            List<GuestBookDTO> guestbooklist = guestBookService.GuestBookList();
            if (guestbooklist == null) {
                return new DataVO(true, null, "방명록이 없습니다.");
            } else {
                return new DataVO(true, guestbooklist, "불러오기 성공");
            }
        } catch (Exception e) {
            return new DataVO(false, null, "방명록을 불러오는 중 오류가 발생하였습니다.");
        }
    }

    @PostMapping("/guestbookinsert")
    public DataVO GuestBookInsert(@RequestPart("data") GuestBookEntity guestBookEntity,
            @RequestPart(value = "gb_f_name", required = false) MultipartFile file, HttpServletRequest request) {
        try {
            String tokens = request.getHeader("Authorization").replace("Bearer ", "");
            GuestBookEntity bookEntity = guestBookService.GuestBookInsert(guestBookEntity, file, tokens);
            return new DataVO(true, bookEntity.getGb_idx(), "방명록이 등록 되었습니다.");
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

    @PostMapping("/guestbookupdate")
    public DataVO GuestBookUpdate(@RequestPart("data") GuestBookEntity entity, @RequestPart(value = "gb_f_name", required = false)MultipartFile file) {
        try {
            guestBookService.GuestBookInsert(entity, file, null);
            return new DataVO(true, null, "방명록이 수정이 되었습니다.");
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

    @GetMapping("/guestbookdetail")
    public DataVO GetGuestBookDetail(@RequestParam("gb_idx") Long param) {
        try {
            GuestBookDetailDTO bookDetailDTO = guestBookService.GetGuestBookDetail(param);
            return new DataVO(true, bookDetailDTO, "");
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

    @PostMapping("/guestbookdelete")
    public DataVO GuestBookDetailDelete(@RequestBody Map<String, String> pramsMap) {
        try {
            guestBookService.GuestBookDetailDelete(pramsMap);
            return new DataVO(true, null, "방명록을 삭제하였습니다.");
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

}
