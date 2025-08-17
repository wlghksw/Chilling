    package com.example.users.controller;

    import com.example.users.dto.PostsDTO;
    import com.example.users.entity.Likes;
    import com.example.users.entity.Posts;
    import com.example.users.repository.*;
    import com.example.users.service.PostMediaService;
    import com.example.users.service.PostsService;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpSession;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Controller;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.time.format.DateTimeFormatter;
    import java.util.*;

    @Controller
    @RequiredArgsConstructor
    @RequestMapping("/post")
    public class PostsController {

        private final UsersRepository usersRepository;
        private final PostsService postsService;
        private final PostsRepository postsRepository;
        private final LikesRepository likesRepository;
        private final CommentsRepository commentsRepository;
        private final PostMediaRepository postMediaRepository;
        private final PostMediaService postMediaService;

        private static final int INTRO_CATEGORY_ID = 1;


        // 목록 페이지
        @GetMapping("/sport/{sportId}/intro")
        public String introList(@PathVariable("sportId") Integer sportId,
                                @RequestParam(name = "sort", defaultValue = "created_at") String sort,
                                @RequestParam(name = "q", required = false) String q,
                                Model model) {

            final Integer categoryId = INTRO_CATEGORY_ID;


            List<Posts> list;
            if (q != null && !q.isBlank()) {
                // 검색 있을 때: 제목 검색 → 아래에서 정렬 적용
                list = postsRepository.searchByTitle(sportId, categoryId, q.trim());
            } else {

                list = switch (sort) {
                    case "view_count" -> postsRepository.findBySportIdAndCategoryIdOrderByViewCountDesc(sportId, categoryId);
                    case "like_count" -> postsRepository.findBySportIdAndCategoryIdOrderByLikeCountDesc(sportId, categoryId);
                    default -> postsRepository.findBySportIdAndCategoryIdOrderByCreatedAtDesc(sportId, categoryId);
                };
            }


            if (q != null && !q.isBlank()) {
                Comparator<Posts> byLikeDesc = Comparator
                        .comparing((Posts p) -> nvl(p.getLike_count())).reversed()
                        .thenComparing((Posts p) -> nvl(p.getView_count()), Comparator.reverseOrder())
                        .thenComparing((Posts p) -> nvl(p.getPost_id()), Comparator.reverseOrder());

                Comparator<Posts> byViewDesc = Comparator
                        .comparing((Posts p) -> nvl(p.getView_count())).reversed()
                        .thenComparing((Posts p) -> nvl(p.getLike_count()), Comparator.reverseOrder())
                        .thenComparing((Posts p) -> nvl(p.getPost_id()), Comparator.reverseOrder());

                Comparator<Posts> byCreatedDesc = (a, b) -> {
                    var ta = a.getCreated_at();
                    var tb = b.getCreated_at();
                    if (ta == null && tb == null) return 0;
                    if (tb == null) return -1;
                    if (ta == null) return 1;
                    int primary = tb.compareTo(ta);
                    return (primary != 0) ? primary
                            : Integer.compare(nvl(b.getPost_id()), nvl(a.getPost_id()));
                };

                list = switch (sort) {
                    case "view_count" -> list.stream().sorted(byViewDesc).toList();
                    case "like_count" -> list.stream().sorted(byLikeDesc).toList();
                    default -> list.stream().sorted(byCreatedDesc).toList();
                };
            }


            Set<Long> userIds = new HashSet<>();
            for (Posts p : list) {
                if (p.getUser_id() != null) userIds.add(p.getUser_id().longValue());
            }
            Map<Long, String> nicknameById = new HashMap<>();
            usersRepository.findAllById(userIds).forEach(u ->
                    nicknameById.put(u.getUser_id(), u.getNickname())
            );


            List<Map<String, Object>> rows = new ArrayList<>();
            int total = list.size();
            DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (int i = 0; i < list.size(); i++) {
                Posts p = list.get(i);
                Map<String, Object> m = new HashMap<>();
                m.put("rowNo", total - i);
                m.put("post_id", p.getPost_id());
                m.put("title", p.getTitle());

                Long uid = (p.getUser_id() == null) ? null : p.getUser_id().longValue();
                String nickname = (uid == null) ? "익명" : nicknameById.getOrDefault(uid, "탈퇴회원");
                m.put("nickname", nickname);

                m.put("created_at", p.getCreated_at() == null ? "" : p.getCreated_at().toLocalDate().format(dateOnly));
                m.put("view_count", p.getView_count());
                m.put("like_count", p.getLike_count());
                rows.add(m);
            }


            model.addAttribute("posts", rows);
            model.addAttribute("sportId", sportId);
            model.addAttribute("categoryId", categoryId);

            model.addAttribute("q", q == null ? "" : q.trim());
            model.addAttribute("sort", sort);
            model.addAttribute("sort_created_at", "created_at".equals(sort));
            model.addAttribute("sort_view_count", "view_count".equals(sort));
            model.addAttribute("sort_like_count", "like_count".equals(sort));

            return "post/list";
        }



        //글쓰기 폼
        @GetMapping("/sport/{sportId}/intro/new")
        public String introNewForm(@PathVariable("sportId") Integer sportId,
                                   HttpServletRequest request,
                                   Model model) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("LOGIN_USER_ID") == null) {
                return "redirect:/users/login";
            }
            Long uid = (Long) session.getAttribute("LOGIN_USER_ID");
            usersRepository.findByUserId(uid)
                    .ifPresent(u -> model.addAttribute("nickname", u.getNickname()));

            model.addAttribute("sportId", sportId);
            model.addAttribute("categoryId", INTRO_CATEGORY_ID);
            return "post/new";
        }

        //글 등록 처리
        @PostMapping(value = "/sport/{sportId}/intro", consumes = "multipart/form-data")
        public String introCreate(@PathVariable("sportId") Integer sportId,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) String content,
                                      @RequestParam Integer categoryId,
                                      @RequestParam(name = "files", required = false) MultipartFile[] files,
                                      HttpServletRequest request,
                                      Model model) {

                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("LOGIN_USER_ID") == null) {
                    return "redirect:/users/login";
                }
                Integer userId = ((Long) session.getAttribute("LOGIN_USER_ID")).intValue();


                List<String> errors = new ArrayList<>();
                if (title == null || title.isBlank()) errors.add("제목을 입력하세요.");
                if (content == null || content.isBlank()) errors.add("내용을 입력하세요.");

                if (!errors.isEmpty()) {
                    model.addAttribute("errors", errors);
                    model.addAttribute("sportId", sportId);
                    model.addAttribute("categoryId", categoryId);
                    model.addAttribute("title", title);
                    model.addAttribute("content", content);
                    return "post/new"; // 다시 작성 폼으로
                }


                postsService.createBasic(userId, title, content, sportId, categoryId);

                Posts saved = postsRepository
                        .findLatestByUserAndSportAndCategoryAndTitle(userId, sportId, categoryId, title)
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (saved != null) {
                    try {

                        postMediaService.saveMedias(
                                saved.getPost_id(),
                                (files == null) ? List.of() : Arrays.asList(files)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return "redirect:/post/" + saved.getPost_id();
                }

                return "redirect:/post/sport/" + sportId + "/intro";
            }

        @GetMapping("/{id}")
        public String view(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {

            Posts post = postsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다. id=" + id));

            String nickname = "익명";
            if (post.getUser_id() != null) {
                nickname = usersRepository.findByUserId(post.getUser_id().longValue())
                        .map(u -> u.getNickname())
                        .orElse("탈퇴회원");
            }

            DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String created_at = (post.getCreated_at() == null) ? "" : post.getCreated_at().toLocalDate().format(dateOnly);


            HttpSession session = request.getSession(false);
            Long loginUserId = (session == null) ? null : (Long) session.getAttribute("LOGIN_USER_ID");
            boolean isOwner = (loginUserId != null && post.getUser_id() != null
                    && loginUserId.intValue() == post.getUser_id());

            boolean isLoggedIn = (loginUserId != null);
            boolean hasLiked = false;
            if (isLoggedIn) {
                hasLiked = likesRepository.existsByPostIdAndUserId(id, loginUserId.intValue());
            }

            if (isLoggedIn) {
                @SuppressWarnings("unchecked")
                Set<String> viewedKeys = (Set<String>) session.getAttribute("VIEWED_KEYS");
                if (viewedKeys == null) {
                    viewedKeys = new HashSet<>();
                    session.setAttribute("VIEWED_KEYS", viewedKeys);
                }

                String key = "u:" + loginUserId + ":p:" + id;
                if (viewedKeys.add(key)) {
                    postsService.increaseViewCount(id);
                    post.setView_count((post.getView_count() == null ? 0 : post.getView_count()) + 1);
                }
            }


            var commentList = commentsRepository.findAllActiveByPostId(id);

            Set<Long> cUserIds = new HashSet<>();
            for (var c : commentList) {
                if (c.getUser_id() != null) cUserIds.add(c.getUser_id().longValue());
            }
            Map<Long, String> cNickById = new HashMap<>();
            usersRepository.findAllById(cUserIds).forEach(u -> cNickById.put(u.getUser_id(), u.getNickname()));

            List<Map<String, Object>> commentRows = new ArrayList<>();
            DateTimeFormatter cfmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (var c : commentList) {
                Map<String, Object> m = new HashMap<>();
                m.put("comment_id", c.getComment_id());
                m.put("content", c.getContent());
                m.put("created_at", c.getCreated_at() == null ? "" : c.getCreated_at().format(cfmt));

                m.put("parent_comment_id", c.getParent_comment_id());
                m.put("isReply", c.getParent_comment_id() != null);

                Long cuid = (c.getUser_id() == null) ? null : c.getUser_id().longValue();
                m.put("nickname", cuid == null ? "익명" : cNickById.getOrDefault(cuid, "탈퇴회원"));
                boolean canDelete = (loginUserId != null && c.getUser_id() != null
                        && loginUserId.intValue() == c.getUser_id());
                m.put("canDelete", canDelete);
                commentRows.add(m);
            }
            var medias = postMediaRepository.findAllByPostIdOrderByMediaOrderAsc(id);
            
            record MV(String url, boolean image, boolean video) {}
            var mediaViews = medias.stream()
                    .map(m -> new MV(
                            m.getMediaUrl(),  // 저장해둔 "/uploads/..." 같은 경로
                            m.getMediaType() == com.example.users.entity.PostMedia.MediaType.image,
                            m.getMediaType() == com.example.users.entity.PostMedia.MediaType.video
                    ))
                    .toList();

            model.addAttribute("nickname", nickname);
            model.addAttribute("post", post);
            model.addAttribute("isOwner", isOwner);
            model.addAttribute("isLoggedIn", isLoggedIn);
            model.addAttribute("hasLiked", hasLiked);
            model.addAttribute("medias", mediaViews);
            model.addAttribute("created_at", created_at);


            model.addAttribute("comments", commentRows);
            model.addAttribute("commentCount", commentRows.size()); // 템플릿에서 {{commentCount}} 쓰면 필요

            return "post/view";
        }


        @GetMapping("/{id}/edit")
        public String editForm(@PathVariable Integer id,
                               HttpServletRequest request,
                               Model model) {
            Posts post = postsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다. id=" + id));

            HttpSession session = request.getSession(false);
            Long loginUserId = (session == null) ? null : (Long) session.getAttribute("LOGIN_USER_ID");
            if (loginUserId == null) {
                return "redirect:/users/login";
            }
            if (post.getUser_id() == null || !loginUserId.equals(post.getUser_id().longValue())) {
                return "redirect:/post/" + id + "?error=forbidden";
            }

            // 날짜 포맷
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String createdAt = post.getCreated_at() != null ? post.getCreated_at().format(dtf) : "";
            String updatedAt = post.getUpdated_at() != null ? post.getUpdated_at().format(dtf) : "";

            model.addAttribute("post", post);
            model.addAttribute("createdAt", createdAt);
            model.addAttribute("updatedAt", updatedAt);

            return "post/edit";
        }

        @PostMapping("/{id}/edit")
        public String edit(@PathVariable Integer id,
                           @RequestParam String title,
                           @RequestParam String content,
                           HttpServletRequest request) {
            Posts post = postsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다. id=" + id));

            HttpSession session = request.getSession(false);
            Long loginUserId = (session == null) ? null : (Long) session.getAttribute("LOGIN_USER_ID");
            if (loginUserId == null) return "redirect:/users/login";
            if (post.getUser_id() == null || !loginUserId.equals(post.getUser_id().longValue())) {
                return "redirect:/post/" + id + "?error=forbidden";
            }

            post.setTitle(title);
            post.setContent(content);
            post.setUpdated_at(java.time.LocalDateTime.now());
            postsRepository.save(post);

            return "redirect:/post/" + id;
        }

        @PostMapping("/{id}/delete")
        public String delete(@PathVariable Integer id, HttpServletRequest request) {
            Posts post = postsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다. id=" + id));

            HttpSession session = request.getSession(false);
            Long loginUserId = (session == null) ? null : (Long) session.getAttribute("LOGIN_USER_ID");
            if (loginUserId == null) return "redirect:/users/login";
            if (post.getUser_id() == null || !loginUserId.equals(post.getUser_id().longValue())) {
                return "redirect:/post/" + id + "?error=forbidden";
            }


            Integer sportId = post.getSport_id();

            postsRepository.delete(post);

            return "redirect:/post/sport/" + sportId + "/intro";
        }

        //좋아요
        @PostMapping("/{id}/like")
        @Transactional
        public String toggleLike(@PathVariable Integer id, HttpServletRequest request) {
            Posts post = postsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다. id=" + id));

            HttpSession session = request.getSession(false);
            Long loginUserId = (session == null) ? null : (Long) session.getAttribute("LOGIN_USER_ID");
            if (loginUserId == null) return "redirect:/users/login";

            Integer uid = loginUserId.intValue();

            boolean already = likesRepository.existsByPostIdAndUserId(id, uid);
            if (already) {
                // 좋아요 취소
                likesRepository.deleteByPostIdAndUserId(id, uid);
                post.setLike_count(Math.max(0, (post.getLike_count() == null ? 0 : post.getLike_count()) - 1));
            } else {
                // 좋아요 추가
                Likes l = new Likes();
                l.setPostId(id);
                l.setUserId(uid);
                likesRepository.save(l);
                post.setLike_count((post.getLike_count() == null ? 0 : post.getLike_count()) + 1);
            }

            postsRepository.save(post);
            return "redirect:/post/" + id;
        }


        private Integer nvl(Integer v) {
            return v == null ? 0 : v;
        }
    }

