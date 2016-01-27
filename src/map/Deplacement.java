package map;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import commandesEvent.Avancer;
import commandesEvent.CommandeEvent;
import commandesEvent.Mouvement;

/**
 * Un D�placement est un ensemble de mouvements subits par un Event.
 * Selon son utilisation, le D�placement pourra �tre naturel (situ� dans la description JSON de l'Event)
 * ou forc� (provoqu� par des Commandes Event).
 */
public class Deplacement {
	public final ArrayList<CommandeEvent> mouvements;
	public boolean ignorerLesMouvementsImpossibles = false;
	public boolean repeterLeDeplacement = true;
	
	/**
	 * Constructeur g�n�rique
	 * @param deplacementJSON fichier JSON d�crivant le D�placement
	 */
	public Deplacement(final JSONObject deplacementJSON) {		
		this.mouvements = new ArrayList<CommandeEvent>();
		for (Object actionDeplacementJSON : deplacementJSON.getJSONArray("mouvements")) {
			try {
				final Class<?> classeActionDeplacement = Class.forName("commandesEvent."+((JSONObject) actionDeplacementJSON).get("nom"));
				final Iterator<String> parametresNoms = ((JSONObject) actionDeplacementJSON).keys();
				String parametreNom;
				Object parametreValeur;
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				while (parametresNoms.hasNext()) {
					parametreNom = parametresNoms.next();
					if (!parametreNom.equals("nom")) { //le nom servait � trouver la classe, ici on ne s'int�resse qu'aux param�tres
						parametreValeur = ((JSONObject) actionDeplacementJSON).get(parametreNom);
						parametres.put(parametreNom, parametreValeur);
					}
				}
				final Constructor<?> constructeurActionDeplacement = classeActionDeplacement.getConstructor(HashMap.class);
				final CommandeEvent actionDeplacement = (CommandeEvent) constructeurActionDeplacement.newInstance(parametres);
				this.mouvements.add(actionDeplacement);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
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
	 * @param event qui doit �tre d�plac�
	 */
	public final void executerLePremierMouvement(final Event event) {
		//TODO faire de event un attribut de Deplacement ou de Mouvement
		//qui a une m�thode executerLeMouvement()
		//et caster par l'interface
		((Mouvement) this.mouvements.get(0)).executerLeMouvement(event, this);
	}
}
