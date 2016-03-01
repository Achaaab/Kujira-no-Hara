package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import main.Commande;
import main.Fenetre;
import map.Event;
import map.LecteurMap;
import map.PageEvent;
import mouvements.Attendre;
import mouvements.Mouvement;
import utilitaire.InterpreteurDeJson;

/**
 * Un D�placement est un ensemble de mouvements subis par un Event.
 * Selon son utilisation, le D�placement pourra �tre naturel (situ� dans la description JSON de l'Event)
 * ou forc� (provoqu� par des Commandes Event).
 */
public class Deplacement extends Commande implements CommandeEvent {
	/** id de l'Event qui va �tre d�plac� durant ce Mouvement */
	public Integer idEventADeplacer; //Integer car cl� d'une HashMap, et null lorsque "cet Event"
	/** Mouvements constitutifs de ce D�placement */
	public final ArrayList<Mouvement> mouvements;
	/** faut-il interrompre les Mouvements impossibles, ou attendre qu'ils soient possibles ? */
	public boolean ignorerLesMouvementsImpossibles = false;
	/** faut-il rejouer le D�placement lorsqu'on l'a termin� ? */
	public boolean repeterLeDeplacement = true;
	/** faut-il attendre la fin du D�placement pour passer � la Commande suivante ? */
	public boolean attendreLaFinDuDeplacement = false;
	
	/**
	 * Constructeur explicite
	 * @param idEventADeplacer id de l'Event � d�placer, null signifie "cet Event", 0 le H�ros
	 * @param mouvements liste des Mouvements constitutifs du D�placement
	 * @param ignorerLesMouvementsImpossibles faut-il interrompre les Mouvements impossibles, ou attendre qu'ils soient possibles ?
	 * @param repeterLeDeplacement faut-il rejouer le D�placement lorsqu'on l'a termin� ?
	 * @param attendreLaFinDuDeplacement faut-il attendre la fin du D�placement pour passer � la Commande suivante ?
	 */
	public Deplacement(final Integer idEventADeplacer, final ArrayList<Mouvement> mouvements, final boolean ignorerLesMouvementsImpossibles, final boolean repeterLeDeplacement, final boolean attendreLaFinDuDeplacement, final PageEvent page) {
		this.idEventADeplacer = idEventADeplacer;
		this.mouvements = mouvements;
		this.ignorerLesMouvementsImpossibles = ignorerLesMouvementsImpossibles;
		this.repeterLeDeplacement = repeterLeDeplacement;
		this.attendreLaFinDuDeplacement = attendreLaFinDuDeplacement;
		this.page = page;
		
		//on apprend aux Mouvements le D�placement dont ils font partie
		for (Mouvement mouvement : this.mouvements) {
			mouvement.deplacement = this;
		}
	}
	
	//TODO utiliser l'un des autres constructeurs
	/**
	 * Constructeur batard
	 * @param deplacementJSON fichier JSON d�crivant le D�placement
	 * @param page de l'Event qui contient le Mouvement
	 */
	public Deplacement(final JSONObject deplacementJSON, final PageEvent page) {
		this( deplacementJSON.has("idEventADeplacer") ? (Integer) deplacementJSON.get("idEventADeplacer") : null, 
			creerListeDesMouvements(deplacementJSON, page), 
			deplacementJSON.has("ignorerLesMouvementsImpossibles") ? (boolean) deplacementJSON.get("ignorerLesMouvementsImpossibles") : Event.IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT, 
			deplacementJSON.has("repeterLeDeplacement") ? (boolean) deplacementJSON.get("repeterLeDeplacement") : Event.REPETER_LE_DEPLACEMENT_PAR_DEFAUT,
			deplacementJSON.has("attendreLaFinDuDeplacement") ? (boolean) deplacementJSON.get("attendreLaFinDuDeplacement") : Event.ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT,
			page
		);
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Deplacement(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("idEventADeplacer") ? (Integer) parametres.get("idEventADeplacer") : null,
			InterpreteurDeJson.recupererLesMouvements((JSONArray) parametres.get("mouvements")),
			parametres.containsKey("ignorerLesMouvementsImpossibles") ? (boolean) parametres.get("ignorerLesMouvementsImpossibles") : Event.IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT,
			parametres.containsKey("repeterLeDeplacement") ? (boolean) parametres.get("repeterLeDeplacement") : Event.REPETER_LE_DEPLACEMENT_PAR_DEFAUT,
			parametres.containsKey("attendreLaFinDuDeplacement") ? (boolean) parametres.get("attendreLaFinDuDeplacement") : Event.ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT,
			parametres.containsKey("page") ? (PageEvent) parametres.get("page") : null
		);
	}
	
	/**
	 * Cr��er la liste des Mouvements � partir du fichier JSON du D�placement
	 * @param deplacementJSON fichier JSON d�crivant le D�placement
	 * @param page de l'Event qui contient le Mouvement
	 * @return liste des Mouvements
	 */
	private static ArrayList<Mouvement> creerListeDesMouvements(final JSONObject deplacementJSON, final PageEvent page) {
		final ArrayList<Mouvement> mvts = new ArrayList<Mouvement>();
		for (Object actionDeplacementJSON : deplacementJSON.getJSONArray("mouvements")) {
			mvts.add( InterpreteurDeJson.recupererUnMouvement((JSONObject) actionDeplacementJSON) );
		}
		return mvts;
	}
	
	/**
	 * Ajouter ce Mouvement � la liste des Mouvements forc�s pour cet Event.
	 * M�thode appel�e lors de l'ex�cution des Commandes.
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		//on ajoute � la liste les Mouvements re�us
		final Event event = this.getEventADeplacer();
		this.page = event.pageActive; //TODO plut�t la page active de l'Event qui appelle la Commande
		for (Mouvement mvt : this.mouvements) {
			mvt.reinitialiser();
			event.deplacementForce.mouvements.add(mvt);
		}

		if (!this.attendreLaFinDuDeplacement) {
			//apr�s la planification, on passe � la Commande suivante
			return curseurActuel+1;
		} else {
			//on attend la fin du D�placement avant de passer � la Commande suivante
			if (this.mouvements.size() <= 0) {
				//la liste a �t� vid�e, on passe � la Commande suivante
				return curseurActuel+1;
			} else {
				//la liste contient encore des Mouvements � effectuer, on reste ici
				return curseurActuel;
			}
		}
	}
	
	/**
	 * Tout Mouvement d�place un Event de la Map en particulier.
	 * @return Event qui va �tre d�plac�
	 */
	public final Event getEventADeplacer() {
		if (this.idEventADeplacer !=null) {
			//un num�ro d'Event � d�placer a �t� sp�cifi� dans le JSON
			return ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
		} else {
			//aucun num�ro n'a �t� sp�cifi�, on d�place l'Event qui a lanc� la Commande
			return this.page.event;
		}
	}
	
	/**
	 * Executer le premier Mouvement du D�placement.
	 */
	public final void executerLePremierMouvement() {
		final Mouvement premierMouvement = this.mouvements.get(0);
		//si le stopEvent est activ�, on n'effectue pas les Mouvements
		//sauf s'il s'agit d'Attendre
		if (!((LecteurMap) Fenetre.getFenetre().lecteur).stopEvent || premierMouvement instanceof Attendre) {
			premierMouvement.executerLeMouvement(this);
		}
	}
	
	
}
