package main.block

import main.block.BlockSystem.getCustomBlockId
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.CustomBlock
import net.minestom.server.utils.BlockPosition

abstract class SkylaCustomBlock(baseBlockID: Short, blockID: String?) : CustomBlock(baseBlockID, blockID!!) {
    private val baseBlock: Block = Block.fromStateId(baseBlockID)
    private val properties: BlockPropertyList
    private val blockStates: BlockStates
    val baseBlockState: BlockState
    protected open fun createPropertyValues(): BlockPropertyList {
        return BlockPropertyList()
    }

    open fun blockStateOnPlacement(player: Player, position: BlockPosition, data: Data): Short {
        return baseBlockState.blockId
    }

    override fun getCustomBlockId(): Short {
        return getCustomBlockId(this)
    }

    init {
        properties = createPropertyValues()


        // create block states
        blockStates = BlockStates(properties)
        val allVariants = properties.cartesianProduct
        if (allVariants.isEmpty()) {
            val id = baseBlock.blockId
            val state = BlockState(id, blockStates)
            blockStates.add(state)
        } else {
            for (variant in allVariants) {
                val id = baseBlock.withProperties(*variant)
                val blockState = BlockState(id, blockStates, *variant)
                blockStates.add(blockState)
            }
        }
        baseBlockState = blockStates.default
    }
}