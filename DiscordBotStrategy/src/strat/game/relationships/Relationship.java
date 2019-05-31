package strat.game.relationships;

import java.util.HashSet;

import strat.game.ISerializable;
import strat.game.Nation;

public abstract class Relationship implements ISerializable {
	private HashSet<Nation> nations;
	
	public Relationship() {
		nations = new HashSet<>();
	}
	
	public boolean addNation(Nation n) {
		return nations.add(n);
	}
	
	public boolean removeNation(Nation n) {
		return nations.remove(n);
	}
	
	public boolean hasNation(Nation n) {
		return nations.contains(n);
	}
	
	public HashSet<Nation> getNations() {
		return nations;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Nation n : nations) {
			sb.append(n.getNationID()).append(',');
		}
		
		return sb.toString();
	}
}