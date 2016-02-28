package map;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import commandes.Mouvement;
import utilitaire.InterpreteurDeJson;

/**
 * Un D�placement est un ensemble de mouvements subis par un Event.
 * Selon son utilisation, le D�placement pourra �tre naturel (situ� dans la description JSON de l'Event)
 * ou forc� (provoqu� par des Commandes Event).
 */
public class Deplacement {
	public final ArrayList<Mouvement> mouvements;
	public boolean ignorerLesMouvementsImpossibles = false;
	public boolean repeterLeDeplacement = true;
	
	/**
	 * Constructeur explicite
	 * @param mouvements liste des Mouvements constitutifs du D�placement
	 * @param ignorerLesMouvementsImpossibles si le Mouvement est impossible, passe-t-on au suivant ?
	 * @param repeterLeDeplacement faut-il r�p�ter le D�placement � partir du d�but une fois qu'il est termin� ?
	 */
	public Deplacement(final ArrayList<Mouvement> mouvements, final boolean ignorerLesMouvementsImpossibles, final boolean repeterLeDeplacement) {
		this.mouvements = mouvements;
		this.ignorerLesMouvementsImpossibles = ignorerLesMouvementsImpossibles;
		this.repeterLeDeplacement = repeterLeDeplacement;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param idEvent identifiant de l'Event � d�placer
	 * @param deplacementJSON fichier JSON d�crivant le D�placement
	 * @param page de l'Event qui contient le Mouvement
	 */
	public Deplacement(final Integer idEvent, final JSONObject deplacementJSON, final PageEvent page) {
		this.mouvements = new ArrayList<Mouvement>();
		for (Object actionDeplacementJSON : deplacementJSON.getJSONArray("mouvements")) {
			this.mouvements.add( InterpreteurDeJson.recupererUnMouvement((JSONObject) actionDeplacementJSON, page) );
		}
		
		try {
			this.repeterLeDeplacement = (boolean) deplacementJSON.get("repeterLeDeplacement");
		} catch (JSONException e2) {
			this.repeterLeDeplacement = Event.REPETER_LE_DEPLACEMENT_PAR_DEFAUT;
		}
		try {
			this.ignorerLesMouvementsImpossibles = (boolean) deplacementJSON.get("ignorerLesMouvementsImpossibles");
		} catch (JSONException e2) {
			this.ignorerLesMouvementsImpossibles = Event.IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT;
		}
	}
	
	/**
	 * Executer le premier Mouvement du D�placement.
	 */
	public final void executerLePremierMouvement() {
		((Mouvement) this.mouvements.get(0)).executerLeMouvement(this);
	}
}
