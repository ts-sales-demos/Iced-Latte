package com.zufar.icedlatte.review.exception;

import java.util.UUID;

public class DeniedProductReviewDeletionException extends RuntimeException {

    public DeniedProductReviewDeletionException(final UUID userId, final UUID productReviewId) {
        super(String.format("Deletion of the product' review with productReviewId = '%s' is denied for the user with userId = '%s'",
                productReviewId, userId));
    }
}
