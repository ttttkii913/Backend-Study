package org.springbootdeveloper2.service;

import lombok.RequiredArgsConstructor;
import org.springbootdeveloper2.domain.Article;
import org.springbootdeveloper2.dto.request.AddArticleRequest;
import org.springbootdeveloper2.dto.request.UpdateArticleRequest;
import org.springbootdeveloper2.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 빈으로 등록
@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
public class BlogService {

    private final BlogRepository blogRepository; // 컨트롤러는 서비스, 서비스는 레포지토리 주입

    // 블로그 글 추가 - POST
    public Article save(AddArticleRequest addArticleRequest, String userName) {
        return blogRepository.save(addArticleRequest.toEntity(userName));
    }

    // 블로그 글 목록 조회 - GET
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 블로그 글 한 개 조회 - GET
    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found: " + id));
    }

    // 블로그 글 한 개 삭제 - DELETE
    public void delete(Long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    // 블로그 글 수정 - PUT
    @Transactional
    public Article update(Long id, UpdateArticleRequest updateArticleRequest) {
        Article article = blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        article.update(updateArticleRequest.getTitle(), updateArticleRequest.getContent());

        return article;
    }

    // 게시글을 작성한 유저인지 확인하는 메소드 - 현재 인증 객체에 담겨있는 사용자의 정보와 글을 작성한 사용자의 정보 비교
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
