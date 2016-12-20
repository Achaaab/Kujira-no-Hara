package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteur extends Commande implements CommandeEvent {
	int numeroInterrupteur;
	boolean valeurADonner;
	
	/**
	 * Constructeur explicite
	 * @param numero de l'interrupteur � modifier
	 * @param valeur � donner � l'interrupteur
	 */
	public ModifierInterrupteur(final int numero, final boolean valeur) {
		numeroInterrupteur = numero;
		valeurADonner = valeur;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierInterrupteur(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroInterrupteur"),
			(boolean) parametres.get("valeur")
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Fenetre.getPartieActuelle().interrupteurs[numeroInterrupteur] = valeurADonner;
		return curseurActuel+1;
	}

}
