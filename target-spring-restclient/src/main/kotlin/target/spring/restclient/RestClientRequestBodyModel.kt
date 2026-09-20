package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.Body
import dev.akif.tapik.BodyInput
import dev.akif.tapik.Input
import dev.akif.tapik.NoBody
import dev.akif.tapik.NoInput
import dev.akif.tapik.common.plugin.KotlinSourceType
import dev.akif.tapik.common.plugin.KotlinType
import dev.akif.tapik.common.plugin.argument
import dev.akif.tapik.common.plugin.toKotlinSourceType
import dev.akif.tapik.common.plugin.tupleElements
import dev.akif.tapik.common.plugin.uniqueKotlinName

internal data class RestClientRequestBodyModel(
    val parameterName: String,
    val valueType: KotlinSourceType,
    val alternatives: List<RestClientRequestBodyAlternative>,
    val choiceTypeName: String?,
    val optional: Boolean
) {
    val parameterType: String = choiceTypeName ?: valueType.source
}

internal data class RestClientRequestBodyAlternative(
    val variantName: String,
    val definitionAccess: String
)

internal fun restClientRequestBody(
    inputType: KotlinType,
    input: Input,
    endpointAccess: String,
    endpointId: String,
    parameterNames: MutableSet<String>,
    choiceTypeName: String?,
    variantNames: List<String>
): RestClientRequestBodyModel? {
    if (input is NoInput) return null
    require(input is BodyInput<*>) { "$endpointId has unsupported input '${input::class.simpleName}'" }
    val bodyTypes = inputType.argument(0, "$endpointId input").tupleElements("$endpointId input bodies")
    require(bodyTypes.size == input.bodies.values.size) {
        "$endpointId compiled input body types do not match its runtime bodies"
    }
    val encoded = input.bodies.values.withIndex().filter { (_, alternative) -> alternative is Body<*> }
    if (encoded.isEmpty()) return null
    val valueTypes =
        encoded.map { (index, _) ->
            bodyTypes[index]
                .argument(0, "$endpointId request body")
                .toKotlinSourceType("$endpointId request body")
        }
    require(valueTypes.distinct().size == 1) {
        "$endpointId request body representations must encode one value type"
    }
    val alternatives =
        encoded.mapIndexed { variantIndex, (index, _) ->
            RestClientRequestBodyAlternative(
                variantName = variantNames[variantIndex],
                definitionAccess = "$endpointAccess.input.bodies._${index + 1}"
            )
        }
    return RestClientRequestBodyModel(
        parameterName = uniqueKotlinName("body", parameterNames),
        valueType = valueTypes.first(),
        alternatives = alternatives,
        choiceTypeName = choiceTypeName,
        optional = input.bodies.values.any { alternative -> alternative is NoBody }
    )
}
