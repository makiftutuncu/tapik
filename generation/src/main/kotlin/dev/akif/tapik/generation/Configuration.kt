package dev.akif.tapik.generation

/** A host-neutral value in target configuration. */
sealed interface ConfigurationValue

/**
 * A scalar target configuration [value].
 *
 * @property value textual value supplied by the host adapter.
 */
data class ScalarConfigurationValue(
    val value: String
) : ConfigurationValue

/**
 * An ordered target configuration list.
 *
 * @property values list elements in declaration order.
 */
data class ListConfigurationValue(
    val values: List<ConfigurationValue>
) : ConfigurationValue

/**
 * A named target configuration object.
 *
 * @property values child values keyed by name in declaration order.
 */
data class ObjectConfigurationValue(
    val values: Map<String, ConfigurationValue>
) : ConfigurationValue {
    init {
        require(values.keys.none(String::isBlank)) { "Configuration names must not be blank" }
    }
}

/**
 * Host-neutral configuration supplied to one target execution.
 *
 * @property values root configuration values keyed by name.
 * @throws IllegalArgumentException when a configuration name is blank.
 */
data class TargetConfiguration(
    val values: Map<String, ConfigurationValue> = emptyMap()
) {
    init {
        require(values.keys.none(String::isBlank)) { "Configuration names must not be blank" }
    }

    /** Returns the value named [name], or `null` when it is absent. */
    operator fun get(name: String): ConfigurationValue? = values[name]
}

/**
 * Builds scalar target configuration from [values].
 *
 * @throws IllegalArgumentException when a name is blank or duplicated.
 */
fun targetConfigurationOf(vararg values: Pair<String, String>): TargetConfiguration {
    val names = values.map { it.first }
    require(names.distinct().size == names.size) { "Configuration names must be unique" }
    return TargetConfiguration(values.associate { (name, value) -> name to ScalarConfigurationValue(value) })
}
