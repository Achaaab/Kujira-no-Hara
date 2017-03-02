package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;
import map.Heros;
import map.LecteurMap;
import map.Map;
import map.Transition;

/**
 * Le Heros est t�l�port� sur une autre Map.
 */
public class ChangerDeMap extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ChangerDeMap.class);
	
	private final int numeroNouvelleMap;
	private final int xDebutHeros;
	private final int yDebutHeros;
	private final Transition transition;
	
	/**
	 * Constructeur explicite
	 * @param numeroNouvelleMap num�ro de la nouvelle Map
	 * @param xDebutHeros coordonn�e x du H�ros (en pixels) � son arriv�e sur la Map
	 * @param yDebutHeros coordonn�e y du H�ros (en pixels) � son arriv�e sur la Map
	 * @param transition visuelle pour passer d'une Map � l'autre
	 */
	public ChangerDeMap(final int numeroNouvelleMap, final int xDebutHeros, final int yDebutHeros, final Transition transition) {
		this.numeroNouvelleMap = numeroNouvelleMap;
		this.xDebutHeros = xDebutHeros;
		this.yDebutHeros = yDebutHeros;
		this.transition = transition;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ChangerDeMap(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroNouvelleMap"), 
			(int) parametres.get("xDebutHeros") * Fenetre.TAILLE_D_UN_CARREAU,
			(int) parametres.get("yDebutHeros") * Fenetre.TAILLE_D_UN_CARREAU,
			parametres.containsKey("transition") ? Transition.parNom((String) parametres.get("transition")) : Transition.DEFILEMENT
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Heros heros = this.page.event.map.heros;
		final int directionHeros = heros.direction;
		this.transition.direction = directionHeros;
		final LecteurMap nouveauLecteur = new LecteurMap(Fenetre.getFenetre(), this.transition);
		try {
			final Map nouvelleMap = new Map(numeroNouvelleMap, nouveauLecteur, xDebutHeros, yDebutHeros, directionHeros);
			nouveauLecteur.devenirLeNouveauLecteurMap(nouvelleMap);
		} catch (Exception e) {
			LOG.error("Impossible de charger la map numero "+numeroNouvelleMap, e);
		}
		return curseurActuel+1;
	}

}
