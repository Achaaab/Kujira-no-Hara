package conditions;

import java.util.HashMap;

import main.Fenetre;

/**
 * Condition pour v�rifier la valeur d'un interrupteur local d'un Event
 */
public class ConditionInterrupteurLocal extends Condition {
	boolean valeurQuIlEstCenseAvoir;
	private final Integer numeroMap;
	private final Integer numeroEvent;
	private final int numeroInterrupteurLocal;
	
	/**
	 * Constructeur explicite
	 * @param numeroMap numero de la Map o� se trouve l'interrupteur local � v�rifier
	 * @param numeroEvent num�ro de l'Event auquel appartient l'interrupteur local � v�rifier
	 * @param numeroInterrupteurLocal � v�rifier (0 A ; 1 B ; 2 C ; 3 D)
	 * @param valeur bool�enne attendue
	 */
	public ConditionInterrupteurLocal(final Integer numeroMap, final Integer numeroEvent, final int numeroInterrupteurLocal, final boolean valeur) {
		this.numeroMap = numeroMap;
		this.numeroEvent = numeroEvent;
		this.numeroInterrupteurLocal = numeroInterrupteurLocal;
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionInterrupteurLocal(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numeroMap") ? (int) parametres.get("numeroMap") : null,
			parametres.containsKey("numeroEvent") ? (int) parametres.get("numeroEvent") : null,
			(int) parametres.get("numeroInterrupteurLocal"),
			(boolean) parametres.get("valeurQuIlEstCenseAvoir")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final String code = "m"+this.numeroMap+"e"+this.numeroEvent+"i"+this.numeroInterrupteurLocal;
		return Fenetre.getPartieActuelle().interrupteursLocaux.contains(code) == valeurQuIlEstCenseAvoir;
	}

}