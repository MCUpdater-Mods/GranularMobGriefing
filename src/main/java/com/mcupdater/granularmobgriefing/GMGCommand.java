package com.mcupdater.granularmobgriefing;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class GMGCommand {
	public static SuggestionProvider<CommandSourceStack> ACTION_SUGGESTION = SuggestionProviders.register(
			ResourceLocation.fromNamespaceAndPath(GranularMobGriefing.MODID,"actions"),
			(context, builder) -> SharedSuggestionProvider.suggest(new String[]{"add", "remove"}, builder)
	);

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		dispatcher.register(
				Commands.literal("gmg")
						.then(Commands.literal("list")
								.executes(commandContext -> {
									commandContext.getSource().sendSystemMessage(Component.literal("Registered entities:"));
									Config.entities.stream().forEach(entityType -> commandContext.getSource().sendSystemMessage(Component.literal(EntityType.getKey(entityType).toString())));
									return 1;
								})
						)
						.then(
								Commands.argument("action", StringArgumentType.string())
										.suggests(ACTION_SUGGESTION)
										.requires(issuer -> issuer.hasPermission(2))
										.then(
												Commands.argument("entity", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
														.suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
														.executes(
																commandContext -> {
																	String action = commandContext.getArgument("action", String.class);
																	if (action.equals("add")) {
																		Config.add(ResourceArgument.getSummonableEntityType(commandContext, "entity").value());
																		commandContext.getSource().sendSuccess(() -> Component.translatable("commands.gmg.add.success"), true);
																		return 1;
																	} else if (action.equals("remove")) {
																		Config.remove(ResourceArgument.getSummonableEntityType(commandContext, "entity").value());
																		commandContext.getSource().sendSuccess(() -> Component.translatable("commands.gmg.remove.success"), true);
																		return 1;
																	}
																	commandContext.getSource().sendFailure(Component.translatable("commands.gmg.failure"));
																	return 0;
																}
														)
										)
						)
		);
	}
}
