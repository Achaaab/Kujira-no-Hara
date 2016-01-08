package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Supprimer un Event de la Map
 */
public class SupprimerEvent extends CommandeEvent {
	
	/**
	 * Constructeur vide
	 */
	public SupprimerEvent() {
		
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public SupprimerEvent(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		int numeroEventASupprimer = this.page.event.numero;
		this.page.event.pageActive = null;
		this.page.event.map.supprimerEvenement(numeroEventASupprimer);
		return curseurActuel+1;
	}

}
