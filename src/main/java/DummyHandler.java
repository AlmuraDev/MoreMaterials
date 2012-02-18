
import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.handlers.GenericHandler;
import org.bukkit.Location;
import org.getspout.spoutapi.player.SpoutPlayer;

public class DummyHandler extends GenericHandler{

	@Override
	public void onActivation(Location location, SpoutPlayer player) {
		player.sendMessage("DERP");
	}

	@Override
	public void init(Main instance) {
	}

	@Override
	public void shutdown() {
	}

}
