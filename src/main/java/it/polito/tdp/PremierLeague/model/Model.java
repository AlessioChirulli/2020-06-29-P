package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	PremierLeagueDAO dao;
	Graph<Match,DefaultWeightedEdge> grafo;
	Map<Integer,Match> idMap;
	List<Connessione> soluzione;
	int pesoMax;
	
	public Model() {
		dao=new PremierLeagueDAO();
	}
	
	public String creaGrafo(int minuti,int mese) {
		idMap=new HashMap<>();
		grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		dao.listMatches(mese, idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		for(Adiacenza a: dao.listAdiacenze(mese, minuti, idMap)) {
			if(grafo.getEdge(a.getM1(), a.getM2())==null) {
				Graphs.addEdge(grafo, a.getM1(), a.getM2(), a.getPeso());
			}
		}
		return "GRAFO CREATO!\n#VERTICI: "+grafo.vertexSet().size()+"\n#ARCHI: "+grafo.edgeSet().size();
		
	}
	
	public String connessioneMax() {
		int pesoMax=0;
		List<DefaultWeightedEdge> result=new ArrayList<>();
		for(DefaultWeightedEdge e: grafo.edgeSet()) {
			if(pesoMax<grafo.getEdgeWeight(e))
				pesoMax=(int)grafo.getEdgeWeight(e);
		}
		for(DefaultWeightedEdge e: grafo.edgeSet()) {
			if(pesoMax==grafo.getEdgeWeight(e)) {
				result.add(e);
			}
		}
		String s="Coppie con connessione massima:\n";
		for(DefaultWeightedEdge e: result) {
			s+=grafo.getEdgeSource(e)+" - "+grafo.getEdgeTarget(e)+" : "+grafo.getEdgeWeight(e)+"\n";
		}
		return s;
	}
	
	public Set<Match> getVertex(){
		return grafo.vertexSet();
	}
	
	public List<Connessione> getCammino(Match m1,Match m2){
		pesoMax=0;
		soluzione=new LinkedList<Connessione>();
		List<Connessione> parziale=new ArrayList<>();
		for(Match m: Graphs.neighborListOf(grafo, m1)) {
			parziale.add(new Connessione(m,(int)grafo.getEdgeWeight(grafo.getEdge(m, m1))));
			cercaSoluzione(parziale,m2);
		}
		return soluzione;
	}
	
	public int getPeso() {
		return pesoMax;
	}

	private void cercaSoluzione(List<Connessione> parziale,Match m2) {
		// caso terminale
		if(parziale.get(parziale.size()-1).getM().equals(m2)) {
			int peso=0;
			for(Connessione c: parziale) {
				peso+= c.getPeso();
			}
			if(pesoMax<peso) {
				pesoMax=peso;
				soluzione=new LinkedList<>(parziale);
			}
			return ;
		}else {
			for(Match m: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1).getM())) {
				boolean controllo = true;
				for(Connessione c: parziale) {
					if((c.getM().getTeamHomeID()==m.getTeamHomeID() && c.getM().getTeamAwayID()==m.getTeamAwayID() ) || (c.getM().getTeamAwayID()==m.getTeamHomeID() && c.getM().getTeamHomeID()==m.getTeamAwayID() ) ) {
						controllo=false;
					}
				}
				if(controllo) {
					Connessione c=new Connessione(m,(int)grafo.getEdgeWeight(grafo.getEdge(m, parziale.get(parziale.size()-1).getM())));
					parziale.add(c);
					cercaSoluzione(parziale,m2);
					//backtracking
					parziale.remove(c);
				}
				
			}
		}
		
	}
}
