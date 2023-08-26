package tukorea.devhive.swapshopbackend.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tukorea.devhive.swapshopbackend.model.dto.login.LoginDTO;
import tukorea.devhive.swapshopbackend.model.dto.post.PostDTO;
import tukorea.devhive.swapshopbackend.service.post.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 생성
    @PostMapping
    public ResponseEntity<PostDTO> create(@AuthenticationPrincipal LoginDTO userDTO,
                                          @RequestPart("post") String postJson,
                                          @RequestPart(value="image",required = false) Optional<List<MultipartFile>> image) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 직렬화
        PostDTO postDTO = objectMapper.readValue(postJson, PostDTO.class); // JSON 문자열을 PostDTO 객체로 변환
        PostDTO post = postService.create(userDTO, postDTO, image.orElse(Collections.emptyList()));
        return ResponseEntity.ok(post);
    }

    // 전체 조회
    @GetMapping()
    public WrappedResponse<List<PostDTO>> findPosts(){
        return new WrappedResponse<>(true,postService.showList(),"성공");
    }

    // 내가 작성한 모든 작성글 조회
    @GetMapping("/my")
    public ResponseEntity<List<PostDTO>> showListByLogin(@AuthenticationPrincipal LoginDTO userDTO){
        return ResponseEntity.ok(postService.showListByLogin(userDTO));
    }

    // 개별 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> findPost(HttpServletRequest request, HttpServletResponse response, @PathVariable("postId") Long postId){
        return ResponseEntity.ok(postService.getPostById(request,response,postId));
    }

    // 삭제된 리소스의 상세 정보를 함께 응답
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDTO> delete(@PathVariable("postId") Long postId){
        PostDTO delete = postService.delete(postId);
        return ResponseEntity.ok(delete);
    }

    // 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostDTO> patchPost(@AuthenticationPrincipal LoginDTO userDTO,
                                             @PathVariable("postId") Long postId,
                                             @RequestPart(value = "post") PostDTO postDTO,
                                             @RequestPart(value="image",required = false) List<MultipartFile> image) throws IOException {
        PostDTO update=postService.update(userDTO,postId,postDTO,image);
        return ResponseEntity.ok(update);
    }

    //페이지네이션 (no-offset 방식 (무한 스크롤))
    @GetMapping("/api")
    public Page<PostDTO> getPosts(@RequestParam Long lastPostId,@RequestParam int size){
        return postService.pagePost(lastPostId,size);
    }

}
