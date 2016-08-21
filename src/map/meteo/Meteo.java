package map.meteo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utilitaire.Maths;

/**
 * Effet m�t�orologique sur la Map.
 * La M�t�o est constitu�e de particules anim�es � l'�cran.
 */
public abstract class Meteo {
	protected static int dureeDeVieParticule;
	protected static int ratioIntensiteNombreDeParticules;
	
	/**
	 * Obtenir le type de cette M�t�o.
	 * @return �l�ment de l'�num�ration TypeDeMeteo
	 */
	public abstract TypeDeMeteo getType();
	public int intensite;
	protected int nombreDeParticulesNecessaires;
	protected ArrayList<Particule> particules = new ArrayList<Particule>();
	
	/**
	 * Fabriquer l'image repr�sentant l'effet M�t�o.
	 * @param numeroFrame num�ro de la frame actuelle du LecteurMap
	 * @return image de l'effet M�t�o � superposer � l'�cran
	 */
	public abstract BufferedImage calculerImage(int numeroFrame);

	/**
	 * Ajouter une goutte � la pluie.
	 */
	protected abstract void ajouterUneGoutte();
	
	/**
	 * Ajouter des gouttes � la pluie en fonction de l'intensit� voulue pour l'intemp�rie.
	 * @param numeroFrame num�ro de la frame actuelle du LecteurMap
	 */
	protected final void ajouterDesParticulesSiNecessaire(final int numeroFrame) {
		if (particules.size() < nombreDeParticulesNecessaires) {
			//il faut rajouter des gouttes
			if (dureeDeVieParticule <= nombreDeParticulesNecessaires
			&& numeroFrame % (nombreDeParticulesNecessaires/dureeDeVieParticule) == 0) {
				//ajouter les gouttes au fur et � mesure, pour �viter qu'elles arrivent toutes en groupe
				ajouterUneGoutte();
				if (Maths.generateurAleatoire.nextInt(2) == 1) {
					ajouterUneGoutte();
				}
			} else {
				//ajouter plusieurs gouttes � la fois car elles disparaissent plus vite qu'elles apparaissent
				for (int i = 0; i<=nombreDeParticulesNecessaires/dureeDeVieParticule; i++) {
					ajouterUneGoutte();
				}
			}
		}
	}
	
	/**
	 * Calculer la position horizontae de la particule au cours du temps.
	 * @param particule et ses caract�ristiques
	 * @return position horizontale de la particule
	 */
	protected abstract int calculerXParticule(final Particule particule);
	
	/**
	 * Calculer la position verticale de la particule au cours du temps.
	 * @param particule et ses caract�ristiques
	 * @return position horizontale de la particule
	 */
	protected abstract int calculerYParticule(final Particule particule);
	
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
