package strat.commands;

public class Response {
	public static final int DEFAULT_COLOR = 0x44bd32;
	
	public ResponseType type;
	public String title;
	public String content;
	public int color;
	
	public Response(ResponseType type, String title, String content, int color) {
		this.type = type;
		this.title = title;
		this.content = content;
		this.color = color;
	}
	
	public Response(ResponseType type, String content) {
		this(type, null, content, DEFAULT_COLOR);
	}
	
	public Response(String content) {
		this(ResponseType.PUBLIC, content);
	}
}
