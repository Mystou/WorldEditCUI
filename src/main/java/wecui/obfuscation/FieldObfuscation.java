package wecui.obfuscation;

/**
 * Method name obfuscation, used whenever we use reflection. 
 * This lets us keep reflected obfuscated methods in a central
 * class to ease updating.
 * 
 * @author yetanotherx
 * 
 * @obfuscated 1.6.2
 */
public enum FieldObfuscation {

	/**
	 * NetworkManager instance in NetClientHandler.class (netManager -> Reference to the NetworkManager object.)
	 */
	NETWORKMANAGER("g", "field_72555_g"),
	/**
	 * Packet stream list in NetworkManager.class/TcpConnection.class (dataPackets -> Linked list of packets awaiting sending.)
	 */
	PACKETLIST("q", "field_74487_p"),
	/**
	 * ID->Class hashmap for packets in Packet.class (packetIdToClassMap -> Maps packet id to packet class)
	 */
	IDSTOCLASSES("l", "field_73294_l"),
	/**
	 * Class->ID hashmap for packets in Packet.class (packetClassToIdMap -> Maps packet class to packet id)
	 */
	CLASSESTOIDS("a", "field_73291_a");

	protected String variable;
	protected String seargeVariable;
	private static final boolean isDeobfuscatedEnvironment = getDeobfuscatedEnvironment();

	private FieldObfuscation(String variable, String seargeVariable) {
		this.variable = variable;
		this.seargeVariable = seargeVariable;
	}

	private static boolean getDeobfuscatedEnvironment() {
		try {
			Class.forName("net.minecraft.world.World");
			final String pkg = "cpw.mods.fml.common.asm.transformers.deobf";
			Class.forName(pkg + ".FMLDeobfuscatingRemapper").getField("INSTANCE");
			System.out.println("WECUI: Deobfuscated environment detected.");
			return true;
		} catch (Throwable t) {
			return false;
		}    
	}

	public String getVariable() {
		if(isDeobfuscatedEnvironment) {
			return seargeVariable;
		} else {
			return variable;
		}
	}

	public static String getVariable(FieldObfuscation type) {
		return type.getVariable();
	}
}