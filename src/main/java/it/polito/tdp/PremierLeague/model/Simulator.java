package it.polito.tdp.PremierLeague.model;

import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;
import it.polito.tdp.PremierLeague.model.Event.EventType;

public class Simulator {

	// dati in ingresso
	private int N;
	
	// dati in uscita 
	private TeamSim t1;
	private TeamSim t2;
	
	
	// modello del mondo
	private Graph<Player, DefaultWeightedEdge> grafo;

	// coda degli eventi
	PriorityQueue<Event> queue;
	
	public Simulator(Graph<Player, DefaultWeightedEdge> grafo) {
		
		this.grafo = grafo;
	}
	
	public void init(int N, int teamID1, int teamID2) {
		
		this.N = N;
		this.t1 = new TeamSim(teamID1);
		this.t2 = new TeamSim(teamID2);
		
		this.queue = new PriorityQueue<>();
		this.queue.add(new Event(1));
	}
	
	public void run() {
		
		while (!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}
	}

	private void processEvent(Event e) {
		
		double p = Math.random();
		if (p<0.5)
			e.setType(EventType.GOAL);
		else if (p>=0.5 && p<0.8)
			e.setType(EventType.ESPULSIONE);
		else
			e.setType(EventType.INFORTUNIO);

		switch(e.getType()) {
		
		case GOAL:
			if (t1.getPlayersTeam()>t2.getPlayersTeam()) {
				this.t1.incrementGoal();
			} else if (t1.getPlayersTeam()<t2.getPlayersTeam()) {
				this.t2.incrementGoal();
			} else {
				if (getTeamMigliore()==t1.getTeamID())
					t1.incrementGoal();
				else 
					t2.incrementGoal();
			}
			break;
			
		case ESPULSIONE:
			double pr = Math.random();
			if (pr<0.6) {
				int teamMigl = getTeamMigliore();
				if (t1.getTeamID()==teamMigl) {
					t1.incrementEspl();
					t1.decrementPlayers();
				} else {
					t2.incrementEspl();
					t2.decrementPlayers();
				}
			}
			break;
			
		case INFORTUNIO:
			double pro = Math.random();
			if (pro<0.5)
				this.N += 2;
			else
				this.N += 3;
			break;
		}
		
		if (e.getTime()<N)
			this.queue.add(new Event(e.getTime()+1));
	}
	
	private int getTeamMigliore() {
		
		PremierLeagueDAO dao = new PremierLeagueDAO();
		return dao.getTeam(giocatoreMigliore());
	}
	
	private int giocatoreMigliore() {
		
		Player migliore = null;
		double effMigliore = 0.0;
		
		for (Player p: this.grafo.vertexSet()) {
			double peso = 0.0;
			for (DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(p))
				peso += this.grafo.getEdgeWeight(e);
			for (DefaultWeightedEdge e: this.grafo.incomingEdgesOf(p))
				peso -= this.grafo.getEdgeWeight(e);
			
			if (peso>effMigliore) {
				effMigliore = peso;
				migliore = p;
			}
		}
		return migliore.getPlayerID();
	}

	public TeamSim getT1() {
		return t1;
	}

	public TeamSim getT2() {
		return t2;
	}	
	
}