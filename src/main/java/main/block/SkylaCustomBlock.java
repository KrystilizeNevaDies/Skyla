package main.block;

import java.util.List;

import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.utils.BlockPosition;

public abstract class SkylaCustomBlock extends CustomBlock {

    private final Block baseBlock;
    private final BlockPropertyList properties;
    private final BlockStates blockStates;
    private final BlockState baseBlockState;
	
    public SkylaCustomBlock(short baseBlockID, String blockID) {
		super(baseBlockID, blockID);
		
        this.baseBlock = Block.fromStateId(baseBlockID);
        this.properties = createPropertyValues();
		
		
		// create block states
        this.blockStates = new BlockStates(properties);
        List<String[]> allVariants = properties.getCartesianProduct();
        
        if(allVariants.isEmpty()) {
            short id = baseBlock.getBlockId();
            BlockState state = new BlockState(id, blockStates);
            blockStates.add(state);
        } else {
            for(String[] variant : allVariants) {
                short id = baseBlock.withProperties(variant);
                BlockState blockState = new BlockState(id, blockStates, variant);
                blockStates.add(blockState);
            }
        }
        
        baseBlockState = blockStates.getDefault();
	}

    public BlockState getBaseBlockState() {
    	return baseBlockState;
    }
    
	protected BlockPropertyList createPropertyValues() {
		return new BlockPropertyList();
	}

	public short blockStateOnPlacement(Player player, BlockPosition position, Data data) {
		return getBaseBlockState().getBlockId();
	}
	
	@Override
	public short getCustomBlockId() {
		return BlockSystem.getCustomBlockId(this);
	}
}
