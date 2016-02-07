package commandesMenu;

import java.util.HashMap;

import jeu.Objet;
import main.Fenetre;

/**
 * Retirer un certain nombre d'Objets au joueur.
 */
public class RetirerObjet extends CommandeMenu {
	public Integer numeroObjet;
	private final int quantite;
	
	/**
	 * Constructeur explicite
	 * @param objet identifiant de l'Objet � retirer, soit son nom, soit son num�ro
	 * @param quantite � retirer pour cet Objet
	 */
	public RetirerObjet(final Object objet, final int quantite) {
		try {
			//l'identifiant de l'Objet est son num�ro
			this.numeroObjet = (Integer) objet;
		} catch (Exception e) {
			//l'identifiant de l'Objet est son nom
			this.numeroObjet = Objet.objetsDuJeuHash.get((String) objet).numero;
		}
		this.quantite = quantite;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public RetirerObjet(final HashMap<String, Object> parametres) {
		this( (Object) (parametres.containsKey("nomObjet") ? parametres.get("nomObjet") : parametres.get("numeroObjet")), //num�ro ou nom
			(int) parametres.get("quantite")
		);
	}
	
	@Override
	public final void executer() {
		final int[] objetsPossedes = Fenetre.getPartieActuelle().objetsPossedes;
		
		if (objetsPossedes[this.numeroObjet] >= this.quantite) {
			objetsPossedes[this.numeroObjet] -= quantite;
		} else {
			objetsPossedes[this.numeroObjet] = 0;
		}
	}

}
