package dev.zornov.repomine.factory

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.inject.InjectionPoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Factory
class LoggerFactoryBean {

    @Prototype
    fun logger(injectionPoint: InjectionPoint<*>): Logger {
        val beanType = injectionPoint.declaringBean.beanType
        return LoggerFactory.getLogger(beanType)
    }
}