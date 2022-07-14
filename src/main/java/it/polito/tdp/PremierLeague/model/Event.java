package it.polito.tdp.PremierLeague.model;

public class Event implements Comparable<Event>{
	
	public enum EventType {
		GOAL,
		ESPULSIONE,
		INFORTUNIO
	}
	
	private Integer time;
	private EventType type;
	
	public Event(int time) {
		super();
		this.time = time;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public int getTime() {
		return time;
	}

	@Override
	public int compareTo(Event o) {
		return this.time.compareTo(o.getTime());
	}
	
	

}