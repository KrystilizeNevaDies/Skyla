package main.block

import main.SkylaExtension
import main.block.power.powerblocks.*
import net.minestom.server.MinecraftServer
import net.minestom.server.data.Data
import net.minestom.server.data.DataImpl
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.CustomBlock
import java.util.*

object BlockSystem {
    private val blockMap: MutableMap<Block, CustomBlock> = EnumMap(net.minestom.server.instance.block.Block::class.java)
    private val customBlocks: MutableSet<CustomBlock> = LinkedHashSet()

	fun init() {
        val manager = MinecraftServer.getBlockManager()
        for (block in listOf(
            WireBlock,
            ManualGeneratorBlock,
            BatteryBlock,
            PowerDiverterBlock,
            KineticGeneratorBlock
        )) {
            val staticBlock = Block.fromStateId(block.defaultBlockStateId)
            SkylaExtension.LOGGER?.info("Registering static block $staticBlock to custom block: $block")
            blockMap[staticBlock] = block
            customBlocks.add(block)
            manager.registerCustomBlock(block)
        }
        MinecraftServer.getGlobalEventHandler()
            .addEventCallback(PlayerBlockPlaceEvent::class.java) { event: PlayerBlockPlaceEvent ->
                val block = blockMap[Block.fromStateId(event.blockStateId)]
                if (block != null) {
                    val data: Data = DataImpl()
                    event.setCustomBlock(block)
                    event.blockData = data
                    if (block is SkylaCustomBlock) event.blockStateId =
                        block.blockStateOnPlacement(event.player, event.blockPosition, data)
                }
            }
    }

    /**
     * Slow method of retrieving a truly unique ID for each custom block.
     * @param customBlock
     * @return unique id
     */
	fun getCustomBlockId(customBlock: CustomBlock): Short {
        var id: Short = 1
        for (block in customBlocks) {
            if (block === customBlock) return id
            id++
        }
        Throwable("Custom Block: $customBlock not found in block system.").printStackTrace()
        return 0
    }
}