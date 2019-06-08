package strat.game.relationships;

import java.util.HashSet;

import strat.game.ISerializable;
import strat.game.Nation;

public abstract class Relationship implements ISerializable {
	private HashSet<Nation> nations;
	private Nation sender;
	private boolean request;
	
	public Relationship(Nation sender, boolean isRequest) {
		this.sender = sender;
		nations = new HashSet<>();
		request = isRequest;
	}
	
	public void setRequest(boolean request) {
		this.request = request;
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
	
	public boolean isRequest() {
		return request;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(request).append(',');
		
		for (Nation n : nations) {
			sb.append(n.getNationID()).append(',');
		}
		
		return sb.toString();
	}
}