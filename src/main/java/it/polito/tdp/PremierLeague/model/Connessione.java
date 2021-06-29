package it.polito.tdp.PremierLeague.model;

public class Connessione {

	Match m;
	int peso;
	
	public Connessione(Match m, int peso) {
		super();
		this.m = m;
		this.peso = peso;
	}

	public Match getM() {
		return m;
	}

	public void setM(Match m) {
		this.m = m;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}
	
	
	
	
}
