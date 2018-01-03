package map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Une tuile de d�cor peut �tre traversable ou solide.
 * Les quatre faces de la tuile n'ont pas forc�ment la m�me passabilit� :
 * on pourra y entrer par la gauche, mais pas forc�ment en resortir par la droite.
 */
public enum Passabilite {
	PASSABLE(0, true, true, true, true),
	GAUCHE_DROITE_HAUT(1, false, true, true, true), 
	BAS_DROITE_HAUT(2, true, false, true, true), 
	DROITE_HAUT(3, false, false, true, true), 
	BAS_GAUCHE_HAUT(4, true, true, false, true), 
	GAUCHE_HAUT(5, false, true, false, true), 
	BAS_HAUT(6, true, false, false, true),
	HAUT(7, false, false, false, true),
	BAS_GAUCHE_DROITE(8, true, true, true, false),
	GAUCHE_DROITE(9, false, true, true, false),
	BAS_DROITE(10, true, false, true, false),
	DROITE(11, false, false, true, false),
	BAS_GAUCHE(12, true, true, false, false),
	GAUCHE(13, false, true, false, false),
	BAS(14, true, false, false, false),
	OBSTACLE(15, false, false, false, false);
	
	private static final Logger LOG = LogManager.getLogger(Passabilite.class);
	private static final Passabilite[] BASE = {GAUCHE_DROITE_HAUT, BAS_DROITE_HAUT, BAS_GAUCHE_HAUT, BAS_GAUCHE_DROITE};
	final int code;
	final boolean passableEnBas;
	final boolean passableAGauche;
	final boolean passableADroite;
	final boolean passableEnHaut;
	
	/**
	 * Constructeur explicite
	 * @param code repr�sentant cette passabilit�
	 */
	Passabilite(final int code, final boolean passableEnBas, final boolean passableAGauche, final boolean passableADroite, 
			final boolean passableEnHaut) {
		this.code = code;
		this.passableEnBas = passableEnBas;
		this.passableAGauche = passableAGauche;
		this.passableADroite = passableADroite;
		this.passableEnHaut = passableEnHaut;
	}
	
	/**
	 * Obtenir la passabilit� par son code.
	 * @param code repr�sentant la passabilit� des quatre faces
	 * @return passabilit� dont c'est le code
	 */
	public static Passabilite parCode(int code) {
		if (code > OBSTACLE.code) {
			code -= 128;
		}
		for (Passabilite p : Passabilite.values()) {
			if (p.code == code) {
				return p;
			}
		}
		LOG.error("Code de passabilit� inconnu : "+code);
		return PASSABLE;
	}

	/**
	 * Ajoute les obstacles de deux Passabilit�s.
	 * @param p1 premi�re passabilit�
	 * @param p2 seconbde passabilit�
	 * @return passabilit� constitu�e des obstacles des deux
	 */
	public static Passabilite ajouter(Passabilite p1, Passabilite p2) {
		if (p1 == null) {
			return p2;
		}
		if (p2 == null) {
			return p1;
		}
		
		int codeResultat = 0;
		for (Passabilite dir : BASE) {
			if (p1.code%(2*dir.code) >= dir.code 
			 || p2.code%(2*dir.code) >= dir.code)
			{
				codeResultat += dir.code;
			}
		}
		return parCode(codeResultat);
	}
	
}
