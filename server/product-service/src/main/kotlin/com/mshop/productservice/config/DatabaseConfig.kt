package com.mshop.productservice.config

import com.mshop.config.converter.InstantToLocalDateConverter
import com.mshop.config.converter.InstantToLocalDateTimeConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.MySqlDialect
import com.mshop.config.converter.LocalDateTimeToInstantConverter
import com.mshop.config.converter.LocalDateToInstantConverter
import org.springframework.context.annotation.Bean

@Configuration
class DatabaseConfig {

    @Bean
    fun customConversion(): R2dbcCustomConversions {
        return R2dbcCustomConversions.of(
            MySqlDialect.INSTANCE,
            mutableListOf(
                com.mshop.config.converter.LocalDateTimeToInstantConverter,
                com.mshop.config.converter.InstantToLocalDateTimeConverter,
                com.mshop.config.converter.LocalDateToInstantConverter,
                com.mshop.config.converter.InstantToLocalDateConverter
            )
        )
    }
}