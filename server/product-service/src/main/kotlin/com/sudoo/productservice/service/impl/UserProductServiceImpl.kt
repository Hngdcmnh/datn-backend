package com.sudoo.productservice.service.impl

import com.sudoo.domain.base.OffsetRequest
import com.sudoo.domain.base.Pagination
import com.sudoo.domain.exception.BadRequestException
import com.sudoo.domain.exception.NotFoundException
import com.sudoo.domain.utils.Utils
import com.sudoo.productservice.dto.*
import com.sudoo.productservice.mapper.combine
import com.sudoo.productservice.mapper.toReviewDto
import com.sudoo.productservice.mapper.toUserProduct
import com.sudoo.productservice.mapper.toUserProductDto
import com.sudoo.productservice.model.Image
import com.sudoo.productservice.repository.ImageRepository
import com.sudoo.productservice.repository.ProductRepository
import com.sudoo.productservice.repository.UserProductRepository
import com.sudoo.productservice.service.CoreService
import com.sudoo.productservice.service.ProductService
import com.sudoo.productservice.service.UserProductService
import com.sudoo.productservice.service.UserService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class UserProductServiceImpl(
    private val coreService: CoreService,
    private val userProductRepository: UserProductRepository,
    private val imageRepository: ImageRepository,
    private val productService: ProductService,
    private val userService: UserService,
) : UserProductService {
    override suspend fun postUserProduct(
        userId: String,
        upsertUserProductDto: UpsertUserProductDto
    ): UpsertUserProductDto {
        if (upsertUserProductDto.productId.isNullOrEmpty()) throw BadRequestException("Required product id")
        val upsertUserProduct = upsertUserProductDto.toUserProduct(userId, isReviewed = false)
        userProductRepository.save(upsertUserProduct)
        return upsertUserProductDto
    }

    override suspend fun postListUserProduct(upsertListUserProductDto: UpsertListUserProductDto): List<String> = coroutineScope {
        productService.getListProductInfoByIds(upsertListUserProductDto.productIds).map {
           async {
               val upsertUserProduct =
                   UpsertUserProductDto.create(userId = upsertListUserProductDto.userId, productId = it.productId)
                       .toUserProduct(upsertListUserProductDto.userId, isReviewed = false)
               userProductRepository.save(upsertUserProduct)
               upsertUserProduct.userProductId
           }
        }.toList().awaitAll()
    }

    override suspend fun upsertReview(userId: String, upsertUserProductDto: UpsertUserProductDto): UserProductDto =
        coroutineScope {
            if (upsertUserProductDto.userProductId == null) throw BadRequestException("Required review id")
            val userProduct = userProductRepository.findById(upsertUserProductDto.userProductId)
                ?: throw BadRequestException("Not found review ${upsertUserProductDto.userProductId}")
            val updateUserProduct = upsertUserProductDto.combine(userProduct)

            coreService.upsertComment(updateUserProduct)
            updateUserProduct.images = upsertUserProductDto.images?.map {
                async {
                    imageRepository.save(Image.from(updateUserProduct.userProductId, it))
                }
            }?.awaitAll()
            userProductRepository.save(updateUserProduct)
            updateUserProduct.toUserProductDto(userInfo = userService.getUserInfo(userId))
        }

    override suspend fun deleteComment(userProductId: String): String = coroutineScope {
        userProductRepository.findById(userProductId)
            ?: throw BadRequestException("Not found comment or review $userProductId")
        userProductRepository.deleteById(userProductId)
        userProductId
    }

    override suspend fun getComments(offsetRequest: OffsetRequest): CommentPagination<UserProductDto> = coroutineScope {
        val totalCount = async { userProductRepository.count() }
        val comments = userProductRepository.getAllWithOffset(
            offset = offsetRequest.offset,
            limit = offsetRequest.limit
        ).map { userProduct ->
            val userInfo = async { userService.getUserInfo(userProduct.userId) }
            userProduct.images = imageRepository.getAllByOwnerId(userProduct.userProductId).toList()
            userProduct.toUserProductDto(userInfo = userInfo.await())
        }
        CommentPagination(
            comments = comments.toList(),
            pagination = Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, comments.count()),
                total = totalCount.await(),
            )
        )
    }

    override suspend fun getCommentsByProductId(
        productId: String,
        offsetRequest: OffsetRequest
    ): CommentPagination<UserProductDto> = coroutineScope {
        val totalCount = async { userProductRepository.countByProductId(productId) }
        val comments = userProductRepository.getAllByProductIdWithOffset(
            productId = productId,
            offset = offsetRequest.offset,
            limit = offsetRequest.limit
        ).map { userProduct ->
            val userInfo = async { userService.getUserInfo(userProduct.userId) }
            userProduct.images = imageRepository.getAllByOwnerId(userProduct.userProductId).toList()
            userProduct.toUserProductDto(userInfo = userInfo.await())
        }
        CommentPagination(
            comments = comments.toList(),
            pagination = Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, comments.count()),
                total = totalCount.await(),
            )
        )
    }

    override suspend fun getReviewsByUserId(
        userId: String,
        offsetRequest: OffsetRequest
    ): ReviewPagination<ReviewDto> = coroutineScope {
        val totalCount = async { userProductRepository.countByUserId(userId) }
        val reviews = userProductRepository.getAllByUserIdWithOffset(
            userId = userId,
            offset = offsetRequest.offset,
            limit = offsetRequest.limit
        ).map { userProduct ->
            val userInfo = async { userService.getUserInfo(userProduct.userId) }
            val productInfo = async { productService.getProductInfoById(userProduct.productId) }
            userProduct.toReviewDto(productInfo = productInfo.await(), userInfo = userInfo.await())
        }
        ReviewPagination(
            reviews = reviews.toList(),
            pagination = Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, reviews.count()),
                total = totalCount.await(),
            )
        )
    }

    override suspend fun getCommentsByProductIdAndReviewed(
        productId: String,
        isReviewed: Boolean,
        offsetRequest: OffsetRequest
    ): CommentPagination<UserProductDto> = coroutineScope {
        val totalCount = async { userProductRepository.countByProductIdAndReviewed(productId, isReviewed) }
        val comments = userProductRepository.getByProductIdAndReviewedWithOffset(
            productId,
            isReviewed,
            offset = offsetRequest.offset,
            limit = offsetRequest.limit
        ).map { userProduct ->
            val userInfo = async { userService.getUserInfo(userProduct.userId) }
            userProduct.images = imageRepository.getAllByOwnerId(userProduct.userProductId).toList()
            userProduct.toUserProductDto(userInfo = userInfo.await())
        }
        CommentPagination(
            comments = comments.toList(),
            pagination = Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, comments.count()),
                total = totalCount.await(),
            )
        )
    }

    override suspend fun getReviewsByUserIdAndReviewed(
        userId: String,
        isReviewed: Boolean,
        offsetRequest: OffsetRequest
    ): ReviewPagination<ReviewDto> = coroutineScope {
        val totalCount = async { userProductRepository.countByUserIdAndReviewed(userId, isReviewed) }
        val reviews = userProductRepository.getByUserIdAndReviewedWithOffset(
            userId = userId,
            isReviewed = isReviewed,
            offset = offsetRequest.offset,
            limit = offsetRequest.limit
        ).map { userProduct ->
            val userInfo = async { userService.getUserInfo(userProduct.userId) }
            val productInfo = async { productService.getProductInfoById(userProduct.productId) }
            userProduct.toReviewDto(productInfo = productInfo.await(), userInfo = userInfo.await())
        }
        ReviewPagination(
            reviews = reviews.toList(),
            pagination = Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, reviews.count()),
                total = totalCount.await(),
            )
        )
    }
}