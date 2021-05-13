package main.commands;

import static net.minestom.server.command.builder.arguments.ArgumentType.RelativeBlockPosition;
import static net.minestom.server.command.builder.arguments.ArgumentType.Word;

import java.util.List;
import java.util.stream.Collectors;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.data.DataImpl;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.location.RelativeBlockPosition;

public class PlaceBlockCommand extends Command {

	public PlaceBlockCommand() {
		super("placeblock");
		
		 List<String> blocks = MinecraftServer.getBlockManager().getCustomBlocks()
			.stream()
			.map(block -> block.getIdentifier())
			.collect(Collectors.toList());
		
		addSyntax(PlaceBlockCommand::standardUsage,
			RelativeBlockPosition("position"),
			Word("block")
				.from(blocks.toArray(String[]::new))
		);
	}
	
	private static void standardUsage(CommandSender sender, CommandContext context) {
		Player player = sender.asPlayer();
		
		RelativeBlockPosition relPosition = context.get("position");
		String block = context.get("block");
		BlockPosition position = relPosition.from(player);
		
		
		player.getInstance().setCustomBlock(position.getX(), position.getY(), position.getZ(), block, new DataImpl());
	}
}
