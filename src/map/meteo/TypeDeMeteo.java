package map.meteo;

/** 
 * Diff�rentes sortes d'intemp�ries 
 */
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
		System.err.println("Effet m�t�orologique inconnu : "+nom);
		return TypeDeMeteo.RIEN;
	}
}