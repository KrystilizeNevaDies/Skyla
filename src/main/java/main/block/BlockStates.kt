package main.block

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition
import java.util.*

class BlockStates(private val properties: BlockPropertyList) {
    private val states: MutableList<BlockState> = LinkedList()

    /**
     * Map that stores a comma-separated list of properties, sorted by alphabetical order as key,
     * and the corresponding block state as value
     */
    private val nameLookup: MutableMap<String, BlockState> = HashMap()
    private val idLookup = Short2ObjectOpenHashMap<BlockState>()

    /**
     * Adds a new blockstate to the known ones
     */
    fun add(blockState: BlockState) {
        states.add(blockState)
        val lookupKey = properties.computeSortedList()
            .joinToString(",") { property -> property.key + "=" + blockState[property.key] }
        nameLookup[lookupKey] = blockState
        idLookup[blockState.blockId] = blockState
    }

    /**
     * Gets a BlockState based on the given properties
     * the value 'value'
     * @param properties
     * @return
     */
    fun getState(properties: Map<String, String>): BlockState? {
        val lookupKey = properties.entries
            .sortedBy { it.key }
            .joinToString(",") { entry -> entry.key + "=" + entry.value }
        return nameLookup[lookupKey]
    }

    /**
     * Gets a BlockState based on its protocol id. Return [.getDefault] if none found
     * @param id
     * @return
     */
    fun fromStateID(id: Short): BlockState {
        return idLookup.getOrDefault(id, default)
    }

    /**
     * Gets a BlockState based on the given properties, with the property corresponding to 'key' being changed to have
     * the value 'value'
     * @param properties
     * @param key
     * @param value
     * @return
     */
    fun getStateWithChange(properties: Map<String, String>, key: String?, value: String): BlockState {
        val lookupKey = properties.entries
            .sortedBy { it.key }
            .map { entry ->
                val prefix = entry.key + "="
                if (entry.key.equals(key, ignoreCase = true)) {
                    return@map prefix + value
                }
                prefix + entry.value
            }
            .joinToString(",")
        return nameLookup[lookupKey] ?: throw Exception("Incorrect block state produced with $properties")
    }

    val default: BlockState
        get() = states[0]

    /**
     * Returns the corresponding BlockState at the given position.
     * Can return null if it does not correspond to any known state.
     * @param instance
     * @param blockPosition
     * @return
     */
    fun getFromInstance(instance: Instance, blockPosition: BlockPosition?): BlockState? {
        val id = instance.getBlockStateId(blockPosition!!)
        val alternative = Block.fromStateId(id).getAlternative(id)
        return getState(alternative.createPropertiesMap())
    }
}