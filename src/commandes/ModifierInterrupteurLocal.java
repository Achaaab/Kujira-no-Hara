package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteurLocal extends Commande implements CommandeEvent {
	final boolean valeurADonner;
	Integer numeroMap;
	Integer numeroEvent;
	final int numeroInterrupteurLocal;
	
	/**
	 * Constructeur explicite
	 * @param numeroMap num�ro de la Map o� se situe l'interrupteur local � modifier
	 * @param numeroEvent num�ro de l'Event auquel appartient l'interrupteur local � modifier
	 * @param numeroInterrupteurLocal 0, 1, 2 ou 3 pour dire A, B, C ou D
	 * @param valeur � donner � l'interrupteur local
	 */
	public ModifierInterrupteurLocal(final Integer numeroMap, final Integer numeroEvent, final int numeroInterrupteurLocal, final boolean valeur) {
		this.numeroMap = numeroMap; //peut �tre null si signifie "cette Map"
		this.numeroEvent = numeroEvent; //peut �tre null si signifie "cet Event"
		this.numeroInterrupteurLocal = numeroInterrupteurLocal;
		this.valeurADonner = valeur;
	}
	
	/**
	 * Constructeur implicite (cette Map, cet Event)
	 * @param numeroInterrupteurLocal 0, 1, 2 ou 3 pour dire A, B, C ou D
	 * @param valeur � donner � l'interrupteur local
	 */
	public ModifierInterrupteurLocal(final int numeroInterrupteurLocal, final boolean valeur) {
		this(null, null, numeroInterrupteurLocal, valeur);
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierInterrupteurLocal(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numeroMap") ? (int) parametres.get("numeroMap") : null,
			parametres.containsKey("numeroEvent") ? (int) parametres.get("numeroEvent") : null,
			(int) parametres.get("numeroInterrupteurLocal"),
			(boolean) parametres.get("valeurADonner")
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		//null signifie "cette Map"
		if (this.numeroMap==null) {
			this.numeroMap = this.page.event.map.numero;
		}
		//null signifie "cet Event"
		if (this.numeroEvent==null) {
			this.numeroEvent = this.page.event.numero;
		}
		
		final String code = "m"+this.numeroMap+"e"+this.numeroEvent+"i"+this.numeroInterrupteurLocal;
		final ArrayList<String> interrupteursLocaux = Fenetre.getPartieActuelle().interrupteursLocaux;
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
