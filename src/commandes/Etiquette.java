package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Une Etiquette vers laquelle on peut d�placer le curseur.
 */
public class Etiquette  extends Commande implements CommandeEvent {
	/** Nom de l'Etiquette */
	public String nom;
	
	/**
	 * Constructeur explicite
	 * @param nom identifiant de l'Etiquette
	 */
	public Etiquette(final String nom) {
		this.nom = nom;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Etiquette(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nom") );
	}
	
	/**
	 * L'Etiquette en soi n'a aucun effet, ce n'est qu'un point de rep�re.
	 * Son execution est instantan�e.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		return curseurActuel+1;
	}

}
