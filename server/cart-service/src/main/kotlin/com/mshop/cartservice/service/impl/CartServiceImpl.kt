package com.mshop.cartservice.service.impl

import com.mshop.cartservice.controller.dto.*
import com.mshop.cartservice.controller.dto.order.OrderCartDto
import com.mshop.cartservice.controller.dto.order.OrderCartProductDto
import com.mshop.cartservice.repository.CartProductRepository
//import com.mshop.cartservice.service.ProductService
import com.mshop.cartservice.repository.CartRepository
import com.mshop.cartservice.repository.entity.*
import com.mshop.cartservice.service.CartService
import com.mshop.cartservice.service.ProductService
import com.mshop.exception.BadRequestException
import com.mshop.exception.NotFoundException
import com.mshop.utils.IdentifyCreator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class CartServiceImpl(
    val cartRepository: CartRepository,
    val cartProductRepository: CartProductRepository,
    val productService: ProductService
) : CartService {

    override suspend fun getActiveCart(userId: String): CartDto {
        return try {
            val activeCarts = cartRepository.findCartByUserIdAndStatus(userId, "active").toList()

            val cart: Cart =
                if (activeCarts.isNotEmpty()) activeCarts[0] else createCartByStatus(userId, "active").toCart()

            val cartProducts = getCartProducts(cart.cartId)
            val cartDto = CartDto(
                userId = cart.userId,
                cartId = cart.cartId,
                totalPrice = 0.0,
                quantity = 0,
                status = cart.status,
                cartProducts = cartProducts
            )

            for (cartProduct in cartProducts) {
                cartDto.quantity += cartProduct.quantity
                cartDto.totalPrice += if (cartProduct.totalPrice > 0) cartProduct.totalPrice else ((cartProduct.product?.price
                    ?: 0.0f) * (cartProduct.quantity)).toDouble()
            }

            cartDto
        } catch (e: Exception) {
            e.printStackTrace()
            createNewCart(userId)
        }
    }

    override suspend fun createNewCart(userId: String): CartDto {
        val cart = Cart(
            cartId = IdentifyCreator.create(),
            userId = userId,
            quantity = 0,
            totalPrice = 0.0,
            status = CartStatus.ACTIVE.value,
        ).apply { isNewCart = true }
        val savedCart = cartRepository.save(cart)
        val cartDto = getCartById(cartId = savedCart.cartId)
        return cartDto
    }

    override suspend fun getCountItemActiveCart(userId: String): Int {
        return try {
            val activeCart = getActiveCart(userId)
            cartProductRepository.findCartProductByCartId(activeCart.cartId).toList().size
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            0
        }
    }

    override suspend fun createCartByStatus(userId: String, status: String): CartDto {
        val processingCarts = cartRepository.findCartByUserIdAndStatus(userId, CartStatus.PROCESSING.value).toList()
        for (cart in processingCarts) {
            cartRepository.deleteById(cart.cartId)
        }
        val cart = Cart(
            cartId = IdentifyCreator.create(),
            userId = userId,
            quantity = 0,
            totalPrice = 0.0,
            status = when (status) {
                CartStatus.ACTIVE.value -> CartStatus.ACTIVE.value
                CartStatus.PROCESSING.value -> CartStatus.PROCESSING.value
                else -> CartStatus.COMPLETED.value
            },
        ).apply { isNewCart = true }
        cartRepository.save(cart)
        return getCartById(cartId = cart.cartId)
    }

    override suspend fun getCartById(cartId: String): CartDto {
        val cart = cartRepository.findById(cartId) ?: throw NotFoundException("Not found cart $cartId")
        val totalPrice = 0.0
        var quantity = 0
        val cartProducts: List<CartProductDto> = getCartProducts(cartId)
        if (cart.cartProducts.isNotEmpty()) {
            for (cartProduct in cart.cartProducts) {
                quantity += cartProduct.quantity
            }
        }
        return CartDto(
            userId = cart.userId,
            cartId = cart.cartId,
            totalPrice = totalPrice,
            quantity = quantity,
            status = cart.status,
            cartProducts = cartProducts
        )
    }

    override suspend fun getOrderCartById(cartId: String, supplierId: String?): OrderCartDto {
        val cart = cartRepository.findById(cartId) ?: throw NotFoundException("Not found cart $cartId")
        val cartProducts = getOrderCartProducts(cart.cartId, supplierId)
        val orderCartDto = OrderCartDto(
            userId = cart.userId,
            cartId = cart.cartId,
            totalPrice = 0.0,
            quantity = 0,
            status = cart.status,
            cartProducts = cartProducts
        )

        for (cartProduct in cartProducts) {
            orderCartDto.quantity += cartProduct.quantity
            if (cartProduct.purchasePrice == null) {
                orderCartDto.totalPrice += (cartProduct.product?.price ?: 0.0f) * (cartProduct.quantity)
            } else {
                orderCartDto.totalPrice += (cartProduct.purchasePrice * 1.0) * (cartProduct.quantity)
            }
        }
        return orderCartDto
    }

    override suspend fun upsertUserProductByUserAndSupplier(userId: String, supplierId: String, cartId: String): List<String> {
        val cartProductsOfCart = cartProductRepository.findCartProductByCartId(cartId).toList()
        val listProductId = cartProductsOfCart.map { it.productId }
        return productService.upsertUserProductByUserAndSupplier(UpsertListUserProductDto(
            userId = userId,
            supplierId = supplierId,
            productIds = listProductId
        ))
    }

    override suspend fun getCartProducts(cartId: String): List<CartProductDto> {
        val cartProducts: MutableList<CartProductDto> = mutableListOf()
        val cartProductsOfCart = cartProductRepository.findCartProductByCartId(cartId).toList()
        for (cartProduct in cartProductsOfCart) {
            cartProducts.add(cartProduct.toCartProductDto(productService.getProductInfo(cartProduct.productId)))
        }
        return cartProducts.toList()
    }

    override suspend fun getOrderCartProducts(cartId: String, supplierId: String?): List<OrderCartProductDto> {
        val cartProductsOfCart = cartProductRepository.findCartProductByCartId(cartId).toList()
        // get product by ids and filter by supplier => maybe length of listOrderProductInfo less than length of cartProductsOfCart
        val listOrderProductInfo =
            productService.getListOrderProductInfoByIds(cartProductsOfCart.map { it.productId }, supplierId)
        return listOrderProductInfo.map { orderProductInfo ->
            cartProductsOfCart.first {
                it.productId == orderProductInfo.productId
            }.toOrderCartProductDto(orderProductInfo)
        }
    }

    override suspend fun updateProductInActiveCart(
        userId: String,
        upsertCartProductDto: UpsertCartProductDto
    ): CartDto =
        coroutineScope {
//            if (upsertCartProductDto.cartProductId == null) throw BadRequestException("Require cart product id")
            val activeCart = getActiveCart(userId)
            val deferred = awaitAll(
                async {
                    cartProductRepository.findCartProductByCartIdAndProductId(
                        activeCart.cartId,
                        upsertCartProductDto.productId
                    )
                },
                async {
                    try {
                        productService.getProductInfo(upsertCartProductDto.productId)
                    } catch (e: Exception) {
                        throw NotFoundException("Not found product ${upsertCartProductDto.productId}")
                    }
                }
            )

            val cartProduct = deferred[0] as CartProduct?
            val productInfo = deferred[1] as ProductInfoDto

            if (cartProduct == null) {
                if (upsertCartProductDto.quantity > productInfo.amount) throw BadRequestException("Not enough product. Total product: ${productInfo.amount}")
                val newCartProduct = upsertCartProductDto.toCartProduct(activeCart.cartId)
                activeCart.totalPrice += newCartProduct.quantity * productInfo.price
                activeCart.quantity += newCartProduct.quantity

                joinAll(
                    launch {
                        cartProductRepository.save(newCartProduct)
                    },
                    launch {
                        cartRepository.save(activeCart.toCart())
                    }
                )
            } else {
                cartProduct.quantity += upsertCartProductDto.quantity
                if (cartProduct.quantity > productInfo.amount) throw BadRequestException("Not enough product. Total product: ${productInfo.amount}")
                activeCart.totalPrice += upsertCartProductDto.quantity * productInfo.price
                activeCart.quantity += upsertCartProductDto.quantity

                if (cartProduct.quantity >= 0) {
                    joinAll(
                        launch {
                            cartProductRepository.save(cartProduct)
                        },
                        launch {
                            cartRepository.save(activeCart.toCart())
                        }
                    )
                }
            }

            CartDto(
                cartId = activeCart.cartId,
                totalPrice = activeCart.totalPrice,
                quantity = activeCart.quantity,
                status = activeCart.status,
                cartProducts = getCartProducts(activeCart.cartId)
            )
        }

    override suspend fun deleteCartProduct(userId:String,cartId: String, productId: String): CartDto = coroutineScope {
        val deferred = awaitAll(
            async {
                cartRepository.findById(cartId) ?: throw NotFoundException("Not found cart $cartId")
            },
            async {
                cartProductRepository.findCartProductByCartIdAndProductId(cartId, productId) ?: throw NotFoundException(
                    "Not found product $productId in cart"
                )
            },
        )

        val cart = deferred[0] as Cart
        val cartProduct = deferred[1] as CartProduct
        val productInfo = productService.getProductInfo(cartProduct.productId)

        cartProductRepository.deleteById(cartProduct.cartProductId)

        cart.quantity -= cartProduct.quantity
        cart.totalPrice -= (cartProduct.quantity * productInfo.price)
        cartRepository.save(cart)

        getActiveCart(userId = userId)
    }

    override suspend fun getProcessingCart(userId: String): OrderCartDto {
        val processingCarts = cartRepository.findCartByUserIdAndStatus(userId, CartStatus.PROCESSING.value).toList()
            .takeIf { it.isNotEmpty() } ?: throw NotFoundException("Not found processing cart for user $userId")
        val processingCart = processingCarts.first()
        val cartProducts = getOrderCartProducts(processingCart.cartId)
        val orderCartDto = OrderCartDto(
            userId = processingCart.userId,
            cartId = processingCart.cartId,
            totalPrice = 0.0,
            quantity = 0,
            status = processingCart.status,
            cartProducts = cartProducts
        )

        for (cartProduct in cartProducts) {
            orderCartDto.quantity += cartProduct.quantity
            orderCartDto.totalPrice += (cartProduct.product?.price ?: 0.0f) * (cartProduct.quantity)
        }
        return orderCartDto
    }

    override suspend fun createProcessingCart(userId: String, cartProducts: List<CartProductDto>): CartDto {
        val processingCart = createCartByStatus(userId, CartStatus.PROCESSING.value)
        val cartProductsOfProcessingCart = mutableListOf<CartProductDto>()
        for (cartProduct in cartProducts) {
            val cartProductOfProcessingCart = CartProductDto(
                cartProductId = IdentifyCreator.create(),
                cartId = processingCart.cartId,
                totalPrice = ((cartProduct.product?.price ?: 0.0f) * cartProduct.quantity).toDouble(),
                quantity = cartProduct.quantity,
                product = cartProduct.product
            )
            cartProductRepository.save(cartProductOfProcessingCart.toCartProduct().apply { isNewCartProduct = true })
            cartProductsOfProcessingCart.add(
                cartProductOfProcessingCart
            )
            processingCart.totalPrice += cartProductOfProcessingCart.totalPrice
        }

        processingCart.quantity = cartProducts.size
        processingCart.cartProducts = cartProductsOfProcessingCart
        cartRepository.save(processingCart.toCart())

        return processingCart
    }

    override suspend fun deleteProcessingCart(userId: String): Boolean {
        val processingCart = try {
            cartRepository.findCartByUserIdAndStatus(userId, CartStatus.PROCESSING.value).toList()[0]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return if (processingCart != null) {
            cartRepository.deleteById(processingCart.cartId)
            true
        } else {
            true
        }
    }

    override suspend fun checkoutProcessingCart(userId: String) = coroutineScope {
        val processingCart = try {
            cartRepository.findCartByUserIdAndStatus(userId, CartStatus.PROCESSING.value).toList()[0]
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        val purchaseCartProducts = cartProductRepository.findCartProductByCartId(processingCart.cartId).toList()
        for(purchaseCartProduct in purchaseCartProducts){
            val product = productService.getProductInfo(purchaseCartProduct.productId)
            purchaseCartProduct.purchasePrice = product.price * 1.0
            cartProductRepository.save(purchaseCartProduct)
        }

        processingCart.status = CartStatus.COMPLETED.value
        cartRepository.save(processingCart)

        val activeCart = try {
            cartRepository.findCartByUserIdAndStatus(userId, CartStatus.ACTIVE.value).toList()[0]
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

//val list =
//            upsertUserProductByUserAndSupplier(userId,"",processingCart.cartId)
//        }.join()

        cartProductRepository.findCartProductByCartId(processingCart.cartId).map {
            launch {
                cartProductRepository.deleteCartProductByCartIdAndProductId(activeCart.cartId, it.productId)
            }
        }.toList().joinAll()
    }

    override suspend fun getCartProductsByCartId(cartId: String): Flow<CartProduct> {
        return cartProductRepository.findCartProductByCartId(cartId)
    }

}