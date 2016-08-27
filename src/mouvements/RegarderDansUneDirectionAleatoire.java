package mouvements;

import java.util.HashMap;

import map.Event.Direction;
import utilitaire.Maths;

/**
 * D�placer un Event dans une Direction al�atoire et d'un certain nombre de cases
 */
public class RegarderDansUneDirectionAleatoire extends RegarderDansUneDirection {
	//constantes
	public static final int NOMBRE_DE_DIRECTIONS_POSSIBLES = 4; 
	
	/** 
	 * Constructeur explicite 
	 */
	public RegarderDansUneDirectionAleatoire() {
		super(Maths.generateurAleatoire.nextInt(NOMBRE_DE_DIRECTIONS_POSSIBLES));
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public RegarderDansUneDirectionAleatoire(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final void reinitialiserSpecifique() {
		int nouvelleDirection = Maths.generateurAleatoire.nextInt(NOMBRE_DE_DIRECTIONS_POSSIBLES);
		//ne pas utiliser la m�me direction que la direction actuelle sinon aucun changement
		if ( (  this.direction==Direction.BAS && nouvelleDirection==Direction.BAS) 
			|| (this.direction==Direction.GAUCHE && nouvelleDirection==Direction.GAUCHE) 
			|| (this.direction==Direction.DROITE && nouvelleDirection==Direction.DROITE) 
			|| (this.direction==Direction.HAUT && nouvelleDirection==Direction.HAUT) 
		) {
			nouvelleDirection += (1 + Maths.generateurAleatoire.nextInt(NOMBRE_DE_DIRECTIONS_POSSIBLES-1));
			nouvelleDirection = Maths.modulo(nouvelleDirection, NOMBRE_DE_DIRECTIONS_POSSIBLES);
		}
		this.direction = nouvelleDirection;
	}
}
