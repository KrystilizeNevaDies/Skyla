package main.block.power.powerblocks;

import java.util.HashMap;
import java.util.Map;

import net.minestom.server.data.Data;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.utils.BlockPosition;

public class PowerBlock {
	
	public static final String POWER_KEY = "power";
	
	private static Map<Data, PowerBlock> references = new HashMap<>();
	private static Map<Class<? extends CustomBlock>, Double> maxPower = new HashMap<>();
	private Data data;
	private BlockPosition position;
	private Class<? extends CustomBlock> clazz;
	
	public static void registerPowerBlock(Class<? extends CustomBlock> clazz, Double maxPower) {
		PowerBlock.maxPower.put(clazz, maxPower);
	}
	
	public static PowerBlock of(BlockPosition position, Data data, Class<? extends CustomBlock> clazz) {
		if (references.containsKey(data))
			return references.get(data);
		
		PowerBlock returnReference = new PowerBlock(position, data, clazz);
		
		references.put(data, returnReference);
		
		return returnReference;
	}
	
	private PowerBlock(BlockPosition position, Data data, Class<? extends CustomBlock> clazz) {
		this.data = data;
		this.position = position;
		this.clazz = clazz;
		setPower(0);
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
}
