package utilitaire;

/**
 * Classe utilitaire qui contient des fonctions math�matiques.
 */
public abstract class Math {
	/**
	 * Modulo
	 * @param a nombre � diviser
	 * @param b diviseur
	 * @return reste de la division de a par b
	 */
	public static int modulo(int a, final int b) {
		if (b<=0) {
			return -1;
		}
		while (a<0) {
			a += b;
		}
		while (a>=b) {
			a -= b;
		}
		return a;
	}
}
