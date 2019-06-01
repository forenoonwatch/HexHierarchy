package strat.bot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Webhook {
	private String url;
	private JSONObject json;
	private JSONObject embed;
	
	public Webhook(String url) {
		this.url = url;
		json = new JSONObject();
		embed = new JSONObject();
		json.put("embeds", new JSONObject[] {embed});
	}
	
	public void setTitle(String title) {
		embed.put("title", title);
	}
	
	public void setDescription(String description) {
		embed.put("description", description);
	}
	
	public void setColor(int color) {
		embed.put("color", Integer.toString(color));
	}
	
	public void setImage(Webhook.Image image) {
		embed.put("image", image != null ? image.getJSON() : null);
	}
	
	public boolean send() {
		return send(url, json.toString());
	}
	
	public static class Image {
		public String url;
		public int width;
		public int height;
		
		public Image(String url, int width, int height) {
			this.url = url;
			this.width = width;
			this.height = height;
		}
		
		public JSONObject getJSON() {
			JSONObject json = new JSONObject();
			json.put("url", url);
			json.put("width", Integer.toString(width));
			json.put("height", Integer.toString(height));
			
			return json;
		}
	}
	
	public static boolean send(String webhookURL, String data) {
		HttpURLConnection connection = null;
		
		System.out.println(data);
		
		try {
			URL url = new URL(webhookURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", Integer.toString(data.length()));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(data);
			wr.close();
			
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line;
			
			while ((line = rd.readLine()) != null) {
				response.append(line).append('\r');
			}
			
			rd.close();
			
			System.out.println(response.toString());
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			connection.disconnect();
		}
		
		return true;
	}
}
