package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Une des diff�rentes Alternatives du Choix.
 */
public class ChoixAlternative extends Commande implements CommandeEvent {
	/** Num�ro du Choix */
	public int numeroChoix;
	/** Num�ro d'Alternative au sein du Choix */
	public int numeroAlternative;
	
	/**
	 * Constructeur explicite
	 * @param numeroChoix num�ro identifiant du Choix
	 * @param numeroAlternative num�ro de l'Alternative au sein du Choix
	 */
	public ChoixAlternative(final int numeroChoix, final int numeroAlternative) {
		this.numeroChoix = numeroChoix;
		this.numeroAlternative = numeroAlternative;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ChoixAlternative(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"),
				(int) parametres.get("alternative"));
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
			if (commande instanceof ChoixFin) {
				final ChoixFin finDeChoix = (ChoixFin) commande;
				if (finDeChoix.numero == this.numeroChoix) {
					//la fin de ce Choix a �t� trouv�e
					return i+1;
				}
			}
		}
		//la fin de Boucle n'a pas �t� trouv�e
		System.err.println("La fin du choix num�ro "+numeroChoix+" n'a pas �t� trouv�e !");
		return curseurActuel+1;
	}

}
