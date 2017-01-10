package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Event;
import map.Map;
import utilitaire.InterpreteurDeJson;

/**
 * Invoque un Event g�n�rique
 */
public class InvoquerEventGenerique extends Commande implements CommandeEvent {
	
	private final int x;
	private final int y;
	private final String nomEventInvoque;
	
	/**
	 * Constructeur explicite
	 * @param x position x de l'Event g�n�rique invoqu�
	 * @param y position y de l'Event g�n�rique invoqu�
	 * @param nom nom de l'Event g�n�rique invoqu�
	 */
	public InvoquerEventGenerique(final int x,final int y, final String nomEventInvoque) {
		this.x = x;
		this.y = y;
		this.nomEventInvoque = nomEventInvoque;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public InvoquerEventGenerique(final HashMap<String, Object> parametres) {
		this((int) parametres.get("x"),
			(int) parametres.get("y"),
			(String) parametres.get("nomEventInvoque"));
	}

	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {
		final Map map = this.page.event.map;
		final int xPixel = this.x*Fenetre.TAILLE_D_UN_CARREAU;
		final int yPixel = this.y*Fenetre.TAILLE_D_UN_CARREAU;
		final int idEventInvoque = -1;
		final Event eventInvoque = InterpreteurDeJson.creerEventGenerique(idEventInvoque, this.nomEventInvoque, 
				xPixel, yPixel, 0, map);
		if (map.calculerSiLaPlaceEstLibre(xPixel, yPixel, eventInvoque.largeurHitbox, eventInvoque.hauteurHitbox, idEventInvoque)) {
			map.eventsAAjouter.add(eventInvoque);
		}
		return curseurActuel + 1;
	}

}
