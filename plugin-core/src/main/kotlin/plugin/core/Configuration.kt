package dev.akif.tapik.plugin.core

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
class ListConfigurationValue(
    values: List<ConfigurationValue>
) : ConfigurationValue {
    val values: List<ConfigurationValue> = values.snapshotList()

    operator fun component1(): List<ConfigurationValue> = values

    fun copy(values: List<ConfigurationValue> = this.values): ListConfigurationValue =
        ListConfigurationValue(values)

    override fun equals(other: Any?): Boolean =
        this === other || other is ListConfigurationValue && values == other.values

    override fun hashCode(): Int = values.hashCode()

    override fun toString(): String = "ListConfigurationValue(values=$values)"
}

/**
 * A named target configuration object.
 *
 * @property values child values keyed by name in declaration order.
 */
class ObjectConfigurationValue(
    values: Map<String, ConfigurationValue>
) : ConfigurationValue {
    val values: Map<String, ConfigurationValue> = values.snapshotMap()

    init {
        require(values.keys.none(String::isBlank)) { "Configuration names must not be blank" }
    }

    operator fun component1(): Map<String, ConfigurationValue> = values

    fun copy(values: Map<String, ConfigurationValue> = this.values): ObjectConfigurationValue =
        ObjectConfigurationValue(values)

    override fun equals(other: Any?): Boolean =
        this === other || other is ObjectConfigurationValue && values == other.values

    override fun hashCode(): Int = values.hashCode()

    override fun toString(): String = "ObjectConfigurationValue(values=$values)"
}

/**
 * Host-neutral configuration supplied to one target execution.
 *
 * @property values root configuration values keyed by name.
 * @throws IllegalArgumentException when a configuration name is blank.
 */
class TargetConfiguration(
    values: Map<String, ConfigurationValue> = emptyMap()
) {
    val values: Map<String, ConfigurationValue> = values.snapshotMap()

    init {
        require(values.keys.none(String::isBlank)) { "Configuration names must not be blank" }
    }

    /** Returns the value named [name], or `null` when it is absent. */
    operator fun get(name: String): ConfigurationValue? = values[name]

    operator fun component1(): Map<String, ConfigurationValue> = values

    fun copy(values: Map<String, ConfigurationValue> = this.values): TargetConfiguration =
        TargetConfiguration(values)

    override fun equals(other: Any?): Boolean =
        this === other || other is TargetConfiguration && values == other.values

    override fun hashCode(): Int = values.hashCode()

    override fun toString(): String = "TargetConfiguration(values=$values)"
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
