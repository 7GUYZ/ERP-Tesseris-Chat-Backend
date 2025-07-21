package com.ict.protice01.bbs.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ict.protice01.bbs.dto.BbsDTO;
import com.ict.protice01.bbs.dto.BbsDetailDTO;
import com.ict.protice01.bbs.entity.BbsEntity;
import com.ict.protice01.bbs.service.BbsService;
import com.ict.protice01.vo.DataVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bbs")
public class BbsController {
    private final BbsService bbsService;

    @GetMapping("/bbslist")
    public DataVO BbsList(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        try {
            Page<BbsDTO> bbslist = bbsService.GuestBookList(page, size);
            if (bbslist == null) {
                return new DataVO(true, null, "No - Data");
            } else {
                return new DataVO(true, bbslist, "Success");
            }
        } catch (Exception e) {
            return new DataVO(false, null, "불러오는 중 오류 발생");
        }
    }

    @PostMapping("/bbsinsert")
    public DataVO BbsInsert(@RequestPart("data") BbsEntity bbsEntity,
            @RequestPart(value = "f_name", required = false) MultipartFile file, HttpServletRequest request) {
        try {
            String tokens = request.getHeader("Authorization").replace("Bearer ", "");
            BbsEntity entity = bbsService.BbsInsert(bbsEntity, file, tokens);
            return new DataVO(true, entity.getB_idx(), "게시글이 등록 되었습니다.");
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

    @GetMapping("/bbsdetail")
    public DataVO BbsDetail(@RequestParam String b_idx) {
        try {
            BbsDetailDTO bbsDetailDTO = bbsService.BbsDetail(b_idx);
            return new DataVO(true, bbsDetailDTO, null);
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

}
