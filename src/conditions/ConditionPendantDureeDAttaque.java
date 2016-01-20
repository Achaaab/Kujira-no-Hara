package conditions;

import main.Arme;
import main.Fenetre;

/**
 * Si l'attaque en est au moment du coup donn�.
 * En effet, toutes les images de l'animation d'attaque ne correspondent pas au coup donn�.
 */
public class ConditionPendantDureeDAttaque extends Condition {
	
	/**
	 * Constructeur vide
	 */
	public ConditionPendantDureeDAttaque() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final int animationAttaqueActuelle = this.page.event.map.heros.animationAttaque;
		final Arme armeActuelle = Fenetre.getPartieActuelle().getArmeEquipee();
		if (armeActuelle!=null) {
			return animationAttaqueActuelle >= armeActuelle.frameDebutCoup && animationAttaqueActuelle <= armeActuelle.frameFinCoup;
		} else {
			return false;
		}
	}
	
	/**
	 * C'est une Condition qui implique une proximit� avec le H�ros.
	 * En effet, elle sert de signal du coup donn� par le H�ros � un Event avec une Arme.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}

}

