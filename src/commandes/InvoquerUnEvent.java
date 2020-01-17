package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import main.Commande;
import main.Main;
import map.Event;
import utilitaire.InterpreteurDeJson;

/**
 * Invoquer un Event sur la Map courante � partir du fichier JSON d'une Map
 */
public class InvoquerUnEvent extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(InvoquerUnEvent.class);

	private final int idMap, idEvent, x, y;

	/**
	 * Constructeur explicite
	 * 
	 * @param x       coordonnee (case) ou placer la copie
	 * @param y       coordonnee (case) ou placer la copie
	 * @param idMap   id de la Map ou se trouve l'Event a imiter
	 * @param idEvent id de l'Event a imiter
	 */
	public InvoquerUnEvent(final int x, final int y, final int idMap, final int idEvent) {
		this.idMap = idMap;
		this.idEvent = idEvent;
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public InvoquerUnEvent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("x"), (int) parametres.get("y"), (int) parametres.get("idMap"),
				(int) parametres.get("idEvent"));
	}

	@Override
	public int executer(int curseurActuel, List<Commande> commandes) {
		// Cr�er juste un Event � partir du JSON de la Map
		// (pas la peine de cr�er toute la Map)
		try {
			// Ouvrir le fichier JSON de la Map o� se trouve l'Event � invoquer
			JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(this.idMap);
			// Chercher l'Event dans le fichier JSON de la Map
			final JSONArray jsonEvents = jsonMap.getJSONArray("events");
			Event eventInvoque = null;
			onChercheLEventNumerote: for (Object ev : jsonEvents) {
				JSONObject jsonEvent = (JSONObject) ev;
				if (jsonEvent.getInt("id") == this.idEvent) {
					// Instancier l'Event � invoquer
					eventInvoque = Event.recupererUnEvent(jsonEvent, this.page.event.map);
					break onChercheLEventNumerote;
				}
			}
			if (eventInvoque != null) {
				// L'Event a �t� instanci� avec succ�s

				// Le placer sur la Map courante
				eventInvoque.x = this.x * Main.TAILLE_D_UN_CARREAU;
				eventInvoque.y = this.y * Main.TAILLE_D_UN_CARREAU;
				// Il sera ajout� � la Map courante � la prochaine frame
				this.page.event.map.eventsAAjouter.add(eventInvoque);
				LOG.info("Invocation de l'event " + this.idEvent + " originaire de la map " + this.idMap);

			} else {
				// L'event n'a pas �t� trouv�
				LOG.error("Impossible d'invoquer l'event " + this.idEvent
						+ ", il n'a pas �t� trouv� dans le JSON de la map " + this.idMap);
			}
		} catch (Exception e) {
			LOG.error("Impossible d'ouvrir le fichier JSON de la map " + this.idMap, e);
		}
		return curseurActuel + 1;
	}

}
