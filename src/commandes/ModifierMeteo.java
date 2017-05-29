package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;
import map.meteo.Aigrettes;
import map.meteo.Meteo;
import map.meteo.Neige;
import map.meteo.Pluie;
import map.meteo.TypeDeMeteo;

/**
 * Changer l'effet m�t�orologique actuel de la Map.
 */
public class ModifierMeteo extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ModifierMeteo.class);
	
	private final TypeDeMeteo typeDeMeteo;
	private int intensite;
	
	/**
	 * Constructeur explicite
	 * @param nom de l'intemp�rie souhait�e
	 * @param intensite de l'intemp�rie souhait�e
	 */
	public ModifierMeteo(final String nom, final int intensite) {
		this.typeDeMeteo = TypeDeMeteo.obtenirParNom(nom);
		LOG.info("Nouvelle m�t�o : "+this.typeDeMeteo.nom);
		this.intensite = intensite;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierMeteo(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("type") ? (String) parametres.get("type") : null,
			  parametres.containsKey("intensite") ? (int) parametres.get("intensite") : 0	
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Meteo nouvelleMeteo = null;
		
		switch (this.typeDeMeteo) {
		case PLUIE:
			nouvelleMeteo = new Pluie(this.intensite);
			break;
		case NEIGE:
			nouvelleMeteo = new Neige(this.intensite);
			break;
		case AIGRETTES:
			nouvelleMeteo = new Aigrettes(this.intensite);
			break;
		default:
			break;
		}
		
		if (Meteo.verifierSiIdentiques(nouvelleMeteo, Fenetre.getPartieActuelle().meteo)) {
			LOG.warn("Cette m�t�o est identique � l'ancienne, on ne fait rien."+ nouvelleMeteo.getType()+" "+Fenetre.getPartieActuelle().meteo.getType());
		} else {
			//la nouvelle m�t�o propos�e est diff�rente de l'ancienne
			Fenetre.getPartieActuelle().meteo = nouvelleMeteo;
		}
		
		return curseurActuel + 1;
	}
}
