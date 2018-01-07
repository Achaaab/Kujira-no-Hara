package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;
import map.PageCommune;

/**
 * Appeler une Page de code Commun.
 */
public class AppelerPageCommune extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(AppelerPageCommune.class);
	
	private int curseurInterne;
	private final int numeroPageCommune;
	private PageCommune pageCommune = null;
	
	/**
	 * Constructeur explicite
	 * @param numeroPageCommune num�ro de la Page � appeler
	 */
	public AppelerPageCommune(final int numeroPageCommune) {
		this.numeroPageCommune = numeroPageCommune;
		final ArrayList<PageCommune> pagesCommunes = Fenetre.getFenetre().lecteur.pagesCommunes;
		if (numeroPageCommune < pagesCommunes.size()) {
			this.pageCommune = Fenetre.getFenetre().lecteur.pagesCommunes.get(numeroPageCommune);
		} else {
			LOG.warn("Page commune "+numeroPageCommune+" introuvable !");
		}
		this.curseurInterne = 0;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AppelerPageCommune(final HashMap<String, Object> parametres) {
		this(
				(int) parametres.get("numeroPageCommune")
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// page commune introuvable
		if (this.pageCommune == null) {
			LOG.warn("Page commune "+numeroPageCommune+" introuvable !");
			curseurInterne = 0;
			return curseurActuel+1;
		}
		
		final ArrayList<Commande> commandesInternes = this.pageCommune.commandes;
		int nouveauCurseurInterne;
		boolean commandeInstantanee = true;
		try {
			while (commandeInstantanee) {
				nouveauCurseurInterne = this.pageCommune.commandes.get(curseurInterne).executer(curseurInterne, commandesInternes);
				commandeInstantanee = (nouveauCurseurInterne != curseurInterne);
				curseurInterne = nouveauCurseurInterne;
			}
		} catch (Exception e) {
			// la Page Commune a �t� lue en entier
			LOG.trace("La page commune a �t� lue en entier.", e);
		}
		
		if (this.curseurInterne >= this.pageCommune.commandes.size()) {
			//fini
			curseurInterne = 0;
			return curseurActuel+1;
		} else {
			//pas fini
			return curseurActuel;
		}
	}

}
