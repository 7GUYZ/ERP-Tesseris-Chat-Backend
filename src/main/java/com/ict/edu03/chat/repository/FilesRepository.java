package com.ict.edu03.chat.repository;

import com.ict.edu03.chat.entity.files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<files, Long> {
    // message_index로 파일 목록 조회
    List<files> findByMessageindex(Long messageIndex);
} 