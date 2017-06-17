package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Chronometre;
import main.Commande;
import main.Fenetre;

/**
 * D�marrer le Chronometre � partir d'une certaine valeur.
 */
public class DemarrerChronometre extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(DemarrerChronometre.class);
	
	public final boolean croissant;
	public final int nombreDeSecondesInitial;
	
	/**
	 * Constructeur explicite
	 * @param croissant le temps augmente-t-il ?
	 * @param depart valeur dont part le temps
	 */
	public DemarrerChronometre(final boolean croissant, final int depart) {
		this.nombreDeSecondesInitial = depart;
		this.croissant = croissant;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public DemarrerChronometre(final HashMap<String, Object> parametres) {
		this( 
				parametres.containsKey("croissant") ? (boolean) parametres.get("croissant") : false,
				parametres.containsKey("depart") ? (int) parametres.get("depart") : 0
		);
	}
	
	@Override
	public int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Fenetre.getPartieActuelle().chronometre = new Chronometre(this.croissant, this.nombreDeSecondesInitial);
		LOG.debug("Chronom�tre "+ (this.croissant?"croissant":"d�croissant") +" d�marr� � "+ this.nombreDeSecondesInitial +" secondes.");
		return curseurActuel+1;
	}

}
