package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Lecteur;

/**
 * Une Boucle r�p�te ind�finiment les Commandes qu'elle contient.
 */
public class Boucle extends Commande implements CommandeEvent, CommandeMenu {
	protected static final Logger LOG = LogManager.getLogger(Boucle.class);
	
	public int numero; //le num�ro de Boucle est le m�me que le num�ro de fin de Boucle qui correspond
	public long debutBoucle = -1;

	/**
	 * Constructeur explicite
	 * @param numero identifiant de la Boucle
	 */
	public Boucle(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Boucle(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero") );
	}
	
	/**
	 * Une Boucle est une Commande Event, elle peut �tre execut�e pour faire des sauts de curseur.
	 * Son execution est instantan�e.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (this.debutBoucle == -1) {
			this.debutBoucle = System.currentTimeMillis();
		}
		
		// cas o� la boucle a dur� trop longtemps
		if (System.currentTimeMillis() - this.debutBoucle >= Lecteur.DUREE_FRAME/4) {
			LOG.warn("Boucle interrompue car a dur� trop longtemps.");
			this.debutBoucle = -1;
			return curseurActuel;
		}
		
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
