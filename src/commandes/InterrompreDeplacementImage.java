package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Picture;

/**
 * Figer une Image en supprimant son D�placement.
 */
public class InterrompreDeplacementImage extends Commande implements CommandeEvent {
	
	/** num�ro de l'image � d�placer */
	private Integer numero; //Integer car utilis� comme cl� d'une HashMap
	
	/**
	 * Constructeur explicite
	 * @param numero de l'image � stopper
	 */
	private InterrompreDeplacementImage(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public InterrompreDeplacementImage(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Picture picture = Fenetre.getPartieActuelle().images.get(this.numero);
		picture.deplacementActuel = null;
		
		return curseurActuel+1;
	}

}
