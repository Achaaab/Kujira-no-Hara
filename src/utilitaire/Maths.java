package utilitaire;

/**
 * Classe utilitaire qui contient des fonctions math�matiques.
 */
public abstract class Maths {
	
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
			System.err.println("Cette inegalit� n'a pas �t� trouv�e : "+symbole);
			return null;
		}
	}
	
}
