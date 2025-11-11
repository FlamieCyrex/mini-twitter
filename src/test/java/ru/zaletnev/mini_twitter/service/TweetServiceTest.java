package ru.zaletnev.mini_twitter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.zaletnev.mini_twitter.model.Tweet;
import ru.zaletnev.mini_twitter.model.User;
import ru.zaletnev.mini_twitter.model.dto.CreateTweetRequest;
import ru.zaletnev.mini_twitter.model.dto.TweetDto;
import ru.zaletnev.mini_twitter.repository.TweetRepository;
import ru.zaletnev.mini_twitter.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
public class TweetServiceTest {
    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TweetService tweetService;

    private User author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        author = User.builder()
                .id(UUID.randomUUID())
                .username("john")
                .build();
    }

    @Test
    @DisplayName("create: проверка сохранения твита и DTO")
    void createTweetSavesEntityAndReturnsDto() {
        CreateTweetRequest request = new CreateTweetRequest("Hello world");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(author));

        Tweet saved = Tweet.builder()
                .id(UUID.randomUUID())
                .author(author)
                .content("Hello world")
                .createdAt(Instant.parse("2023-09-01T10:15:30Z"))
                .build();
        when(tweetRepository.save(any(Tweet.class))).thenReturn(saved);

        TweetDto dto = tweetService.createTweet("john", request);

        assertThat(dto.id()).isEqualTo(saved.getId().toString());
        assertThat(dto.authorUsername()).isEqualTo("john");
        assertThat(dto.content()).isEqualTo("Hello world");
        assertThat(dto.createdAt()).isEqualTo("2023-09-01T10:15:30Z");

        ArgumentCaptor<Tweet> captor = ArgumentCaptor.forClass(Tweet.class);
        verify(tweetRepository).save(captor.capture());
        Tweet tweetToSave = captor.getValue();
        assertThat(tweetToSave.getAuthor()).isEqualTo(author);
        assertThat(tweetToSave.getContent()).isEqualTo("Hello world");
    }

    @Test
    @DisplayName("get:проверка пагинации твитов общая")
    void tweetFeedReturnsPageOfDtos() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
        Tweet tweet = Tweet.builder()
                .id(UUID.randomUUID())
                .author(author)
                .content("Test")
                .createdAt(timestamp)
                .build();
        Pageable pageable = PageRequest.of(0, 20);
        when(tweetRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(List.of(tweet), pageable, 1));

        Page<TweetDto> page = tweetService.tweetFeed(pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        TweetDto dto = page.getContent().getFirst();
        assertThat(dto.authorUsername()).isEqualTo("john");
        assertThat(dto.content()).isEqualTo("Test");
        assertThat(dto.createdAt()).isEqualTo(timestamp.toString());
    }

    @Test
    @DisplayName("get:проверка пагинации твитов для юзера")
    void userTweetsReturnsPageOfDtos() {
        Instant timestamp = Instant.parse("2024-02-01T00:00:00Z");
        Tweet tweet = Tweet.builder()
                .id(UUID.randomUUID())
                .author(author)
                .content("Personal")
                .createdAt(timestamp)
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        when(tweetRepository.findByAuthor_UsernameOrderByCreatedAtDesc("john", pageable))
                .thenReturn(new PageImpl<>(List.of(tweet), pageable, 1));

        Page<TweetDto> page = tweetService.userTweets("john", pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        TweetDto dto = page.getContent().getFirst();
        assertThat(dto.authorUsername()).isEqualTo("john");
        assertThat(dto.content()).isEqualTo("Personal");
        assertThat(dto.createdAt()).isEqualTo(timestamp.toString());
    }

    @Test
    @DisplayName("delete:проверка удаления твита по автору(УСПЕХ)")
    void deleteTweetRemovesTweetWhenAuthorMatches() throws Exception {
        UUID tweetId = UUID.randomUUID();
        Tweet tweet = Tweet.builder()
                .id(tweetId)
                .author(author)
                .content("Hello")
                .createdAt(Instant.now())
                .build();
        when(tweetRepository.findById(tweetId)).thenReturn(Optional.of(tweet));

        tweetService.deleteTweet("john", tweetId);

        verify(tweetRepository).delete(tweet);
    }

    @Test
    @DisplayName("delete:проверка удаления твита по автору(ПРОВАЛ)")
    void deleteTweetThrowsWhenAuthorDoesNotMatch() {
        UUID tweetId = UUID.randomUUID();
        Tweet tweet = Tweet.builder()
                .id(tweetId)
                .author(User.builder().username("alice").build())
                .content("Hello")
                .createdAt(Instant.now())
                .build();
        when(tweetRepository.findById(tweetId)).thenReturn(Optional.of(tweet));

        assertThatThrownBy(() -> tweetService.deleteTweet("john", tweetId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Not your tweet!");

        verify(tweetRepository, never()).delete(any());
    }
}
