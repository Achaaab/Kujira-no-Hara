package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class SupprimerEvent extends CommandeEvent {
	
	/**
	 * Constructeur sp�cifique
	 */
	public SupprimerEvent(){}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public SupprimerEvent(HashMap<String,Object> parametres){
		this();
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		int numeroEventASupprimer = this.page.event.numero;
		this.page.event.pageActive = null;
		this.page.event.map.supprimerEvenement(numeroEventASupprimer);
		return curseurActuel+1;
	}

}
