package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Retirer une Arme au joueur.
 */
public class RetirerArme extends Commande implements CommandeEvent, CommandeMenu {
	private int idArme;
	
	/**
	 * Constructeur explicite
	 * @param idArme identifiant de l'Arme � retirer (num�ro)
	 */
	public RetirerArme(final int idArme) {
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public RetirerArme(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idArme") );
	}
	
	@Override
	public final void executer() {
		//on proc�de � la suppression
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		final boolean[] armesPossedees = partieActuelle.armesPossedees;
		if (armesPossedees[idArme]) {
			armesPossedees[this.idArme] = false;
			partieActuelle.nombreDArmesPossedees--;
			partieActuelle.idArmeEquipee = -1; // -1 pour signifier qu'aucune Arme n'est �quip�e
		}
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
