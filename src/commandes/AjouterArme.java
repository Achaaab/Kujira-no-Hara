package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Arme;
import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Ajouter une nouvelle Arme au Heros
 */
public class AjouterArme extends Commande implements CommandeEvent {
	int idArme;
	
	/**
	 * Constructeur explicite
	 * @param arme identifiant de l'Arme � ajouter : son num�ro ou son nom
	 */
	public AjouterArme(final Object arme) {
		try {
			//l'identifiant de l'Arme est son num�ro
			this.idArme = (Integer) arme;
		} catch (Exception e) {
			//l'identifiant de l'Arme est son num�ro
			this.idArme = Arme.armesDuJeuHash.get((String) arme).id;
		}
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AjouterArme(final HashMap<String, Object> parametres) {
		this( (Object) (parametres.containsKey("idArme") ? parametres.get("idArme") : parametres.get("nomArme")) );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (!partieActuelle.armesPossedees[idArme]) {
			partieActuelle.armesPossedees[idArme] = true;
			partieActuelle.nombreDArmesPossedees++;
		}
		return curseurActuel+1;
	}

}
