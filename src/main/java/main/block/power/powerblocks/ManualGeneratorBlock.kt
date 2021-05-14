package main.block.power.powerblocks

import main.block.power.CustomPowerBlock
import main.block.power.PowerBlock
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.entity.Player.Hand
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition

object ManualGeneratorBlock : CustomPowerBlock(Block.BELL, "manual_generator", 100.0) {
    override fun onInteract(
        player: Player, hand: Hand, blockPosition: BlockPosition,
        data: Data?
    ): Boolean {
        if (player.isSneaking) return false
        val block = PowerBlock.of(blockPosition, data ?: return false, this.javaClass)
        block.power = (block.power + 5.0).coerceAtMost(block.maxPower)
        return super.onInteract(player, hand, blockPosition, data)
    }
}