package dev.akif.tapik.format.kotlinx

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleCollector
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
internal fun SerializersModule.hasDefaultProvider(target: KClass<*>): Boolean {
    var found = false
    dumpTo(
        object : SerializersModuleCollector {
            override fun <Value : Any> contextual(
                kClass: KClass<Value>,
                provider: (List<KSerializer<*>>) -> KSerializer<*>
            ) = Unit

            override fun <Base : Any, Sub : Base> polymorphic(
                baseClass: KClass<Base>,
                actualClass: KClass<Sub>,
                actualSerializer: KSerializer<Sub>
            ) = Unit

            override fun <Base : Any> polymorphicDefaultSerializer(
                baseClass: KClass<Base>,
                defaultSerializerProvider: (Base) -> SerializationStrategy<Base>?
            ) {
                if (baseClass == target) found = true
            }

            override fun <Base : Any> polymorphicDefaultDeserializer(
                baseClass: KClass<Base>,
                defaultDeserializerProvider: (String?) -> DeserializationStrategy<Base>?
            ) {
                if (baseClass == target) found = true
            }
        }
    )
    return found
}
