package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Event;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteurLocal extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ModifierInterrupteurLocal.class);
	
	final boolean valeurADonner;
	Integer numeroMap;
	Integer idEvent;
	final int numeroInterrupteurLocal;
	
	/**
	 * Constructeur explicite
	 * @param numeroMap num�ro de la Map o� se situe l'interrupteur local � modifier
	 * @param idEvent id de l'Event auquel appartient l'interrupteur local � modifier
	 * @param numeroInterrupteurLocal 0, 1, 2 ou 3 pour dire A, B, C ou D
	 * @param valeur � donner � l'interrupteur local
	 */
	public ModifierInterrupteurLocal(final Integer numeroMap, final Integer idEvent, final int numeroInterrupteurLocal, final boolean valeur) {
		this.numeroMap = numeroMap; //peut �tre null si signifie "cette Map"
		this.idEvent = idEvent; //peut �tre null si signifie "cet Event"
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
			parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null,
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
		if (this.idEvent==null) {
			this.idEvent = this.page.event.id;
		}
		
		final String code = "m"+this.numeroMap+"e"+this.idEvent+"i"+this.numeroInterrupteurLocal;
		final ArrayList<String> interrupteursLocaux = getPartieActuelle().interrupteursLocaux;
		if (valeurADonner) {
			if (!interrupteursLocaux.contains(code)) {
				getPartieActuelle().interrupteursLocaux.add(code);
				LOG.debug("Interrupteur local "+code+" allum�.");
			}
		} else {
			if (interrupteursLocaux.contains(code)) {
				getPartieActuelle().interrupteursLocaux.remove(code);
				LOG.debug("Interrupteur local "+code+" �teint.");
			}
		}
		return curseurActuel+1;
	}

	/**
	 * R�initialiser les interrupteurs locaux en rapport avec cet Event.
	 * @param event � r�initialiser
	 */
	public static void reinitialiserEvent(final Event event) {
		final String debutDuCode = "m" + event.map.numero + "e" + event.id;
		LOG.debug("R�initialisation des interrupteurs locaux de l'event "+debutDuCode);
		int tailleListe = getPartieActuelle().interrupteursLocaux.size();
		String code;
		for (int i = 0; i<tailleListe; i++) {
			code = getPartieActuelle().interrupteursLocaux.get(i);
			if (code.startsWith(debutDuCode)) {
				LOG.trace("R�initialisation de l'interrupteur local "+code);
				getPartieActuelle().interrupteursLocaux.remove(i);
				tailleListe--;
			}
		}
	}

}
