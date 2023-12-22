package com.mshop.productservice.service.impl

import com.mshop.base.OffsetRequest
import com.mshop.base.Pagination
import com.mshop.base.SortRequest
import com.mshop.exception.BadRequestException
import com.mshop.exception.NotFoundException
import com.mshop.utils.Utils
import com.mshop.validator.ProductValidator
import com.mshop.productservice.dto.*
import com.mshop.productservice.mapper.*
import com.mshop.productservice.model.CategoryProduct
import com.mshop.productservice.repository.*
import com.mshop.productservice.service.ProductService
import com.mshop.productservice.service.SupplierService
import com.mshop.productservice.service.UserProductService
import com.mshop.productservice.service.UserService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository,
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository,
    private val categoryProductRepository: CategoryProductRepository,
    private val supplierService: SupplierService,
    private val productExtrasRepository: ProductExtrasRepository,
    private val userService: UserService,
    private val userProductRepository: UserProductRepository
) : ProductService {
    override suspend fun addProductToCategory(categoryProductDto: CategoryProductDto): CategoryProductDto {
        val categoryProduct = categoryProductDto.toCategoryProduct()
        categoryProductRepository.save(categoryProduct)
        return categoryProduct.toCategoryProductDto()
    }

    override suspend fun deleteProductOfCategory(categoryProductDto: CategoryProductDto): CategoryProductDto {
        val categoryProduct = categoryProductRepository.getByProductIdAndCategoryId(
            categoryProductDto.productId,
            categoryProductDto.categoryId
        )
            ?: throw com.mshop.exception.NotFoundException("Not found")
        categoryProductRepository.deleteCategoryProductByCategoryIdAndProductId(
            categoryId = categoryProductDto.categoryId,
            productId = categoryProductDto.productId,
        )
        return categoryProduct.toCategoryProductDto()
    }

    override suspend fun upsertProduct(userId: String, productDto: UpsertProductDto): UpsertProductDto =
        coroutineScope {
            validateUpsertProduct(productDto)
            val supplier =
                supplierRepository.getByUserId(userId)
                    ?: throw com.mshop.exception.NotFoundException("Not found supplier of user $userId")
            val createAt =
                if (productDto.productId.isNullOrEmpty()) null else productRepository.findById(productDto.productId)?.createdAt
            val product =
                productDto.toProduct(supplierId = supplier.supplierId, createAt = createAt)
            productRepository.save(product)
            launch {
                productExtrasRepository.save(
                    productDto.extras.toProductExtras(product.productId, product.isNew)
                )
            }
            if (product.isNew) {
                productDto.categoryIds?.map { categoryId ->
                    launch {
                        val categoryProduct =
                            CategoryProduct.from(productId = product.productId, categoryId = categoryId)
                        categoryProductRepository.save(categoryProduct)
                    }
                }

                productDto.images?.map { imageDto ->
                    launch {
                        val image = imageDto.toImage(ownerId = product.productId)
                        imageRepository.save(image)
                        imageDto.imageId = image.imageId
                        imageDto.ownerId = image.ownerId
                    }
                }?.joinAll()

                userService.getAllCustomer().map {
                    async {
                        val upsertUserProduct =
                            UpsertUserProductDto.create(userId = it.userId, productId = product.productId)
                                .toUserProduct(it.userId, isReviewed = false)
                        userProductRepository.save(upsertUserProduct)
                    }
                }.toList().awaitAll()
            }

            productDto.copy(productId = product.productId, sku = product.sku)
        }

    override suspend fun patchProduct(productDto: UpsertProductDto): UpsertProductDto = coroutineScope {
        val oldProduct = productRepository.findById(
            productDto.productId ?: throw com.mshop.exception.BadRequestException("require product id")
        ) ?: throw com.mshop.exception.NotFoundException("Not found product id=${productDto.productId}")
        productDto.extras?.let {
            launch {
                val oldProductExtras = productExtrasRepository.findById(productDto.productId)
                    ?: throw com.mshop.exception.NotFoundException("Not found product extra id=${productDto.productId}")
                val productExtra = it.combineProductExtra(oldProductExtras)
                productExtrasRepository.save(productExtra)
            }
        }
        val product = productDto.combineProduct(oldProduct)
        productRepository.save(product)
        productDto
    }

    override suspend fun deleteProduct(productId: String): String {
        productRepository.deleteById(productId)
        return productId
    }

    override suspend fun getListProductInfo(
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest?
    ): ProductPagination<ProductInfoDto> = coroutineScope {
        val count = async { productRepository.count() }
        val products = (if (sortRequest != null && sortRequest.sortBy.lowercase() == "created_at") {
            if (sortRequest.orderBy.uppercase() == "DESC") {
                productRepository.getProductInfoWithOffsetAndOrderByCreatedAtDesc(
                    offset = offsetRequest.offset,
                    limit = offsetRequest.limit
                )
            } else {
                productRepository.getProductInfoWithOffsetAndOrderByCreatedAtAsc(
                    offset = offsetRequest.offset,
                    limit = offsetRequest.limit
                )
            }
        } else {
            productRepository.getProductInfoWithOffset(
                offset = offsetRequest.offset,
                limit = offsetRequest.limit
            )
        })
            .map { product ->
                async {
                    product.images = listOf(imageRepository.getFirstByOwnerId(product.productId).url)
                    product.toProductInfoDto()
                }
            }
        ProductPagination(
            products = products.toList().awaitAll(),
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, products.count()),
                total = count.await()
            )
        )
    }

    override suspend fun getRecommendListProductInfo(userId:String, offsetRequest: com.mshop.base.OffsetRequest): ProductPagination<ProductInfoDto> =
        coroutineScope {
            // TODO: Implement get recommend by AI model here
            val count = async { productRepository.count() }
//            val products =
//                productRepository.getProductInfoWithOffset(offset = offsetRequest.offset, limit = offsetRequest.limit)
//                    .map { product ->
//                        async {
//                            product.images = listOf(imageRepository.getFirstByOwnerId(product.productId).url)
//                            product.toProductInfoDto()
//                        }
//                    }

            val products = productRepository.getProductInfoByUserIdWithOffset(offset = offsetRequest.offset, limit = offsetRequest.limit, userId =userId )
                .map { product ->
                    async {
                        product.images = listOf(imageRepository.getFirstByOwnerId(product.productId).url)
                        product.toProductInfoDto()
                    }
                }
            ProductPagination(
                products = products.toList().awaitAll(),
                pagination = com.mshop.base.Pagination(
                    offset = Utils.getNexOffset(offsetRequest.offset, products.count()),
                    total = count.await()
                )
            )
        }

    override suspend fun getListProductInfoByCategory(
        categoryId: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest?,
    ): ProductPagination<ProductInfoDto> = coroutineScope {
        val count = async { categoryProductRepository.countProductOfCategory(categoryId) }
        val products = (
                if (sortRequest != null && sortRequest.sortBy.lowercase() == "rating") {
                    if (sortRequest.orderBy.uppercase() == "DESC") {
                        productRepository.getProductInfoByCategoryIdWithOffsetAndOrderByRatingDesc(
                            categoryId = categoryId,
                            offset = offsetRequest.offset,
                            limit = offsetRequest.limit
                        )
                    } else {
                        productRepository.getProductInfoByCategoryIdWithOffsetAndOrderByRatingAsc(
                            categoryId = categoryId,
                            offset = offsetRequest.offset,
                            limit = offsetRequest.limit
                        )
                    }
                } else {
                    productRepository.getProductInfoByCategoryIdWithOffset(
                        categoryId = categoryId,
                        offset = offsetRequest.offset,
                        limit = offsetRequest.limit
                    )
                }
                ).map { productInfo ->
                async {
                    productInfo.images = listOf(imageRepository.getFirstByOwnerId(productInfo.productId).url)
                    productInfo.toProductInfoDto()
                }
            }
        ProductPagination(
            products = products.toList().awaitAll(),
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, products.count()),
                total = count.await()
            )
        )
    }

    override suspend fun getListProductInfoBySupplier(
        supplierId: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest?,
    ): ProductPagination<ProductInfoDto> = coroutineScope {
        val count = async { productRepository.countBySupplierId(supplierId) }
        val products = (
                if (sortRequest != null && sortRequest.sortBy.lowercase() == "created_at") {
                    if (sortRequest.orderBy.uppercase() == "DESC") {
                        productRepository.getProductInfoBySupplierIdWithOffsetAndOrderByCreatedAtDesc(
                            supplierId = supplierId, offset = offsetRequest.offset, limit = offsetRequest.limit
                        )
                    } else {
                        productRepository.getProductInfoBySupplierIdWithOffsetAndOrderByCreatedAtAsc(
                            supplierId = supplierId, offset = offsetRequest.offset, limit = offsetRequest.limit
                        )
                    }
                } else {
                    productRepository.getProductInfoBySupplierIdWithOffset(
                        supplierId = supplierId, offset = offsetRequest.offset, limit = offsetRequest.limit
                    )
                }
                ).map { product ->
                async {
                    product.images = listOf(imageRepository.getFirstByOwnerId(product.productId).url)
                    product.toProductInfoDto()
                }
            }

        ProductPagination(
            products = products.toList().awaitAll(),
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, products.count()),
                total = count.await(),
            )
        )
    }

    override suspend fun getListProductInfoByUserId(
        userId: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest?
    ): ProductPagination<ProductInfoDto> = coroutineScope {
        val supplierId = supplierRepository.getSupplierIdByUserId(userId)
            ?: throw com.mshop.exception.NotFoundException("Not found supplier for user $userId")
        getListProductInfoBySupplier(
            supplierId = supplierId,
            offsetRequest = offsetRequest,
            sortRequest = sortRequest,
        )
    }

    override suspend fun getProductDetailById(productId: String): ProductDto = coroutineScope {
        val product = productRepository.findById(productId) ?: throw com.mshop.exception.NotFoundException("Not found product $productId")
        val supplierInfo = async { supplierService.getSupplierInfoById(product.supplierId) }
        val extras = async { productExtrasRepository.findById(productId)?.toProductExtrasDto() }
        joinAll(
            launch {
                product.images = imageRepository.getAllByOwnerId(productId).toList()
            },
            launch {
                product.categories = categoryProductRepository.getByProductId(productId).map {
                    categoryRepository.findById(it.categoryId)!!
                }.toList()
            }
        )

        product.toProductDto(supplierInfo = supplierInfo.await(), extras = extras.await())
    }

    override suspend fun getProductInfoById(productId: String): ProductInfoDto = coroutineScope {
        val productInfo =
            productRepository.getProductInfoById(productId) ?: throw com.mshop.exception.NotFoundException("Not found product $productId")
        joinAll(
            launch {
                productInfo.images = listOf(imageRepository.getFirstByOwnerId(productInfo.productId).url)
            }
        )
        productInfo.toProductInfoDto()
    }

    override suspend fun getProductDetailBySku(sku: String): ProductDto = coroutineScope {
        val product = productRepository.getProductBySku(sku) ?: throw com.mshop.exception.NotFoundException("Not found product sku $sku")
        val supplierInfo = async { supplierService.getSupplierInfoById(product.supplierId) }
        val extras = async { productExtrasRepository.findById(product.productId)?.toProductExtrasDto() }
        joinAll(
            launch {
                product.supplier = supplierRepository.findById(product.supplierId)
                    ?: throw com.mshop.exception.NotFoundException("Not found supplier of user ${product.supplierId}")
            },
            launch {
                product.images = imageRepository.getAllByOwnerId(product.productId).toList()
            },
            launch {
                product.categories = categoryProductRepository.getByProductId(product.productId).map {
                    categoryRepository.findById(it.categoryId)!!
                }.toList()
            }
        )

        product.toProductDto(supplierInfo = supplierInfo.await(), extras = extras.await())
    }

    override suspend fun getProductInfoByCategoryAndName(
        categoryId: String?,
        name: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest?,
    ): ProductPagination<ProductInfoDto> {
        return when {
            categoryId.isNullOrBlank() && name.isBlank() -> {
                getListProductInfo(offsetRequest, sortRequest)
            }

            categoryId.isNullOrBlank() && name.isNotBlank() -> {
                getProductInfoByName(name, offsetRequest, sortRequest)
            }

            !categoryId.isNullOrBlank() && name.isBlank() -> {
                getListProductInfoByCategory(categoryId, offsetRequest, sortRequest)
            }

            else -> {
                getProductInfoByCategoryIdAndName(categoryId!!, name, offsetRequest, sortRequest)
            }
        }
    }

    override suspend fun getListProductInfoByIds(ids: List<String>): List<ProductInfoDto> = coroutineScope {
        productRepository.getListProductInfoByIds(ids).map { product ->
            async {
                product.images = listOf(imageRepository.getFirstByOwnerId(product.productId).url)
                product.toProductInfoDto()
            }
        }.toList().awaitAll()
    }

    override suspend fun getListOrderProductInfoByIds(ids: List<String>, supplierId: String?): List<OrderProductInfoDto> = coroutineScope {
        productRepository.getListOrderProductInfoByIds(ids).filter {
            if (supplierId.isNullOrBlank()) true
            else it.supplierId == supplierId
        }.map { product ->
            async {
                product.images = listOf(imageRepository.getFirstByOwnerId(product.productId).url)
                product.toOrderProductDto()
            }
        }.toList().awaitAll()
    }

    override suspend fun patchAmountProduct(patchProduct: PatchAmountProductDto): PatchAmountProductDto {
        val product = productRepository.findById(patchProduct.productId)
            ?: throw com.mshop.exception.NotFoundException("Not found product ${patchProduct.productId}")
        if (patchProduct.amount < 0 && abs(patchProduct.amount) > product.amount) {
            throw com.mshop.exception.BadRequestException("Not enough product")
        }
        product.amount += patchProduct.amount
        product.soldAmount -= patchProduct.amount
        productRepository.save(product)
        patchProduct.amount = product.amount
        return patchProduct
    }

    suspend fun getProductInfoByName(
        name: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest?
    ): ProductPagination<ProductInfoDto> =
        coroutineScope {
            val count = async { productRepository.countProductByNameContainsIgnoreCase(name) }
            val products = productRepository.getProductInfoByNameWithOffset(
                name = "%$name%",
                offset = offsetRequest.offset,
                limit = offsetRequest.limit
            )
                .map { product ->
                    async {
                        product.images = listOf(imageRepository.getFirstByOwnerId(product.productId).url)
                        product.toProductInfoDto()
                    }
                }
            ProductPagination(
                products = products.toList().awaitAll(),
                pagination = com.mshop.base.Pagination(
                    offset = Utils.getNexOffset(offsetRequest.offset, products.count()),
                    total = count.await()
                )
            )
        }

    suspend fun getProductInfoByCategoryIdAndName(
        categoryId: String,
        name: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest?
    ): ProductPagination<ProductInfoDto> = coroutineScope {
        val count = async { productRepository.countProductByCategoryIdAndName(categoryId, "%$name%") }
        val products = productRepository.getProductInfoByCategoryIdAndNameWithOffset(
            categoryId = categoryId,
            name = "%$name%",
            offset = offsetRequest.offset,
            limit = offsetRequest.limit
        )
            .map { product ->
                async {
                    product.images = listOf(imageRepository.getFirstByOwnerId(product.productId).url)
                    product.toProductInfoDto()
                }
            }
        ProductPagination(
            products = products.toList().awaitAll(),
            pagination = com.mshop.base.Pagination(
                offset = Utils.getNexOffset(offsetRequest.offset, products.count()),
                total = count.await()
            )
        )
    }

    private fun validateUpsertProduct(productDto: UpsertProductDto) {
        if (!ProductValidator.validateNameProduct(productDto.name.orEmpty())) throw com.mshop.exception.BadRequestException(
            "Product's name is too short, require at least 5 character"
        )
        if (!ProductValidator.validateAmount(productDto.amount)) throw com.mshop.exception.BadRequestException("Product's amount must be more than 0")
        if (!ProductValidator.validatePrice(
                productDto.price,
                productDto.listedPrice
            )
        ) throw com.mshop.exception.BadRequestException("Product's price or listed price must not be null and more than 0")
    }
}