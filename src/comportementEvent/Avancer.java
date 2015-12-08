package comportementEvent;

import java.util.ArrayList;

import map.Event;
import menu.Parametre;

public class Avancer extends CommandeEvent{
	protected int direction;
	public int nombreDeCarreaux;
	public int ceQuiAEteFait = 0; //avanc�e en pixel, doit atteindre nombreDeCarreaux*32
	
	public Avancer(Integer direction, Integer nombreDeCarreaux){
		this.direction = direction;
		this.nombreDeCarreaux = nombreDeCarreaux;
	}
	
	/**
	 * Constructeur g�n�rique.
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Avancer(ArrayList<Parametre> parametres){
		this( (Integer)trouverParametre("direction", parametres), (Integer)trouverParametre("nombreDeCarreaux", parametres) );
	}
	
	public int getDirection(){
		return direction;
	}
	
	/**
	 * R�initialiser un mouvement le d�clare non fait, et change la direction en cas de mouvement al�atoire.
	 */
	public void reinitialiser(){
		ceQuiAEteFait = 0;
	}

	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		Event event = this.page.event;
		if(ceQuiAEteFait >= nombreDeCarreaux*32){
			event.avance = false; //le mouvement est termin�
			return curseurActuel+1;
		}
		event.avance = true; //le mouvement est en cours
		event.deplacer();
		return curseurActuel;
	}
}
