package com.sudoo.productservice.mapper

import com.sudoo.domain.utils.IdentifyCreator
import com.sudoo.productservice.dto.*
import com.sudoo.productservice.ktx.orZero
import com.sudoo.productservice.model.OrderProductInfo
import com.sudoo.productservice.model.Product
import com.sudoo.productservice.model.ProductExtras
import com.sudoo.productservice.model.ProductInfo
import java.time.LocalDateTime

fun UpsertProductDto.toProduct(supplierId: String, createAt: LocalDateTime? = null): Product {
    return Product(
        productId = IdentifyCreator.createOrElse(productId),
        supplierId = supplierId,
        sku = IdentifyCreator.genProductSkuOrElse(sku, brand = brand!!, nameProduct = name!!),
        name = name,
        description = description!!,
        brand = brand,
        price = price ?: 0.0f,
        listedPrice = listedPrice ?: 0.0f,
        amount = amount!!,
        soldAmount = soldAmount ?: 0,
        rate = 5.0f,
        totalRateAmount = 0,
        discount = discount ?: 0,
        startDateDiscount = startDateDiscount,
        endDateDiscount = endDateDiscount,
        saleable = saleable ?: true,
        weight = weight.orZero(),
        height = height.orZero(),
        length = length.orZero(),
        width = width.orZero(),
        createdAt = createAt ?: LocalDateTime.now()
    ).also {
        it.isNewProduct = productId.isNullOrEmpty()
    }
}

fun UpsertProductDto.combineProduct(product: Product): Product {
    return Product(
        productId = productId ?: product.productId,
        supplierId = product.supplierId,
        sku = sku ?: product.sku,
        name = name ?: product.name,
        description = description ?: product.description,
        brand = brand ?: product.brand,
        price = price ?: product.price,
        listedPrice = listedPrice ?: product.listedPrice,
        amount = amount ?: product.amount,
        soldAmount = soldAmount ?: product.amount,
        rate = product.rate,
        totalRateAmount = product.totalRateAmount,
        discount = discount ?: product.discount,
        startDateDiscount = startDateDiscount ?: product.startDateDiscount,
        endDateDiscount = endDateDiscount ?: product.endDateDiscount,
        saleable = saleable ?: product.saleable,
        weight = weight ?: product.weight,
        height = height ?: product.height,
        length = length ?: product.length,
        width = width ?: product.width,
        createdAt = product.createdAt,
    )
}

fun UpsertProductExtrasDto.combineProductExtra(extras: ProductExtras): ProductExtras {
    return ProductExtras(
        productId = extras.productId,
        enable3DViewer = enable3DViewer ?: extras.enable3DViewer,
        enableArViewer = enableArViewer ?: extras.enableArViewer,
        source = source
    )
}

fun Product.toProductDto(supplierInfo: SupplierInfoDto, extras: ProductExtrasDto?): ProductDto {
    return ProductDto(
        productId = productId,
        supplierId = supplierId,
        sku = sku,
        name = name,
        description = description,
        brand = brand,
        price = price,
        listedPrice = listedPrice,
        amount = amount,
        soldAmount = soldAmount,
        rate = rate,
        discount = discount,
        startDateDiscount = startDateDiscount,
        endDateDiscount = endDateDiscount,
        saleable = saleable,
        weight = weight,
        height = height,
        length = length,
        width = width,
        supplier = supplierInfo,
        extras = extras ?: ProductExtrasDto(),
        categories = categories?.map { it.toCategoryInfoDto() },
        images = images?.map { it.toImageDto() },
    )
}

//fun Product.toUpsertProductDto(categoryIds: List<String>? = null, images: List<String>? = null): UpsertProductDto {
//    return UpsertProductDto(
//        productId,
//        sku,
//        name,
//        description,
//        price,
//        listedPrice,
//        amount,
//        soldAmount,
//        discount,
//        startDateDiscount,
//        endDateDiscount,
//        saleable,
//        categoryIds = categoryIds,
//        images = images
//    )
//}

fun ProductInfo.toProductInfoDto(): ProductInfoDto {
    return ProductInfoDto(
        productId = productId,
        supplierId = supplierId,
        sku = sku,
        name = name,
        price = price,
        amount = amount,
        listedPrice = listedPrice,
        discount = discount,
        startDateDiscount = startDateDiscount,
        endDateDiscount = endDateDiscount,
        brand = brand,
        rate = rate,
        saleable = saleable,
        images = images,
    )
}

fun OrderProductInfo.toOrderProductDto(): OrderProductInfoDto {
    return OrderProductInfoDto(
        productId = productId,
        supplierId = supplierId,
        sku = sku,
        name = name,
        price = price,
        weight = weight,
        height = height,
        length = length,
        width = width,
        brand = brand,
        images = images,
    )
}

fun UpsertProductExtrasDto?.toProductExtras(productId: String, isNew: Boolean): ProductExtras {
    return if (this == null) {
        ProductExtras(
            productId = productId,
            enable3DViewer = false,
            enableArViewer = false,
            source = null,
        )
    } else {
        ProductExtras(
            productId = productId,
            enable3DViewer = enable3DViewer ?: false,
            enableArViewer = enableArViewer ?: false,
            source = source,
        )
    }.also {
        it.isNewExtras = isNew
    }
}

fun ProductExtras.toProductExtrasDto(): ProductExtrasDto {
    return ProductExtrasDto(
        enable3DViewer = enable3DViewer,
        enableArViewer = enableArViewer,
        source = source,
    )
}