package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import jeu.courrier.EtatCourrier;
import jeu.courrier.LettreAEnvoyer;
import jeu.courrier.Poste;
import main.Commande;
import main.Main;

/**
 * Envoyer une lettre a la poste.
 */
public class EnvoyerUneLettre extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(EnvoyerUneLettre.class);

	private final int idLettre;
	
	/**
	 * Constructeur explicite
	 * @param idLettre identifiant de la lettre a envoyer
	 */
	public EnvoyerUneLettre(final int idLettre) {
		this.idLettre = idLettre;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public EnvoyerUneLettre(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idLettre"));
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partie = Main.getPartieActuelle();
		final LettreAEnvoyer lettre = partie.lettresAEnvoyer.get(this.idLettre);
		if (EtatCourrier.PAS_ENVOYEE.equals(lettre.etat)) {
			if (StringUtils.isNotEmpty(lettre.texte)) {
				final String responseHttp = Poste.envoyerDuCourrier(lettre);
				if (responseHttp.contains("ok")) { //TODO comment reconnaitre le succes ?
					// Succes de l'envoi
					LOG.info("La lettre "+idLettre+" a pu �tre envoy�e.");
					lettre.etat = EtatCourrier.ENVOYEE_PAS_REPONDUE;
					
				} else {
					// Echec de l'envoi
					LOG.error("La lettre "+idLettre+" n'a pas pu �tre envoy�e !");
				}
				
			} else {
				LOG.error("La lettre "+idLettre+" est vierge !");
			}
		} else {
			LOG.error("La lettre "+idLettre+" a d�j� �t� envoy�e !");
		}
		return curseurActuel+1;
	}

}
