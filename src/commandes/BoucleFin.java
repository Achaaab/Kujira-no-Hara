package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

/**
 * Une Boucle r�p�te ind�finiment les Commandes qu'elle contient.
 */
public class BoucleFin extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(BoucleFin.class);
	
	public int numero; //le num�ro de Boucle est le m�me que le num�ro de fin de Boucle qui correspond

	/**
	 * Constructeur explicite
	 * @param numero identifiant de la Boucle
	 */
	public BoucleFin(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public BoucleFin(final HashMap<String, Object> parametres) {
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
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof Boucle) {
				final Boucle debutDeBoucle = (Boucle) commande;
				if (debutDeBoucle.numero == this.numero) {
					//le d�but de Boucle a �t� trouv�
					return i;
				}
			}
		}
		//le d�but de Boucle n'a pas �t� trouv�
		LOG.error("Le d�but de boucle num�ro "+numero+" n'a pas �t� trouv� !");
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantann�es et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
