package net.morematerials.handlers;

import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;


public class CommandHandler extends GenericHandler {

	@Override
	public void init(MoreMaterials instance) {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		// Get the player for this event.
		Player player;
		try {
			Method method = event.getClass().getMethod("getPlayer");
			player = (Player) method.invoke(event);
		} catch (Exception exception) {
			return;
		}
		
		// Call the chat command :D
		if (config.containsKey("Command")) {
			player.chat((String) config.get("Command"));
		}
	}

}