package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Une des diff�rentes Alternatives du Choix.
 */
public class AllerVersEtiquette extends Commande implements CommandeEvent {
	/** Nom de l'Etiquette */
	public String nom;
	
	/**
	 * Constructeur explicite
	 * @param nom de l'Etiquette vers laquelle aller
	 */
	public AllerVersEtiquette(final String nom) {
		this.nom = nom;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AllerVersEtiquette(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nom"));
	}

	/**
	 * Les Alternatives d'un Choix permettent des sauts de curseur dans le code Event.
	 * Leur execution est instantan�e.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof Etiquette) {
				final Etiquette etiquette = (Etiquette) commande;
				if (etiquette.nom == this.nom) {
					//la fin de ce Choix a �t� trouv�e
					return i+1;
				}
			}
		}
		//la fin de Boucle n'a pas �t� trouv�e
		System.err.println("L'�tiquette "+nom+" n'a pas �t� trouv�e !");
		return curseurActuel+1;
	}

}
