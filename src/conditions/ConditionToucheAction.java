package conditions;

import map.LecteurMap;
import utilitaire.GestionClavier;

/**
 * La touche action vient d'�tre press�e � l'instant.
 */
public class ConditionToucheAction extends Condition {
	
	/** Constructeur vide */
	public ConditionToucheAction() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final LecteurMap lecteur = page.event.map.lecteur;
		if (lecteur.frameActuelle > 1 //pour �viter que l'Ep�e se d�clenche en d�but de Map
		&& lecteur.frameDAppuiDeLaToucheAction+1 == lecteur.frameActuelle
		) {
			return true;
		}
		return false;
	}
	
	public final boolean estLieeAuHeros() {
		return false;
	}

}
