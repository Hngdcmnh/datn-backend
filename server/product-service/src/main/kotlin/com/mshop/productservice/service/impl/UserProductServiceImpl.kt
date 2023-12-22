package com.mshop.productservice.service.impl

import com.mshop.base.OffsetRequest
import com.mshop.base.Pagination
import com.mshop.exception.BadRequestException
import com.mshop.utils.Logger
import com.mshop.utils.Utils
import com.mshop.productservice.dto.*
import com.mshop.productservice.mapper.combine
import com.mshop.productservice.mapper.toReviewDto
import com.mshop.productservice.mapper.toUserProduct
import com.mshop.productservice.mapper.toUserProductDto
import com.mshop.productservice.model.Image
import com.mshop.productservice.repository.ImageRepository
import com.mshop.productservice.repository.ProductRepository
import com.mshop.productservice.repository.UserProductRepository
import com.mshop.productservice.service.CoreService
import com.mshop.productservice.service.ProductService
import com.mshop.productservice.service.UserProductService
import com.mshop.productservice.service.UserService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.lang.Exception
import kotlin.math.pow
import kotlin.math.sqrt

@Service
class UserProductServiceImpl(
    private val coreService: CoreService,
    private val productRepository: ProductRepository,
    private val userProductRepository: UserProductRepository,
    private val imageRepository: ImageRepository,
    private val productService: ProductService,
    private val userService: UserService,
) : UserProductService {
    override suspend fun postUserProduct(
        userId: String,
        upsertUserProductDto: UpsertUserProductDto
    ): UpsertUserProductDto {
        if (upsertUserProductDto.productId.isNullOrEmpty()) throw com.mshop.exception.BadRequestException("Required product id")
        val upsertUserProduct = upsertUserProductDto.toUserProduct(userId, isReviewed = true)
        userProductRepository.save(upsertUserProduct)
        return upsertUserProductDto
    }

    override suspend fun postAllUserProductForNewUser(userId: String): Boolean = coroutineScope {
        productRepository.findAll().map {
            async {
                val upsertUserProduct =
                    UpsertUserProductDto.create(userId = userId, productId = it.productId)
                        .toUserProduct(userId, isReviewed = false)
                userProductRepository.save(upsertUserProduct)
            }
        }.toList().awaitAll()
        true
    }

    suspend fun handleCollaboratingFiltering(userId: String): Boolean {
        try {
            //calculate avg for all user product
            val userProducts = userProductRepository.findAll().toList()

            try {
                for (userProduct in userProducts) {
                    val userProductsByUser = userProducts.filter { it.userId == userProduct.userId && it.isReviewed }
                    if (userProductsByUser.isNotEmpty()) {
                        userProduct.avgRate = 0.0F
                        for (i in userProductsByUser) {
                            userProduct.avgRate += i.rate
                        }
                        userProduct.avgRate /= userProductsByUser.size

                        if (!userProduct.isReviewed) {
                            userProduct.matchRate = 0.0F
                        } else {
                            userProduct.matchRate = userProduct.rate - userProduct.avgRate
                        }
                        userProductRepository.save(userProduct)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }


            val userProducts1 = userProductRepository.findAll().toList()
            val userSimilarityMap = mutableMapOf<Pair<String, String>, Float>()

            try {
                //calculate similarity between all of user
                val userIds = userService.getAllCustomer().map { it.userId }

                for (userId1 in userIds) {
                    for (userId2 in userIds) {
                        val key = Pair(userId1, userId2)
                        if (userId1 != userId2 && !userSimilarityMap.contains(key)) {
                            val userProductsById1 = userProducts1.filter { it.userId == userId1 }
                            val userProductsById2 = userProducts1.filter { it.userId == userId2 }

                            Logger.info("collaborate: ${userProductsById1.size} -- ${userProductsById2.size}")
                            val numerator = userProductsById1.zip(userProductsById2) { product1, product2 ->
                                product1.matchRate * product2.matchRate
                            }.sum()

                            if (numerator == 0.0F) continue
                            Logger.info("collaborate: numerate ${numerator}")

                            val magnitude1 = sqrt(userProductsById1.sumByDouble { it.matchRate.toDouble().pow(2.0) })
                            val magnitude2 = sqrt(userProductsById2.sumByDouble { it.matchRate.toDouble().pow(2.0) })
                            Logger.info("collaborate: magnitude ${magnitude1}  -- ${magnitude2}")

                            userSimilarityMap[key] = (numerator / (magnitude1 * magnitude2)).toFloat()
                            Logger.info("collaborate: similarity ${userSimilarityMap[key]}")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }

            //update rating estimate for user product isReviewed = false

            try {
                for (userProduct in userProducts1) {
                    if (!userProduct.isReviewed) {
                        Logger.info("collaborate ${userProduct.userId} - ${userProduct.userProductId}")
                        val bestMatch =
                            userSimilarityMap.filter { it.key.first == userProduct.userId || it.key.second == userProduct.userId }
                                .maxByOrNull { it.value }
                        Logger.info(" bestMatch ${bestMatch?.key?.first}  ${bestMatch?.key?.second}   ${bestMatch?.value} ")
                        if (bestMatch != null) {
                            val firstUserProduct =
                                userProductRepository.getUserProductsByUserIdAndProductId(
                                    bestMatch.key.first,
                                    userProduct.productId
                                ).toList()[0]
                            val secondUserProduct =
                                userProductRepository.getUserProductsByUserIdAndProductId(
                                    bestMatch.key.second,
                                    userProduct.productId
                                ).toList()[0]
                            if (firstUserProduct.userProductId == userProduct.userProductId) {
                                firstUserProduct.matchRate = secondUserProduct.matchRate
                                Logger.info("id ${firstUserProduct.userProductId} ${firstUserProduct.matchRate} \n ")
                                userProductRepository.save(firstUserProduct)
                            } else {
                                secondUserProduct.matchRate = firstUserProduct.matchRate
                                Logger.info("id ${secondUserProduct.userProductId}  ${secondUserProduct.matchRate} \n ")
                                userProductRepository.save(secondUserProduct)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun postAllUserProductForNewProduct(productId: String): Boolean = coroutineScope {

        userService.getAllCustomer().map {
            async {
                val upsertUserProduct =
                    UpsertUserProductDto.create(userId = it.userId, productId = productId)
                        .toUserProduct(it.userId, isReviewed = false)
                userProductRepository.save(upsertUserProduct)
            }
        }.toList().awaitAll()
        true
    }

    override suspend fun postListUserProduct(upsertListUserProductDto: UpsertListUserProductDto): List<String> =
        coroutineScope {
            productService.getListProductInfoByIds(upsertListUserProductDto.productIds).map {
                async {
                    val upsertUserProduct = userProductRepository.getUserProductsByUserIdAndProductId(
                        upsertListUserProductDto.userId,
                        it.productId
                    ).toList()[0]
//                        UpsertUserProductDto.create(userId = upsertListUserProductDto.userId, productId = it.productId)
//                            .toUserProduct(upsertListUserProductDto.userId, isReviewed = false)
                    userProductRepository.save(upsertUserProduct)
                    upsertUserProduct.userProductId
//                    upsertUserProduct.userProductId
                }
            }.toList().awaitAll()
        }


    override suspend fun upsertReview(userId: String, upsertUserProductDto: UpsertUserProductDto): UserProductDto =
        coroutineScope {
            if (upsertUserProductDto.userProductId == null) throw com.mshop.exception.BadRequestException("Required review id")
            val userProduct = userProductRepository.findById(upsertUserProductDto.userProductId)
                ?: throw com.mshop.exception.BadRequestException("Not found review ${upsertUserProductDto.userProductId}")
            val updateUserProduct = upsertUserProductDto.combine(userProduct)

            coreService.upsertComment(updateUserProduct)

            userProductRepository.save(updateUserProduct)
            handleCollaboratingFiltering(userId)

            updateUserProduct.toUserProductDto(userInfo = userService.getUserInfo(userId))
        }

    override suspend fun deleteComment(userProductId: String): String = coroutineScope {
        userProductRepository.findById(userProductId)
            ?: throw com.mshop.exception.BadRequestException("Not found comment or review $userProductId")
        userProductRepository.deleteById(userProductId)
        userProductId
    }

    override suspend fun getComments(offsetRequest: com.mshop.base.OffsetRequest): CommentPagination<UserProductDto> = coroutineScope {
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
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, comments.count()),
                total = totalCount.await(),
            )
        )
    }

    override suspend fun getCommentsByProductId(
        productId: String,
        offsetRequest: com.mshop.base.OffsetRequest
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
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, comments.count()),
                total = totalCount.await(),
            )
        )
    }

    override suspend fun getReviewsByUserId(
        userId: String,
        offsetRequest: com.mshop.base.OffsetRequest
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
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, reviews.count()),
                total = totalCount.await(),
            )
        )
    }

    override suspend fun getCommentsByProductIdAndReviewed(
        productId: String,
        isReviewed: Boolean,
        offsetRequest: com.mshop.base.OffsetRequest
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
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, comments.count()),
                total = totalCount.await(),
            )
        )
    }

    override suspend fun getReviewsByUserIdAndReviewed(
        userId: String,
        isReviewed: Boolean,
        offsetRequest: com.mshop.base.OffsetRequest
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
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, reviews.count()),
                total = totalCount.await(),
            )
        )
    }
}