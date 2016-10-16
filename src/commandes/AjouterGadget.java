package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Gadget;
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
	public AjouterGadget(final Object gadget) {
		try {
			//l'identifiant du Gadget est son num�ro
			this.idGadget = (Integer) gadget;
		} catch (Exception e) {
			//l'identifiant du Gadget est son num�ro
			this.idGadget = Gadget.gadgetsDuJeuHash.get((String) gadget).id;
		}
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterGadget(final HashMap<String, Object> parametres) {
		this( (Object) (parametres.containsKey("idGadget") ? parametres.get("idGadget") : parametres.get("nomGadget")) );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (!partieActuelle.gadgetsPossedes[idGadget]) {
			partieActuelle.gadgetsPossedes[idGadget] = true;
			partieActuelle.nombreDeGadgetsPossedes++;
		}
		return curseurActuel+1;
	}

}
