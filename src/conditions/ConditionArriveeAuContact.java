package conditions;

/**
 * Est-ce que le H�ros vient d'entrer en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le H�ros est majoritairement superpos� � lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le H�ros et l'Event se touchent par un c�t� de la Hitbox.
 */
public class ConditionArriveeAuContact extends Condition {

	@Override
	public final boolean estVerifiee() {
		//l'Event est au contact du H�ros maintenant
		final ConditionContact conditionContactMaintenant = new ConditionContact();
		conditionContactMaintenant.page = this.page;
		conditionContactMaintenant.numero = this.numero;
		final boolean reponse = conditionContactMaintenant.estVerifiee();
		
		//mais l'Event n'�tait pas encore au contact du H�ros � la frame d'avant
		if (!this.page.event.estAuContactDuHeros) {
			this.page.event.estAuContactDuHeros = reponse;
			if (reponse) {
				System.out.println("ConditionArriveeAuContact"); //TODO retirer
			}
			return reponse;
		}
		
		//ici l'Event �tait d�j� au contact du H�ros, donc ce n'est pas une arriv�e
		this.page.event.estAuContactDuHeros = reponse;
		return false;
	}

}
