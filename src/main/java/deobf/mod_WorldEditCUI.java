package deobf;

import java.nio.charset.Charset;
import wecui.WorldEditCUI;
import wecui.render.RenderEntity;
import wecui.render.RenderHooks;
import java.util.Map;

import deobf.Minecraft;
import wecui.Updater;
import wecui.event.ChannelEvent;
import wecui.obfuscation.DataPacketList;
import wecui.obfuscation.Obfuscation;
import wecui.render.region.CuboidRegion;

/**
 * Main ModLoader class. Initializes the mod, enabling CUI communication 
 * between server and client, in addition to enabling rendering.
 * 
 * TODO: Move Configuration.yml to WEConfig.yml
 * 
 * @author lahwran
 * @author yetanotherx
 */
public class mod_WorldEditCUI extends BaseMod {

	protected WorldEditCUI controller;
	protected WorldClient lastWorld;
	protected EntityPlayerSP lastPlayer;
	protected Entity lastEntity;
	protected boolean gameStarted = false;
	public final static Charset UTF_8_CHARSET = Charset.forName("UTF-8");
	protected int entityUpdateTickCount = 0;

	private final String mcfTopic = "https://github.com/Mystou/WorldEditCUI";

	public mod_WorldEditCUI() {
	}

	@Override
	public void load() {
		this.controller = new WorldEditCUI(ModLoader.getMinecraftInstance());
		this.controller.initialize();

		//ModLoader.registerEntityID(RenderEntity.class, "CUI", ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(RenderEntity.class, "CUI", 110); // use ID 110, hope it's unique

		ModLoader.setInGameHook(this, true, true);
		ModLoader.registerPacketChannel(this, "WECUI");
	}

	/**
	 * Checks if the world or player has changed from the last time we checked.
	 * If it's changed, spawn a new render entity and update accordingly.
	 * 
	 * It also checks if initialization tasks have been done, such as checking
	 * for updates, resetting the region, and registering reflection for the
	 * outgoing command handler.
	 * 
	 * @param partialticks
	 * @param mc
	 * @return 
	 */
	@Override
	public boolean onTickInGame(float partialticks, Minecraft mc) {

		if (Obfuscation.getWorld(mc) != lastWorld || Obfuscation.getPlayer(mc) != lastPlayer) {
			lastEntity = controller.getObfuscation().spawnEntity();
			lastWorld = Obfuscation.getWorld(mc);
			lastPlayer = Obfuscation.getPlayer(mc);

			if (!gameStarted) {
				gameStarted = true;

				new Updater(controller).start();
				this.controller.setSelection(new CuboidRegion(controller));
				//new EntityUpdateThread(this).start();

				DataPacketList.register(controller);
			}
		} else {
			if( this.entityUpdateTickCount > 1000 ) {
				this.entityUpdateTickCount = 0;
				if( lastEntity != null ) {
					Obfuscation.setEntityPositionToPlayer(mc, lastEntity);
				}
			} else {
				++this.entityUpdateTickCount;
			}
		}
		return true;
	}

	/**
	 * Called when the client receives a CUI packet from the server. 
	 * @param handler
	 * @param packet 
	 */
	@Override
	public void clientCustomPayload(NetClientHandler handler, Packet250CustomPayload packet) {
		ChannelEvent channelevent = new ChannelEvent(controller, new String(Obfuscation.getBytesFromPacket(packet), UTF_8_CHARSET));
		controller.getEventManager().callEvent(channelevent);
	}

	/**
	 * Called when the client connects to a server. Sends the protocol version 
	 * in a channel message to the server.
	 * @param handler 
	 */
	@Override
	public void clientConnect(NetClientHandler handler) {
		byte[] buffer = ("v|" + WorldEditCUI.protocolVersion).getBytes(UTF_8_CHARSET);
		ModLoader.clientSendPacket(Obfuscation.newPayloadPacket("WECUI", buffer.length, buffer));
	}

	/**
	 * Tells the renderer that all RenderEntity's should be rendered with the
	 * RenderHooks class.
	 * @param map 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void addRenderer(Map map) {
		map.put(RenderEntity.class, new RenderHooks(controller));
	}

	@Override
	public String getVersion() {
		return WorldEditCUI.getVersion();
	}

	@Override
	public String getName()
	{
		return "WorldEdit CUI";
	}

	@Override
	public String getPriorities()
	{
		return "before:*";
	}
}
