package it.polito.tdp.PremierLeague.model;

public class TeamSim {

	private int teamID;
	private int goalTeam;
	private int esplTeam;
	private int playersTeam;
	
	public TeamSim(int teamID) {
		this.teamID = teamID;
		this.goalTeam = 0;
		this.esplTeam = 0;
		this.playersTeam = 11;
	}

	public int getGoalTeam() {
		return goalTeam;
	}

	public int getEsplTeam() {
		return esplTeam;
	}

	public int getPlayersTeam() {
		return playersTeam;
	}
	
	public void incrementEspl() {
		this.esplTeam ++;
	}
	
	public void decrementPlayers() {
		this.playersTeam --;
	}
	
	public void incrementGoal() {
		this.goalTeam ++;
	}

	public int getTeamID() {
		return teamID;
	}

}
