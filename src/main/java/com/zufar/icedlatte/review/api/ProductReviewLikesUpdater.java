package com.zufar.icedlatte.review.api;

import com.zufar.icedlatte.openapi.dto.ProductReviewLikeDto;
import com.zufar.icedlatte.review.converter.ProductReviewDtoConverter;
import com.zufar.icedlatte.review.entity.ProductReviewLike;
import com.zufar.icedlatte.review.repository.ProductReviewLikeRepository;
import com.zufar.icedlatte.review.repository.ProductReviewRepository;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewLikesUpdater {

    private final ProductReviewLikeRepository productReviewLikeRepository;
    private final ProductReviewRepository productReviewRepository;

    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final ProductReviewDtoConverter productReviewDtoConverter;
    private final ProductReviewValidator productReviewValidator;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductReviewLikeDto update(final UUID productId,
                                       final UUID productReviewId,
                                       final Boolean productReviewLike) {
        var userId = securityPrincipalProvider.getUserId();

        productReviewValidator.validateProductExists(productId);
        productReviewValidator.validateReviewExists(productReviewId);
        productReviewValidator.validateProductIdIsValid(productId, productReviewId);

        var productReviewLikeEntity = productReviewLikeRepository
                .findByUserIdAndProductReviewId(userId, productReviewId)
                .map(productReviewRate -> {
                    productReviewRate.setIsLike(productReviewLike);
                    return productReviewRate;
                })
                .orElseGet(() ->
                        ProductReviewLike.builder()
                                .userId(userId)
                                .productId(productId)
                                .productReviewId(productReviewId)
                                .isLike(productReviewLike)
                                .build()
                );

        productReviewLikeRepository.save(productReviewLikeEntity);

        productReviewRepository.updateLikesCount(productReviewId);
        productReviewRepository.updateDislikesCount(productReviewId);

        return productReviewDtoConverter.toProductReviewLikeDto(productReviewLikeEntity);
    }
}