package strat.game;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;

public final class AdminRegistry {
	
	public static final String ADMIN_REGISTRY_FILE = "admins.dat";
	
	private static final HashSet<Long> admins = new HashSet<>();
	private static boolean initialized = false;
	
	private AdminRegistry() {}
	
	private static void init() {
		if (initialized) {
			return;
		}
		
		initialized = true;
		
		try (Scanner i = new Scanner(new File(ADMIN_REGISTRY_FILE))) {
			while (i.hasNextLong()) {
				admins.add(i.nextLong());
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
			for (Long l : admins) {
				o.println(l);
			}
		}
		catch (IOException e) {
			System.out.println("Could not save admin registry");
		}
	}
	
	public static boolean add(long user) {
		init();
		return admins.add(user);
	}
	
	public static boolean remove(long user) {
		init();
		return admins.remove(user);
	}
	
	public static boolean isAdmin(long user) {
		init();
		return admins.contains(user);
	}
}
