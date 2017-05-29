package map.meteo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Lecteur;
import utilitaire.Maths;

/**
 * Effet m�t�orologique sur la Map.
 * La M�t�o est constitu�e de particules anim�es � l'�cran.
 */
public abstract class Meteo {
	protected static final Logger LOG = LogManager.getLogger(Meteo.class);
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
	protected abstract void ajouterUneParticule();
	
	/**
	 * Ajouter des gouttes � la pluie en fonction de l'intensit� voulue pour l'intemp�rie.
	 * @param numeroFrame num�ro de la frame actuelle du LecteurMap
	 */
	protected final void ajouterDesParticulesSiNecessaire(final int numeroFrame) {
		final int deficit = nombreDeParticulesNecessaires - recenserLesParticules();
		if (deficit > 0) {
			final int lenteurDApparition = dureeDeVieParticule / Lecteur.DUREE_FRAME;
			final int probabilite = 1000/(Lecteur.DUREE_FRAME*deficit) + lenteurDApparition;
			LOG.trace(particules.size()+"/"+nombreDeParticulesNecessaires+" => probabilit� : 1/"+probabilite);
			for (int i = 0; i<=deficit; i++) {
				if (Maths.generateurAleatoire.nextInt(probabilite+1) == 0) {
					ajouterUneParticule();
				}
			}
		}
	}
	
	/**
	 * On ne recense pas les particules sur le point de mourir, pour anticiper le renouvellement.
	 * @return nombre de particules jeunes
	 */
	private final int recenserLesParticules() {
		int compte = 0;
		for (Particule p : particules) {
			compte += p.resteAVivre;
		}
		return 2*compte/dureeDeVieParticule; //fois deux car l'�ge moyen est 50% de la dur�e de vie
	}
	
	/**
	 * Calculer la position horizontale de la particule au cours du temps.
	 * @param particule et ses caract�ristiques
	 * @return position horizontale de la particule
	 */
	protected abstract int calculerXParticule(Particule particule);
	
	/**
	 * Calculer la position verticale de la particule au cours du temps.
	 * @param particule et ses caract�ristiques
	 * @return position horizontale de la particule
	 */
	protected abstract int calculerYParticule(Particule particule);
	
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
