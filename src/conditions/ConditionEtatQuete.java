package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Partie;
import jeu.Quete.EtatQuete;
import main.Fenetre;

/**
 * V�rifie si le H�ros conna�t la Qu�te.
 */
public class ConditionEtatQuete extends Condition implements CommandeEvent, CommandeMenu {
	public int idQuete;
	private EtatQuete etatVoulu;

	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idQuete identifiant de la Qu�te � v�rifier
	 * @param etatVoulu Etat de compl�tion attendu pour cette Qu�te 
	 */
	public ConditionEtatQuete(final int numero, final int idQuete, final EtatQuete etatVoulu) {
		this.numero = numero;
		this.idQuete = idQuete;
		this.etatVoulu = etatVoulu;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ConditionEtatQuete(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(int) parametres.get("idQuete"),
			EtatQuete.getEtat( (String) parametres.get("etat") )
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		final EtatQuete etatReel = partieActuelle.quetesEtat[idQuete];
		return etatReel.equals(this.etatVoulu) 
		|| (etatVoulu.equals(EtatQuete.CONNUE) && etatReel.equals(EtatQuete.FAITE)); //une qu�te faite est connue
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le H�ros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}