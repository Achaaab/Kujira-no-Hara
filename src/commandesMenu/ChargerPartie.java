package commandesMenu;

import main.Fenetre;
import main.Partie;

/**
 * Charger une Partie dans le menu d�di�
 */
public class ChargerPartie extends CommandeMenu {
	private final int numeroDeSauvegarde;
	
	/**
	 * Constructeur explicite
	 * @param numeroDeSauvegarde num�ro du fichier de sauvegarde � charger
	 */
	public ChargerPartie(final int numeroDeSauvegarde) {
		this.numeroDeSauvegarde = numeroDeSauvegarde;
	}
	
	@Override
	public final void executer() {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		System.out.println("chargement de la partie numero "+numeroDeSauvegarde);
		fenetre.setPartieActuelle( Partie.chargerPartie(this.numeroDeSauvegarde) );
		fenetre.ouvrirLaPartie();
	}

}
