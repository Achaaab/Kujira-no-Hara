package conditions;

import commandes.CommandeEvent;
import map.LecteurMap;
import map.PageEvent;

/**
 * La touche action vient d'�tre press�e � l'instant.
 */
public class ConditionToucheAction extends Condition implements CommandeEvent {
	private PageEvent page;
	
	/** Constructeur vide */
	public ConditionToucheAction() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final LecteurMap lecteur = ((CommandeEvent) this).getPage().event.map.lecteur;
		if (lecteur.frameActuelle > 1 //pour �viter que l'Ep�e se d�clenche en d�but de Map
		&& lecteur.frameDAppuiDeLaToucheAction+1 == lecteur.frameActuelle
		) {
			return true;
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

	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
