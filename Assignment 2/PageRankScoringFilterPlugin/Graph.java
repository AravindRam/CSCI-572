package team1.PageRankScoringFilterPlugin;

import java.util.HashSet;
import java.util.Hashtable;

public class Graph {

	private HashSet<Node> nodes = new HashSet<Node>() ;
	private Hashtable<Node, HashSet<Node>> outgoing_links = new Hashtable<Node, HashSet<Node>>();
	private Hashtable<Node, HashSet<Node>> incoming_links = new Hashtable<Node, HashSet<Node>>();
	private int count_links = 0 ;
	
	public boolean addNode ( Node node ) {
		if ( nodes.contains(node) ) return false ;
		
		nodes.add(node) ;
		if ( !outgoing_links.containsKey ( node ) ) {
			outgoing_links.put ( node, new HashSet<Node>() ) ;
		}
		if ( !incoming_links.containsKey ( node ) ) {
			incoming_links.put ( node, new HashSet<Node>() ) ;
		}
		return true ;
	}
	
	public boolean addLink ( Node source, Node destination ) {
		if ( source.equals( destination ) ) return false ;

		addNode ( source ) ;
		addNode ( destination ) ;
		
		if ( outgoing_links.get ( source ).contains( destination ) ) {
			return false ;
		}
		
		outgoing_links.get ( source ).add( destination ) ;
		incoming_links.get ( destination ).add( source ) ;
		count_links++ ;

		return true ;
	}
	
	public int countNodes() {
		return nodes.size() ;
	}
	
	public int countLinks() {
		return count_links ;
	}
	
	public int countOutgoingLinks(Node node) {
		return outgoing_links.get(node).size() ;
	}

	public HashSet<Node> getIncomingLinks(Node node) {
		return incoming_links.get(node) ;
	}
	
	public HashSet<Node> getNodes() {
		return nodes ;
	}
	
}