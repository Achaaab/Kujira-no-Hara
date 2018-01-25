package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Main;

/**
 * Cr�er une nouvelle Partie vierge et y jouer
 */
public class OuvrirNouvellePartie extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(OuvrirNouvellePartie.class);
	
	/**
	 * Constructeur vide
	 */
	private OuvrirNouvellePartie() {
		
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public OuvrirNouvellePartie(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		LOG.info("nouvelle partie");
		Main.setPartieActuelle(null);
		Main.ouvrirLaPartie();
		
		return curseurActuel+1;
	}
	
}
