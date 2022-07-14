package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Team> listAllTeams(){
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void loadPlayers(Map<Integer, Player> idMap, Match m) {
		String sql = "SELECT p.PlayerID, p.Name "
				+ "FROM actions a, players p "
				+ "WHERE a.PlayerID=p.PlayerID AND a.MatchID=?";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				
				idMap.put(res.getInt("PlayerID"), new Player(res.getInt("PlayerID"),
						res.getString("Name")));
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Adiacenza> getAdiacenza(Map<Integer, Player> idMap, Match m) {
		String sql = "SELECT a1.PlayerID AS p1, a2.PlayerID AS p2, a1.TotalSuccessfulPassesAll AS sp1, a1.Assists AS assist1, a1.TimePlayed AS tp1, "
				+ "a2.TotalSuccessfulPassesAll AS sp2, a1.Assists AS assist2, a1.TimePlayed AS tp2 "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.MatchID=? AND a2.MatchID=a1.MatchID AND a2.PlayerID>a1.PlayerID AND a1.TeamID!=a2.TeamID";
		List<Adiacenza> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				
				double e1 = ( (double)res.getInt("sp1") + (double)res.getInt("assist1")) / (double)res.getInt("tp1");
				double e2 = ( (double)res.getInt("sp2") + (double)res.getInt("assist2")) / (double)res.getInt("tp2");
				
				if (e1>e2) {
					result.add(new Adiacenza(idMap.get(res.getInt("p1")), 
							idMap.get(res.getInt("p2")), e1 - e2));
				}
				
				if (e2>e1) {
					result.add(new Adiacenza(idMap.get(res.getInt("p2")), 
							idMap.get(res.getInt("p1")), e2 - e1));
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Integer getTeam(int p) {
		String sql = "SELECT DISTINCT TeamID "
				+ "FROM actions "
				+ "WHERE PlayerID=?";
		int m = 0;
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, p);
			ResultSet res = st.executeQuery();
			
			if (res.next())
				m = res.getInt("TeamID");
			
			conn.close();
			return m;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Integer> getTeamsID(int m) {
		String sql = "SELECT TeamHomeID, TeamAwayID "
				+ "FROM matches "
				+ "WHERE MatchID=?";
		List<Integer> result = new ArrayList<>();
		 
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m);
			ResultSet res = st.executeQuery();
			
			if (res.next()) {
				result.add(res.getInt("TeamHomeID"));
				result.add(res.getInt("TeamAwayID"));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}