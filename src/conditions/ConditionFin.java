package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Balise "fin" de la Condition situ�e parmi les Commandes Event.
 */
public class ConditionFin extends Condition implements CommandeEvent, CommandeMenu {
	private PageEvent page;
	private ElementDeMenu element;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 */
	public ConditionFin(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionFin(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"));
	}
	
	@Override
	public final boolean estVerifiee() {
		return true;
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
