package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import conditions.ConditionContact.TypeDeContact;
import main.Fenetre;
import map.Event;

/**
 * Est-ce que le H�ros vient d'entrer en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le H�ros est majoritairement superpos� � lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le H�ros et l'Event se touchent par un c�t� de la Hitbox.
 */
public class ConditionArriveeAuContact extends Condition  implements CommandeEvent {

	private final TypeDeContact typeDeContact;
	
	private ConditionArriveeAuContact(final int numero, final TypeDeContact typeDeContact) {
		this.numero = numero;
		this.typeDeContact = typeDeContact;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionArriveeAuContact(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
				parametres.containsKey("typeDeContact") ? TypeDeContact.obtenirParNom((String) parametres.get("typeDeContact")) : null
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Event event = this.page.event;
		final int frameActuelle = Fenetre.getFenetre().lecteur.frameActuelle;
		if ( event.frameDuContact != frameActuelle) {
			//on n'est pas � jour ! on calcule s'il y a contact :
			final ConditionContact conditionContactMaintenant = new ConditionContact(this.numero, this.typeDeContact);
			conditionContactMaintenant.page = this.page;
			final boolean leHerosEstAuContactDeLEventMaintenant = conditionContactMaintenant.estVerifiee();
			
			event.estAuContactDuHerosAvant = event.estAuContactDuHerosMaintenant;
			event.estAuContactDuHerosMaintenant = leHerosEstAuContactDeLEventMaintenant;
			event.frameDuContact = frameActuelle;
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

}
