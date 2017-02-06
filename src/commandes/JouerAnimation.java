package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Event;
import map.LecteurMap;

/**
 * Jouer une Animation sur la Map.
 * Une Animation est une succession de vignettes.
 * Une Animation peut cibler un Event, ou bien �tre positionn�e en des coordonn�es sp�cifiques.
 */
public class JouerAnimation extends Commande implements CommandeEvent {
	public final int numeroAnimation;
	public final int xEcran, yEcran;
	
	public int frameActuelle;
	
	private JouerAnimation(final int numeroAnimation, final Integer idEvent, final int xEcran, final int yEcran) {
		this.numeroAnimation = numeroAnimation;
		if (idEvent != null && idEvent>=0) {
			//l'Animation est affich�e sur un Event
			Event event = ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get(idEvent);
			this.xEcran = event.x;
			this.yEcran = event.y;
		} else {
			//l'Animation est affich�e en des coordonn�es sp�cifi�es
			this.xEcran = xEcran;
			this.yEcran = yEcran;
		}
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public JouerAnimation(final HashMap<String, Object> parametres) {
		this( 	(int) parametres.get("numeroAnimation"),
				parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null, //soit on utilise l'idEvent
				parametres.containsKey("idEvent") ? -1 : (int) parametres.get("xEcran"), //soit on utilise des coordonn�es
				parametres.containsKey("idEvent") ? -1 : (int) parametres.get("yEcran")
		);
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {
		this.frameActuelle = 0;
		Fenetre.getPartieActuelle().animations.add(this);
		return curseurActuel+1;
	}

}
