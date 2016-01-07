package comportementEvent;

import java.util.HashMap;

import map.Event.Direction;

/**
 * D�placer un Event dans une Direction al�atoire et d'un certain nombre de cases
 */
public class AvancerAleatoirement extends Avancer {	
	public static final int NOMBRE_DE_DIRECTIONS_POSSIBLES = 4; 
	
	/** 
	 * Constructeur explicite 
	 */
	public AvancerAleatoirement() {
		super(rand.nextInt(NOMBRE_DE_DIRECTIONS_POSSIBLES), 1);
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public AvancerAleatoirement(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final void reinitialiser() {
		super.reinitialiser();
		final int nouvelleDirection = rand.nextInt(4);
		//ne pas faire demi-tour, �a donne l'impression que l'Event ne sait pas o� il va
		if ( (direction==Direction.BAS && nouvelleDirection!=Direction.HAUT) 
				|| (direction==Direction.GAUCHE && nouvelleDirection!=Direction.DROITE) 
				|| (direction==Direction.DROITE && nouvelleDirection!=Direction.GAUCHE) 
				|| (direction==Direction.HAUT && nouvelleDirection!=Direction.BAS) 
		) {
			direction = nouvelleDirection;
		}
		
	}
}
