package comportementEvent;

import java.util.ArrayList;

import main.Fenetre;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteurLocal extends CommandeEvent {
	final boolean valeurADonner;
	final int numeroMap;
	final int numeroEvent;
	final int numeroInterrupteurLocal;
	
	/**
	 * Constructeur explicite
	 * @param numeroMap num�ro de la Map o� se situe l'interrupteur local � modifier
	 * @param numeroEvent num�ro de l'Event auquel appartient l'interrupteur local � modifier
	 * @param numeroInterrupteurLocal 0, 1, 2 ou 3 pour dire A, B, C ou D
	 * @param valeur � donner � l'interrupteur local
	 */
	public ModifierInterrupteurLocal(final int numeroMap, final int numeroEvent, final int numeroInterrupteurLocal, final boolean valeur) {
		this.numeroMap = numeroMap;
		this.numeroEvent = numeroEvent;
		this.numeroInterrupteurLocal = numeroInterrupteurLocal;
		this.valeurADonner = valeur;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		String code = "m"+this.numeroMap+"e"+this.numeroEvent+"i"+this.numeroInterrupteurLocal;
		ArrayList<String> interrupteursLocaux = Fenetre.getPartieActuelle().interrupteursLocaux;
		if (valeurADonner) {
			if (!interrupteursLocaux.contains(code)) {
				Fenetre.getPartieActuelle().interrupteursLocaux.add(code);
			}
		} else {
			if (interrupteursLocaux.contains(code)) {
				Fenetre.getPartieActuelle().interrupteursLocaux.remove(code);
			}
		}
		
		return curseurActuel+1;
	}

}
