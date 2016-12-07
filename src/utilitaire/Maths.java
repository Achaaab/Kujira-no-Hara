package utilitaire;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe utilitaire qui contient des fonctions math�matiques.
 */
public abstract class Maths {
	private static final Logger LOG = LogManager.getLogger(Maths.class);
	
	/** G�n�rateur de nombres al�atoires */
	public static Random generateurAleatoire = new Random();
	
	/**
	 * Modulo
	 * @param a nombre � diviser
	 * @param b diviseur
	 * @return reste de la division de a par b
	 */
	public static int modulo(final int a, final int b) {
		int c = a;
		if (b<=0) {
			return -1;
		}
		while (c<0) {
			c += b;
		}
		while (c>=b) {
			c -= b;
		}
		return c;
	}
	
	/**
	 * Pourcentage
	 * @param a partie
	 * @param b tout
	 * @return pourcentage de la partie sur le tout
	 */
	public static long pourcentage(final long a, final long b) {
		return a*100/b;
	}
	
	/** 
	 * In�galit�s possibles pour comparer des valeurs 
	 */
	public enum Inegalite {
		PLUS_OU_AUTANT(">="), PLUS_STRICTEMENT(">"), MOINS_OU_AUTANT("<="), MOINS_STRICTEMENT("<"), AUTANT("=="), DIFFERENT("!=");
		
		public String symbole;
		
		/**
		 * Constructeur explicite
		 * @param symbole math�matique de comparaison
		 */
		Inegalite(final String symbole) {
			this.symbole = symbole;
		}
		
		/**
		 * Accesseur statique
		 * @param symbole math�matique de comparaison
		 * @return une des In�galit�s possibles
		 */
		public static Inegalite getInegalite(final String symbole) {
			for (Inegalite inegalite : Inegalite.values()) {
				if (inegalite.symbole.equals(symbole)) {
					return inegalite;
				}
			}
			LOG.error("Cette inegalit� n'a pas �t� trouv�e : "+symbole);
			return null;
		}
		
		/**
		 * Effectuer la comparaison math�matique sur deux valeurs.
		 * @param valeur1 � comparer
		 * @param valeur2 � comparer
		 * @return r�sultat de la comparaison
		 */
		public boolean comparer(final double valeur1, final double valeur2) {
			switch(this) {
			case AUTANT:
				return valeur1 == valeur2;
			case PLUS_OU_AUTANT:
				return valeur1 >= valeur2;
			case PLUS_STRICTEMENT:
				return valeur1 > valeur2;
			case MOINS_STRICTEMENT:
				return valeur1 < valeur2;
			case MOINS_OU_AUTANT:
				return valeur1 <= valeur2;
			case DIFFERENT:
				return valeur1 != valeur2;
			default:
				LOG.error("In�galit� inconnue : " + this.symbole);
				return false;
			}
		}
	}
	
}
