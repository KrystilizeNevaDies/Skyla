package main.block.power

import net.minestom.server.data.Data
import net.minestom.server.utils.BlockPosition
import java.util.*

class PowerBlock(
    val position: BlockPosition,
    private val data: Data,
    val blockClass: Class<out CustomPowerBlock?>
) {
    var power: Double
        get() = data.get(POWER_KEY)!!
        set(power) {
            data.set(POWER_KEY, power)
        }

    val maxPower: Double
        get() = Companion.maxPower[blockClass]!!

    companion object {
        const val POWER_KEY = "power"
        private val references: MutableMap<BlockPosition, PowerBlock> = HashMap()
        private val maxPower: MutableMap<Class<out CustomPowerBlock?>, Double> = HashMap()
        fun registerPowerBlock(clazz: Class<out CustomPowerBlock?>, maxPower: Double) {
            Companion.maxPower[clazz] = maxPower
        }

        fun of(position: BlockPosition, data: Data, clazz: Class<out CustomPowerBlock?>): PowerBlock {
            if (references.containsKey(position)) return references[position]!!
            val returnReference = PowerBlock(position, data, clazz)
            references[position] = returnReference
            return returnReference
        }
    }

    init {
        data.set(POWER_KEY, 0.0)
    }
}