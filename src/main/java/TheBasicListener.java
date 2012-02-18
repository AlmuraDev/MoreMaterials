
import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.handlers.GenericHandler;
import org.bukkit.Location;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TheBasicListener extends GenericHandler{

	@Override
	public void onActivation(Location location, SpoutPlayer player) {
	}

	@Override
	public void init(Main instance) {
		System.out.println("Successfully initialized for "+this.getMaterialType());
	}

	@Override
	public void shutdown() {
	}

}
