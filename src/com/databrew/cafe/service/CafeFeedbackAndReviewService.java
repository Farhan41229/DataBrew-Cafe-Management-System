package com.databrew.cafe.service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * CafeFeedbackAndReviewService
 * ----------------------------
 * Manages customer feedback, ratings, moderation,
 * analytics, and review reporting.
 */
public class CafeFeedbackAndReviewService {

    private final Map<String, Review> reviews = new HashMap<>();
    private final Map<String, List<String>> reviewsByCustomer = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    /* ==========================
       REVIEW CREATION
       ========================== */

    public String submitReview(String customerId,
                               int rating,
                               String comment) {

        validateString(customerId, "Customer ID");
        validateRating(rating);

        String reviewId = UUID.randomUUID().toString();
        Review review = new Review(
                reviewId,
                customerId,
                rating,
                comment,
                ReviewStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        reviews.put(reviewId, review);
        reviewsByCustomer
                .computeIfAbsent(customerId, k -> new ArrayList<>())
                .add(reviewId);

        log("Submitted review " + reviewId + " rating=" + rating);
        return reviewId;
    }

    /* ==========================
       MODERATION
       ========================== */

    public void approveReview(String reviewId) {
        Review review = getReviewOrThrow(reviewId);

        if (review.status != ReviewStatus.PENDING) {
            throw new IllegalStateException("Review not pending");
        }

        review.status = ReviewStatus.APPROVED;
        review.lastUpdated = LocalDateTime.now();

        log("Approved review " + reviewId);
    }

    public void rejectReview(String reviewId, String reason) {
        Review review = getReviewOrThrow(reviewId);

        if (review.status != ReviewStatus.PENDING) {
            throw new IllegalStateException("Review not pending");
        }

        review.status = ReviewStatus.REJECTED;
        review.moderationNote = reason;
        review.lastUpdated = LocalDateTime.now();

        log("Rejected review " + reviewId + " reason=" + reason);
    }

    /* ==========================
       REVIEW UPDATES
       ========================== */

    public void updateComment(String reviewId, String newComment) {
        validateString(newComment, "Comment");
        Review review = getReviewOrThrow(reviewId);

        if (review.status == ReviewStatus.REJECTED) {
            throw new IllegalStateException("Cannot update rejected review");
        }

        review.comment = newComment;
        review.lastUpdated = LocalDateTime.now();

        log("Updated comment for review " + reviewId);
    }

    /* ==========================
       REVIEW QUERIES
       ========================== */

    public Review getReview(String reviewId) {
        return getReviewOrThrow(reviewId);
    }

    public List<Review> listAllReviews() {
        return new ArrayList<>(reviews.values());
    }

    public List<Review> listApprovedReviews() {
        List<Review> result = new ArrayList<>();
        for (Review review : reviews.values()) {
            if (review.status == ReviewStatus.APPROVED) {
                result.add(review);
            }
        }
        return result;
    }

    public List<Review> listReviewsForCustomer(String customerId) {
        List<Review> result = new ArrayList<>();
        for (String id : reviewsByCustomer.getOrDefault(customerId, new ArrayList<>())) {
            result.add(reviews.get(id));
        }
        return result;
    }

    /* ==========================
       ANALYTICS
       ========================== */

    public double getAverageRating() {
        int total = 0;
        int count = 0;

        for (Review review : reviews.values()) {
            if (review.status == ReviewStatus.APPROVED) {
                total += review.rating;
                count++;
            }
        }

        return count == 0 ? 0 : (double) total / count;
    }

    public Map<Integer, Integer> getRatingDistribution() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            map.put(i, 0);
        }

        for (Review review : reviews.values()) {
            if (review.status == ReviewStatus.APPROVED) {
                map.put(review.rating, map.get(review.rating) + 1);
            }
        }
        return map;
    }

    public int getTotalReviewCount() {
        return reviews.size();
    }

    public int getApprovedReviewCount() {
        int count = 0;
        for (Review review : reviews.values()) {
            if (review.status == ReviewStatus.APPROVED) {
                count++;
            }
        }
        return count;
    }

    /* ==========================
       EXPORT
       ========================== */

    public String exportApprovedReviews() {
        StringBuilder builder = new StringBuilder();
        builder.append("CUSTOMER_ID,RATING,COMMENT,CREATED_AT\n");

        for (Review review : reviews.values()) {
            if (review.status == ReviewStatus.APPROVED) {
                builder.append(review.customerId).append(",")
                       .append(review.rating).append(",")
                       .append(review.comment.replace(",", " ")).append(",")
                       .append(review.createdAt).append("\n");
            }
        }
        return builder.toString();
    }

    /* ==========================
       AUDIT LOG
       ========================== */

    private void log(String message) {
        auditLog.add(LocalDateTime.now() + " :: " + message);
    }

    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }

    /* ==========================
       VALIDATION
       ========================== */

    private void validateString(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    /* ==========================
       INTERNAL ACCESS
       ========================== */

    private Review getReviewOrThrow(String reviewId) {
        Review review = reviews.get(reviewId);
        if (review == null) {
            throw new NoSuchElementException("Review not found: " + reviewId);
        }
        return review;
    }

    /* ==========================
       INNER MODEL
       ========================== */

    public enum ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public static class Review {
        public String id;
        public String customerId;
        public int rating;
        public String comment;
        public ReviewStatus status;
        public String moderationNote;
        public LocalDateTime createdAt;
        public LocalDateTime lastUpdated;

        public Review(String id,
                      String customerId,
                      int rating,
                      String comment,
                      ReviewStatus status,
                      LocalDateTime createdAt,
                      LocalDateTime lastUpdated) {
            this.id = id;
            this.customerId = customerId;
            this.rating = rating;
            this.comment = comment;
            this.status = status;
            this.createdAt = createdAt;
            this.lastUpdated = lastUpdated;
        }
    }
}
