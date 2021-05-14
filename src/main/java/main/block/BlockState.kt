package main.block

import net.minestom.server.instance.block.Block
import java.util.*

class BlockState(val blockId: Short, val parent: BlockStates, vararg propertyList: String) {
    val properties: Map<String?, String>

    /**
     * Return the value of the given property key
     * @param key the property key
     * @return the value of the property
     * @throws IllegalArgumentException if the key does not correspond to an existing property
     */
    operator fun get(key: String?): String {
        return properties[key]
            ?: throw IllegalArgumentException("Property '$key' does not exist in blockstate $this")
    }

    /**
     * Returns the block state corresponding to this state with a single property changed
     * @param key the key of the property to change
     * @param value the value of the property
     * @return the corresponding blockstate (they are pooled inside this blockstate's parent BlockStates)
     */
    fun with(key: String?, value: String): BlockState {
        return parent.getStateWithChange(properties, key, value)!! // TODO where is this null coming from
    }

    override fun toString(): String {
        val props =
            properties.entries.
            joinToString(",") { e -> e.key.toString() + "=" + e.value }
        return Block.fromStateId(blockId).toString() + "{$props}"
    }

    init {
        val properties: MutableMap<String?, String> = HashMap()
        for (property in propertyList) {
            val parts = property.split('=')
            val key = parts[0]
            val value = parts[1]
            properties[key] = value
        }
        this.properties = Collections.unmodifiableMap(properties)
    }
}