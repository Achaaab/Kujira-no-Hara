package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import map.LecteurMap;
import utilitaire.GestionClavier;

/**
 * La touche action vient d'�tre press�e � l'instant.
 */
public class ConditionTouche extends Condition implements CommandeEvent {
	
	private final GestionClavier.ToucheRole toucheRole;
	
	/** 
	 * Constructeur explicite
	 * @param touche � v�rifier 
	 */
	public ConditionTouche(GestionClavier.ToucheRole touche) {
		this.toucheRole = touche;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionTouche(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("touche") ? GestionClavier.ToucheRole.getToucheRole((String) parametres.get("touche")) : null);
	}
	
	@Override
	public final boolean estVerifiee() {
		final LecteurMap lecteur = this.page.event.map.lecteur;
		if (lecteur.frameActuelle > 1) { //pour �viter que l'Ep�e se d�clenche en d�but de Map
			final Integer frameDAppui = this.toucheRole.touche.frameDAppui;
			if (frameDAppui != null && frameDAppui + 1 == lecteur.frameActuelle) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}
