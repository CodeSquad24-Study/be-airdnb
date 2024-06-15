package team07.airbnb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team07.airbnb.common.auth.aop.Authenticated;
import team07.airbnb.data.review.dto.request.ReviewPostRequest;
import team07.airbnb.data.review.dto.response.ReviewWithReplyResponse;
import team07.airbnb.data.user.dto.response.TokenUserInfo;
import team07.airbnb.data.user.enums.Role;
import team07.airbnb.entity.ReviewEntity;
import team07.airbnb.exception.auth.UnAuthorizedException;
import team07.airbnb.service.accommodation.AccommodationService;
import team07.airbnb.service.booking.BookingService;
import team07.airbnb.service.review.ReviewService;
import team07.airbnb.service.user.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static team07.airbnb.data.user.enums.Role.*;

@Tag(name = "리뷰")
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final AccommodationService accommodationService;
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final UserService userService;

    @Operation(summary = "숙소 리뷰 조회")
    @GetMapping("/{accommodationId}")
    @ResponseStatus(OK)
    public List<ReviewWithReplyResponse> getReviews(@PathVariable Long accommodationId) {
        return accommodationService.findById(accommodationId).reviews()
                .stream()
                .map(ReviewWithReplyResponse::of).toList();
    }

    @Tag(name = "User")
    @Operation(summary = "리뷰 작성")
    @Authenticated(USER)
    @PostMapping("/{bookingId}")
    @ResponseStatus(OK)
    public void postReview(@PathVariable Long bookingId, @RequestBody ReviewPostRequest request, TokenUserInfo user) {
        if (!bookingService.isUserBookerOf(bookingId, userService.getCompleteUser(user))) {
            throw new UnAuthorizedException(ReviewController.class, user.id(), "ID : {%d} 유저가 자신의 예약이 아닌 예약에 리뷸를 남기려고함".formatted(user.id()));
        }

        bookingService.addReview(bookingId, user.id(), new ReviewEntity(bookingService.findByBookingId(bookingId), request.content(), request.rating()));
    }

    @Tag(name = "Host")
    @Operation(summary = "리뷰 댓글 작성")
    @Authenticated(HOST)
    @PostMapping("/{reviewId}/reply")
    @ResponseStatus(OK)
    public void replyToReview(@PathVariable Long reviewId, @RequestBody String content, TokenUserInfo user) {
        reviewService.addReplyTo(reviewId, content, userService.getCompleteUser(user));
    }

    @Tag(name = "User")
    @Operation(summary = "리뷰 내용 수정")
    @PutMapping("/{reviewId}")
    @Authenticated(USER)
    @ResponseStatus(OK)
    public void updateReview(@PathVariable Long reviewId,
                             @RequestBody @NotBlank String content,
                             TokenUserInfo user) {
        if (!reviewService.isWriterOf(reviewId, userService.getCompleteUser(user))) {
            throw new UnAuthorizedException(ReviewController.class, user.id(), "ID : {%d} 유저가 자신의 리뷰가 아닌 리뷰를 수정하려고함".formatted(user.id()));
        }

        reviewService.updateReviewContent(reviewId, content);
    }
}