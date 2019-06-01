package strat.game.relationships;

import java.util.HashSet;

import strat.game.ISerializable;
import strat.game.Nation;

public abstract class Relationship implements ISerializable {
	private HashSet<Nation> nations;
	private Nation sender;
	
	public Relationship(Nation sender) {
		this.sender = sender;
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
	
	public Nation getSender() {
		return sender;
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