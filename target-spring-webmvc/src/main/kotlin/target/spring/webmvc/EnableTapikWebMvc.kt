package dev.akif.tapik.target.spring.webmvc

import org.springframework.context.annotation.Import

/**
 * Registers generated Tapik WebMVC adapters in a Spring application context.
 *
 * This does not enable or configure Spring WebMVC itself.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(TapikWebMvcAdapterRegistrar::class)
public annotation class EnableTapikWebMvc
