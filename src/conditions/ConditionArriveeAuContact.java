package conditions;

import commandes.CommandeEvent;
import map.Event;
import map.PageEvent;

/**
 * Est-ce que le H�ros vient d'entrer en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le H�ros est majoritairement superpos� � lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le H�ros et l'Event se touchent par un c�t� de la Hitbox.
 */
public class ConditionArriveeAuContact extends Condition  implements CommandeEvent {
	private PageEvent page;

	@Override
	public final boolean estVerifiee() {
		final Event event = ((CommandeEvent) this).getPage().event;
		if ( event.frameDuContact != event.map.lecteur.frameActuelle ) {
			//on n'est pas � jour ! on calcule s'il y a contact :
			final ConditionContact conditionContactMaintenant = new ConditionContact();
			((CommandeEvent) conditionContactMaintenant).setPage(((CommandeEvent) this).getPage());
			conditionContactMaintenant.numero = this.numero;
			final boolean leHerosEstAuContactDeLEventMaintenant = conditionContactMaintenant.estVerifiee();
			
			event.estAuContactDuHerosAvant = event.estAuContactDuHerosMaintenant;
			event.estAuContactDuHerosMaintenant = leHerosEstAuContactDeLEventMaintenant;
			event.frameDuContact = event.map.lecteur.frameActuelle;
		}
		
		//on est � jour
		return event.estAuContactDuHerosMaintenant && !event.estAuContactDuHerosAvant;
	}
	
	/**
	 * C'est une Condition qui implique une proximit� avec le H�ros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
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
