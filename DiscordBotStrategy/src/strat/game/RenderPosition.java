package strat.game;

public class RenderPosition implements ISerializable {
	private String name;
	
	private int q;
	private int r;
	
	private int hexScaleX;
	private int hexScaleY;
	
	public RenderPosition(String name, int q, int r, int hexScaleX, int hexScaleY) {
		this.name = name;
		this.q = q;
		this.r = r;
		this.hexScaleX = hexScaleX;
		this.hexScaleY = hexScaleY;
	}
	
	public RenderPosition(String serializedData) {
		String[] data = serializedData.split(",");
		
		name = data[0];
		q = Integer.parseInt(data[1]);
		r = Integer.parseInt(data[2]);
		hexScaleX = Integer.parseInt(data[3]);
		hexScaleY = Integer.parseInt(data[4]);
	}
	
	public String getName() {
		return name;
	}
	
	public int getQ() {
		return q;
	}
	
	public int getR() {
		return r;
	}
	
	public int getHexScaleX() {
		return hexScaleX;
	}
	
	public int getHexScaleY() {
		return hexScaleY;
	}
	
	@Override
	public String serialize() {
		return String.format("RenderPos,%s,%d,%d,%d,%d", name, q, r, hexScaleX, hexScaleY);
	}

}
