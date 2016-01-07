package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Fenetre;
import map.Event;

/**
 * D�placer un Event dans une Direction et d'un certain nombre de cases
 */
public class Avancer extends CommandeEvent {
	protected int direction;
	public int nombreDeCarreaux;
	public int ceQuiAEteFait = 0; //avanc�e en pixel, doit atteindre nombreDeCarreaux*32
	
	/**
	 * Constructeur explicite
	 * @param direction dans laquelle l'Event doit avancer
	 * @param nombreDeCarreaux distance parcourue
	 */
	public Avancer(final Integer direction, final Integer nombreDeCarreaux) {
		this.direction = direction;
		this.nombreDeCarreaux = nombreDeCarreaux;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Avancer(final HashMap<String, Object> parametres) {
		this( (Integer) parametres.get("direction"), 
			  (Integer) parametres.get("nombreDeCarreaux") );
	}
	
	/**
	 * Obtenir la Direction du mouvement
	 * @return la Direction du mouvement
	 */
	public final int getDirection() {
		return direction;
	}
	
	/**
	 * Si la Page de comportement doit �tre rejou�e, il faut r�initialiser cette Commande.
	 * R�initialiser un mouvement le d�clare non fait, et change la direction en cas de mouvement al�atoire.
	 */
	public void reinitialiser() {
		ceQuiAEteFait = 0;
	}

	@Override
	public int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		final Event event = this.page.event;
		if (ceQuiAEteFait >= nombreDeCarreaux*Fenetre.TAILLE_D_UN_CARREAU) {
			event.avance = false; //le mouvement est termin�
			return curseurActuel+1;
		}
		event.avance = true; //le mouvement est en cours
		event.deplacer();
		return curseurActuel;
	}
}
