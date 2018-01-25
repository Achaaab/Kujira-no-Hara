package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Equiper le Heros avec un Gadget qu'il poss�de
 */
public class EquiperGadget extends Commande implements CommandeEvent {
	int idGadget;
	
	/**
	 * Constructeur explicite
	 * @param idGadget identifiant du gadget � �quiper
	 */
	public EquiperGadget(final int idGadget) {
		this.idGadget = idGadget;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public EquiperGadget(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idGadget") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		getPartieActuelle().equiperGadget(this.idGadget);
		return curseurActuel+1;
	}

}
