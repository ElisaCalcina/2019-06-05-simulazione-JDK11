package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao;
	Graph<Integer, DefaultWeightedEdge> grafo;
	
	public Model() {
		dao= new EventsDao();
	}
	
	public List<Integer> getAnni(){
		return dao.getAnni();
	}
	
	public void creaGrafo(Integer anno) {
		grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		Graphs.addAllVertices(grafo, this.dao.getVertici());
		
		//aggiungo gli archi
		for(Integer v1: this.grafo.vertexSet()) {
			for(Integer v2: this.grafo.vertexSet()) {
				if(!v1.equals(v2)) {
					if(this.grafo.getEdge(v1, v2)==null) {
						Double latMediaV1=dao.getLatMedia(anno, v1);
						Double latMediaV2=dao.getLatMedia(anno, v2);
						
						Double lonMediaV1=dao.getLonMedia(anno, v1);
						Double lonMediaV2=dao.getLonMedia(anno, v2);
						
						Double distanzaMedia= LatLngTool.distance(new LatLng(latMediaV1,lonMediaV1), 
								new LatLng(latMediaV2,lonMediaV2 ), LengthUnit.KILOMETER);
						
						Graphs.addEdgeWithVertices(this.grafo, v1,  v2, distanzaMedia);
					}
				}
			}
		}
		
		System.out.println("Grafo creato con "+ this.grafo.vertexSet().size() +" vertici e " + this.grafo.edgeSet().size()+" archi");
	}
	
	public List<Vicini> getVicini(Integer distretto){
		List<Vicini> vicini= new ArrayList<>();
		List<Integer> id= Graphs.neighborListOf(this.grafo, distretto);
		
		for(Integer v: id) {
			vicini.add(new Vicini(v, this.grafo.getEdgeWeight(this.grafo.getEdge(distretto, v))));
		}
		
		Collections.sort(vicini);
		return vicini;
	}
	
	public Set<Integer> getVertici(){
		return this.grafo.vertexSet();
	}
	
}
