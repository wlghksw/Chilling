package com.example.users.service;

import com.example.users.entity.PostMedia;
import com.example.users.repository.PostMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostMediaService {
    //보류!!!!
    private final PostMediaRepository postMediaRepository;

    public void saveMedias(Integer postId, List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) return;

        // uploads/posts/{postId}/ 디렉토리 준비
        Path baseDir = Paths.get("uploads", "posts", String.valueOf(postId));
        Files.createDirectories(baseDir);

        // 다음 media_order
        int nextOrder = Optional.ofNullable(
                postMediaRepository.findMaxOrderByPostId(postId)
        ).orElse(0) + 1;

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String contentType = Optional.ofNullable(file.getContentType()).orElse("");
            boolean isImage = contentType.startsWith("image/");
            boolean isVideo = contentType.startsWith("video/");
            if (!isImage && !isVideo) continue; // 이미지/영상만 허용

            // 파일명 생성
            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains(".")) ?
                    original.substring(original.lastIndexOf(".")) : "";
            String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 디스크 저장
            Path savePath = baseDir.resolve(savedName);
            Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

            // 웹에서 접근 가능한 URL (application.yml에 맞춤)
            String webUrl = "/uploads/posts/" + postId + "/" + savedName;

            // DB 기록
            PostMedia pm = new PostMedia();
            pm.setPostId(postId);
            pm.setMediaType(isVideo ? PostMedia.MediaType.video : PostMedia.MediaType.image);
            pm.setMediaUrl(webUrl);
            pm.setMediaOrder(nextOrder++);
            postMediaRepository.save(pm);
        }
    }

}
