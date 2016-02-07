package conditions;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Balise "sinon" de la Condition situ�e parmi les Commandes Event.
 */
public class ConditionSinon extends Condition implements CommandeEvent, CommandeMenu {
	private PageEvent page;
	private ElementDeMenu element;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionSinon(final int numero) {
		this.numero = numero;
	}
	
	@Override
	public final boolean estVerifiee() {
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
	public final ElementDeMenu getElement() {
		return this.element;
	}

	@Override
	public final void setElement(final ElementDeMenu element) {
		this.element = element;
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
