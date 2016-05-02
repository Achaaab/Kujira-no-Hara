package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;

/**
 * D�placer un Event sur une case destination d�finie
 */
public class SautAbsolu extends Sauter {
	
	private int xDestination;
	private int yDestination;

	/**
	 * Constructeur explicite
	 * @param xDestination : coordonn�e x de la case destination (en nombre de carreaux)
	 * @param yDestination : coordonn�e y de la case destination (en nombre de carreaux)
	 */
	public SautAbsolu(int xDestination, int yDestination) {
		super(0, 0);
		this.xDestination = xDestination;
		this.yDestination = yDestination;
	}

	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public SautAbsolu(final HashMap<String, Object> parametres) {
		this((int) parametres.get("xDestination"),(int) parametres.get("yDestination"));
	}
	
	/**
	 * Le Mouvement est-il possible pour cet Event ?
	 * @return true si le Mouvement est possible, false sinon
	 */
	public final boolean mouvementPossible() {
		final Event eventADeplacer = this.deplacement.getEventADeplacer();
		this.x = this.xDestination - (eventADeplacer.x/Fenetre.TAILLE_D_UN_CARREAU);
		this.y = this.yDestination - (eventADeplacer.y/Fenetre.TAILLE_D_UN_CARREAU);
		return super.mouvementPossible();
	}

}
