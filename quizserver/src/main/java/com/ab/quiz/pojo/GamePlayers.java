package com.ab.quiz.pojo;

public class GamePlayers {
	
	private long id;
	private long gameId;
	private long userId;
	//private int isWinner;
	
	/*public int getIsWinner() {
		return isWinner;
	}
	public void setIsWinner(int isWinner) {
		this.isWinner = isWinner;
	}*/
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getGameId() {
		return gameId;
	}
	public void setGameId(long gameId) {
		this.gameId = gameId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "GamePlayers [id=" + id + ", gameId=" + gameId + ", userId=" + userId + "]";
	}
	
	
}
