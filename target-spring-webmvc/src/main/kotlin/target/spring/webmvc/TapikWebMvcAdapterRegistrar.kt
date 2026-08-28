package dev.akif.tapik.target.spring.webmvc

import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.core.Ordered
import org.springframework.core.PriorityOrdered
import org.springframework.util.ClassUtils

internal class TapikWebMvcAdapterRegistrar(
    private val descriptors: () -> List<TapikWebMvcAdapterDescriptor> = ::tapikWebMvcAdapterDescriptors
) : BeanDefinitionRegistryPostProcessor, PriorityOrdered {
    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val beanFactory = registry as? ConfigurableListableBeanFactory
            ?: error("Tapik WebMVC registration requires a configurable bean factory")
        descriptors().forEach { descriptor -> beanFactory.register(descriptor, registry) }
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) = Unit
}

private fun ConfigurableListableBeanFactory.register(
    descriptor: TapikWebMvcAdapterDescriptor,
    registry: BeanDefinitionRegistry
) {
    val handlerType = ClassUtils.forName(descriptor.handlerType, beanClassLoader)
    val candidates =
        BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
            this,
            handlerType,
            true,
            false
        )
    val selected =
        when {
            candidates.size == 1 -> candidates.single()
            else -> candidates.singleOrNull(::isPrimary)
        }
    if (selected == null) return

    val adapterType = ClassUtils.forName(descriptor.adapterType, beanClassLoader)
    registry.registerBeanDefinition(
        descriptor.adapterType,
        RootBeanDefinition(adapterType).apply {
            autowireMode = RootBeanDefinition.AUTOWIRE_CONSTRUCTOR
        }
    )
}

private fun ConfigurableListableBeanFactory.isPrimary(beanName: String): Boolean =
    when {
        containsBeanDefinition(beanName) -> getMergedBeanDefinition(beanName).isPrimary
        parentBeanFactory is ConfigurableListableBeanFactory ->
            (parentBeanFactory as ConfigurableListableBeanFactory).isPrimary(beanName)
        else -> false
    }
