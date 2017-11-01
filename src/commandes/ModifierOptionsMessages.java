package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Modifier les options de la bo�te de Messages.
 */
public class ModifierOptionsMessages extends Commande {
	private final boolean masquer;
	private final String position;

	/**
	 * 
	 * @param masquer la bo�te de Messages ?
	 * @param position de la bo�te de Messages � l'�cran
	 */
	public ModifierOptionsMessages(final boolean masquer, final String position) {
		this.masquer = masquer;
		this.position = position;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierOptionsMessages(final HashMap<String, Object> parametres) {
		this( 
				parametres.containsKey("masquer") ? (boolean) parametres.get("masquer") : false,
				parametres.containsKey("position") ? (String) parametres.get("position") : Message.Position.BAS.nom
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// masquer ?
		Message.masquerBoiteMessage = this.masquer;
		if (masquer) {
			Message.imageBoiteMessage = Message.IMAGE_BOITE_MESSAGE_VIDE;
		} else {
			Message.imageBoiteMessage = Message.IMAGE_BOITE_MESSAGE_PLEINE;
		}
		// position
		Message.positionBoiteMessage = Message.Position.parNom(this.position);
		
		return curseurActuel+1;
	}

}
