package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Effacer une image.
 */
public class EffacerImage extends Commande implements CommandeEvent {
	private int numero;

	/**
	 * Constructeur explicite
	 * 
	 * @param numero de l'image � effacer
	 */
	public EffacerImage(final int numero) {
		this.numero = numero;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public EffacerImage(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().images[this.numero] = null;
		return curseurActuel + 1;
	}

}
