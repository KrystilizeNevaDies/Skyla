package main.block

import net.minestom.server.instance.block.Block

class BlockState(val blockId: Short, private val parent: BlockStates, vararg propertyList: String) {
    private val properties: Map<String, String> = propertyList.associate {
        val parts = it.split('=')

        parts[0] to parts[1]
    }

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
        return parent.getStateWithChange(properties, key, value)
    }

    override fun toString(): String {
        val props =
            properties.entries.
            joinToString(",") { e -> e.key.toString() + "=" + e.value }
        return Block.fromStateId(blockId).toString() + "{$props}"
    }
}