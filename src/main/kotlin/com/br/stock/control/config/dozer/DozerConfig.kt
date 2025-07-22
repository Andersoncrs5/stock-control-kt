package com.br.stock.control.config.dozer

import com.github.dozermapper.core.DozerBeanMapperBuilder
import com.github.dozermapper.core.Mapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DozerConfig {

    @Bean
    fun dozerMapper(): Mapper {
        return DozerBeanMapperBuilder.buildDefault()
    }
}