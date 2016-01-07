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
	public final Boolean estVerifiee() {
		final int animationAttaqueActuelle = this.page.event.map.heros.animationAttaque;
		final Arme armeActuelle = Fenetre.getPartieActuelle().getArmeEquipee();
		final Boolean reponse = animationAttaqueActuelle >= armeActuelle.frameDebutCoup && animationAttaqueActuelle <= armeActuelle.frameFinCoup;
		return reponse;
	}

}

