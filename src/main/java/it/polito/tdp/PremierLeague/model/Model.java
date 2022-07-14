package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

	public class Model {
		
		private PremierLeagueDAO dao;
		private Graph<Player, DefaultWeightedEdge> grafo;
		private Map<Integer, Player> idMap;
		
		private Player migliore;
		private double effMigliore;
		
		public Model() {
			this.dao = new PremierLeagueDAO();
		}
		
		public void creaGrafo(Match m) {
			
			this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
			this.idMap = new HashMap<>();
			this.dao.loadPlayers(idMap, m);
			
			Graphs.addAllVertices(this.grafo, this.idMap.values());
			
			for (Adiacenza a: this.dao.getAdiacenza(idMap, m))
				if (!this.grafo.containsEdge(a.getP1(), a.getP2()))
					Graphs.addEdge(this.grafo, a.getP1(), a.getP2(), a.getPeso());
		}
		
		public void giocatoreMigliore() {
			
			this.migliore = null;
			this.effMigliore = 0.0;
			
			for (Player p: this.grafo.vertexSet()) {
				double peso = 0.0;
				for (DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(p))
					peso += this.grafo.getEdgeWeight(e);
				for (DefaultWeightedEdge e: this.grafo.incomingEdgesOf(p))
					peso -= this.grafo.getEdgeWeight(e);
				
				if (peso>this.effMigliore) {
					this.effMigliore = peso;
					this.migliore = p;
				}
			}
		}
		
		public String simulazione(int N, Match m) {
			
			Simulator sim = new Simulator(this.grafo);
			List<Integer> id = this.dao.getTeamsID(m.getMatchID());
			sim.init(N, id.get(0), id.get(1));
			sim.run();
			
			String result = "Risultato simulazione: \nTeam 1: "+sim.getT1().getGoalTeam()+" goal - Team2 "
					+sim.getT2().getGoalTeam()+" goal\nTeam 1: "+sim.getT1().getEsplTeam()
					+" espulsioni - Team 2: "+sim.getT2().getEsplTeam()+" espulsioni";
			return result;
			
		}
		
		public Player getMigliore() {
			return migliore;
		}

		public double getEffMigliore() {
			return effMigliore;
		}

		public List<Match> getAllMatches(){
			return this.dao.listAllMatches();
		}
		
		public int getNumVertex() {
			return this.grafo.vertexSet().size();
		}
		
		public int getNumEdge() {
			return this.grafo.edgeSet().size();
		}
	}