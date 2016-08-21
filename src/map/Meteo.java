package map;

import java.awt.image.BufferedImage;

/**
 * Effet m�t�orologique sur la Map
 */
public abstract class Meteo {
	
	/** diff�rentes sortes d'intemp�ries */
	public enum TypeDeMeteo {
		PLUIE("pluie"), NEIGE("neige"), RIEN("rien");
		
		private String nom;
		
		/**
		 * Constructeur explicite
		 * @param nom du type d'intemp�rie
		 */
		TypeDeMeteo(final String nom) {
			this.nom = nom;		
		}
		
		/**
		 * Obtenir un type d'intemp�rie � partir de son nom.
		 * @param nom du type d'intemp�rie
		 * @return type d'intemp�rie
		 */
		public static TypeDeMeteo obtenirParNom(final String nom) {
			for (TypeDeMeteo type : TypeDeMeteo.values()) {
				if (type.nom.equalsIgnoreCase(nom)) {
					return type;
				}
			}
			return TypeDeMeteo.RIEN;
		}
	}
	
	/**
	 * Obtenir le type de cette M�t�o.
	 * @return �l�ment de l'�num�ration TypeDeMeteo
	 */
	public abstract TypeDeMeteo getType();
	public int intensite;
	
	/**
	 * Fabriquer l'image repr�sentant l'effet M�t�o.
	 * @param numeroFrame num�ro de la frame actuelle du LecteurMap
	 * @return image de l'effet M�t�o � superposer � l'�cran
	 */
	public abstract BufferedImage calculerImage(int numeroFrame);
	
	/**
	 * V�rifier si deux M�t�os sont identiques.
	 * @param m1 une m�t�o
	 * @param m2 une autre m�t�o
	 * @return si elles sont �quivalentes
	 */
	public static boolean verifierSiIdentiques(final Meteo m1, final Meteo m2) {
		if (m1 == null && m2 == null) {
			return true;
		}
		if (m1 == null && m2 != null) {
			return false;
		}
		if (m1 != null && m2 == null) {
			return false;
		}
		if (!m1.getType().equals(m2.getType())) {
			return false;
		}
		if (m1.intensite != m2.intensite) {
			return false;
		}
		return true;
	}
}
