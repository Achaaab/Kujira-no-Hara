package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Ajouter un nouveau Gadget au Heros
 */
public class AjouterGadget extends Commande implements CommandeEvent {
	int idGadget;
	
	/**
	 * Constructeur explicite
	 * @param gadget identifiant du Gadget � ajouter : son num�ro ou son nom
	 */
	public AjouterGadget(final int idGadget) {
		//l'identifiant du Gadget est son num�ro
		this.idGadget = idGadget;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterGadget(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idGadget") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (!partieActuelle.gadgetsPossedes[idGadget]) {
			partieActuelle.gadgetsPossedes[idGadget] = true;
			partieActuelle.nombreDeGadgetsPossedes++;
		}
		// Si actuellement rien d'�quip�, on l'�quipe
		if (partieActuelle.idGadgetEquipe < 0) {
			partieActuelle.idGadgetEquipe = idGadget;
		}
		return curseurActuel+1;
	}

}
