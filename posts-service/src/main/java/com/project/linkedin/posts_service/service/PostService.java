package com.project.linkedin.posts_service.service;

import com.project.linkedin.posts_service.auth.UserContextHolder;
import com.project.linkedin.posts_service.clients.ConnectionsClient;
import com.project.linkedin.posts_service.dto.PersonDto;
import com.project.linkedin.posts_service.dto.PostCreateRequestDto;
import com.project.linkedin.posts_service.dto.PostDto;
import com.project.linkedin.posts_service.entity.Post;
import com.project.linkedin.posts_service.exception.ResourceNotFoundException;
import com.project.linkedin.posts_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsClient connectionsClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId) {
        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);

        Post savedPost = postRepository.save(post);
        return modelMapper.map(savedPost, PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        log.info("Retrieving post with id,{}", postId);

        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User Id is, {} ", userId);

        List<PersonDto> firstConnections = connectionsClient.getFirstConnections();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + postId));

        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts
                .stream()
                .map((element) -> modelMapper.map(element, PostDto.class))
                .collect(Collectors.toList());
    }
}
