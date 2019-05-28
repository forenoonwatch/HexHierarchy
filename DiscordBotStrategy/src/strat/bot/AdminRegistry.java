package strat.bot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;

public class AdminRegistry {
	private HashSet<Long> admins;
	
	public AdminRegistry() {
		admins = new HashSet<>();
	}
	
	public void load(String fileName) throws IOException {
		try (Scanner i = new Scanner(new File(fileName))) {
			while (i.hasNextLong()) {
				add(i.nextLong());
			}
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	public void save(String fileName) throws IOException {
		try (PrintWriter o = new PrintWriter(new File(fileName))) {
			for (Long l : admins) {
				o.println(l);
			}
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	public boolean add(long user) {
		return admins.add(user);
	}
	
	public boolean remove(long user) {
		return admins.remove(user);
	}
	
	public boolean isAdmin(long user) {
		return admins.contains(user);
	}
}
