package main.block.power;

import java.util.HashMap;
import java.util.Map;

import net.minestom.server.data.Data;
import net.minestom.server.utils.BlockPosition;

public class PowerBlock {
	
	public static final String POWER_KEY = "power";
	
	private static Map<BlockPosition, PowerBlock> references = new HashMap<>();
	private static Map<Class<? extends CustomPowerBlock>, Double> maxPower = new HashMap<>();
	private Data data;
	private BlockPosition position;
	private Class<? extends CustomPowerBlock> clazz;
	
	public static void registerPowerBlock(Class<? extends CustomPowerBlock> clazz, Double maxPower) {
		PowerBlock.maxPower.put(clazz, maxPower);
	}
	
	public static PowerBlock of(BlockPosition position, Data data, Class<? extends CustomPowerBlock> clazz) {
		if (references.containsKey(position))
			return references.get(position);
		
		PowerBlock returnReference = new PowerBlock(position, data, clazz);
		
		references.put(position, returnReference);
		
		return returnReference;
	}
	
	private PowerBlock(BlockPosition position, Data data, Class<? extends CustomPowerBlock> clazz) {
		this.data = data;
		this.position = position;
		this.clazz = clazz;
		data.set(POWER_KEY, 0.0);
	}

	public double getPower() {
		return data.get(POWER_KEY);
	}
	
	public BlockPosition getPosition() {
		return position;
	}
	
	public double getMaxPower() {
		return PowerBlock.maxPower.get(clazz);
	}
	
	public void setPower(double power) {
		data.set(POWER_KEY, power);
	}

	public Class<? extends CustomPowerBlock> getBlockClass() {
		return clazz;
	}
}
