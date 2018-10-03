package mouvements;

import java.util.HashMap;

import main.Main;
import map.Event;
import map.Event.Direction;

/**
 * D�placer un Event d'un pas dans la direction oppos�e � la direction de l'Event.
 */
public class PasEnArriere extends Avancer {
	
	/**
	 * Constructeur explicite
	 * @param nombreDePixels ditance parcourue � reculons (en pixels)
	 */
	public PasEnArriere(final int nombreDePixels) {
		//le -1 est bidon, il sera remplac� par la direction de l'Event lors de la v�rification
		super(-1, nombreDePixels);
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public PasEnArriere(final HashMap<String, Object> parametres) {
		this( (parametres.containsKey("nombreDeCarreaux") ? (int) parametres.get("parametres") : 1) * Main.TAILLE_D_UN_CARREAU);
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		//on peut avancer � reculons si on peut avancer en arri�re
		final Event event = this.deplacement.getEventADeplacer();
		this.direction = event.direction; //la direction affich�e durant le Mouvement est celle de l'Event avant
		final Avancer mouvementFictif = new Avancer(Event.Direction.directionOpposee(event.direction), Main.TAILLE_D_UN_CARREAU);
		mouvementFictif.deplacement = this.deplacement; //Deplacement pour �viter la NullPointerException dans Avancer
		
		//puis on lance la v�rification traditionnelle
		return mouvementFictif.mouvementPossible();
	}
	
	@Override
	public final void calculDuMouvement(final Event event) {
		event.avance = true;
		
		//d�placement :
		switch (this.direction) {
			case Direction.BAS : 
				event.y -= event.vitesseActuelle.valeur; 
				break;
			case Direction.GAUCHE : 
				event.x += event.vitesseActuelle.valeur; 
				break;
			case Direction.DROITE : 
				event.x -= event.vitesseActuelle.valeur; 
				break;
			case Direction.HAUT : 
				event.y += event.vitesseActuelle.valeur; 
				break;
		}
		this.ceQuiAEteFait += event.vitesseActuelle.valeur;
	}

}
