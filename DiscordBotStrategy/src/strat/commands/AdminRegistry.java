package strat.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class AdminRegistry {
	
	public static final String ADMIN_REGISTRY_FILE = "admins.dat";
	
	private static final HashMap<Long, PermissionLevel> ADMINS = new HashMap<>();
	
	private static boolean initialized = false;
	
	private AdminRegistry() {}
	
	private static void init() {
		if (initialized) {
			return;
		}
		
		initialized = true;
		
		try (Scanner i = new Scanner(new File(ADMIN_REGISTRY_FILE))) {
			while (i.hasNextLine()) {
				String[] tokens = i.nextLine().split(",");
				
				ADMINS.put(Long.parseLong(tokens[0]), PermissionLevel.valueOf(tokens[1]));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void save(String fileName) {
		if (!initialized) {
			return;
		}
		
		try (PrintWriter o = new PrintWriter(new File(fileName))) {
			for (Map.Entry<Long, PermissionLevel> e : ADMINS.entrySet()) {
				o.println(String.format("%d,%s", e.getKey(), e.getValue()));
			}
		}
		catch (IOException e) {
			System.out.println("Could not save admin registry");
		}
	}
	
	public static boolean add(long user, PermissionLevel permissionLevel) {
		init();
		return ADMINS.put(user, permissionLevel) != null;
	}
	
	public static boolean remove(long user) {
		init();
		return ADMINS.remove(user) != null;
	}
	
	public static boolean isAdmin(long user) {
		init();
		return ADMINS.containsKey(user);
	}
	
	public static PermissionLevel getPermissionLevel(long user) {
		return ADMINS.get(user);
	}
}
