import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;
import net.morematerials.materials.MMCustomBlock;
import net.morematerials.materials.MMCustomItem;
import net.morematerials.materials.MMCustomTool;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.Material;

public class PlaySoundHandler extends GenericHandler {
	
	private MoreMaterials plugin;

	public void init(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void shutdown() {
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		Material material = this.plugin.getSmpManager().getMaterial((Integer) config.get("__materialID__"));
		
		// We can safely cast, because this event is only triggered for MoreMaterials materials!
		String smpName;
		if (material instanceof MMCustomBlock) {
			smpName = ((MMCustomBlock) material).getSmpName();
		} else if (material instanceof MMCustomTool) {
			smpName = ((MMCustomTool) material).getSmpName();
		} else {
			smpName = ((MMCustomItem) material).getSmpName();
		}

		// Default location is the world spawn.
		Location location = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
		if (event instanceof PlayerMoveEvent) {
			location = ((PlayerMoveEvent) event).getPlayer().getLocation();
		}

		// Now play the sound!
		String url = this.plugin.getWebManager().getAssetsUrl(smpName + "_" + (String) config.get("Sound"));
		SpoutManager.getSoundManager().playGlobalCustomSoundEffect(this.plugin, url, false, location, 32, 100);
	}
	
}
