package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Un Autotile est un carreau liable. 
 * Selon la nature de ses voisins, il prendra une apparence diff�rente.
 */
public class Autotile {
	// Constantes
	public static int NOMBRE_VIGNETTES_AUTOTILE_ANIME = 4;
	
	private String nomImage;
	private BufferedImage image;
	public boolean passabilite;
	public int altitude;
	public boolean anime;
	public ArrayList<Integer> cousins;
	
	private Autotile (final String nomImage, final boolean passabilite, final int altitude, final ArrayList<Integer> cousins) {
		this.nomImage = nomImage;
		//this.image = f(nomImage);
		//this.anime = f(this.image);

		this.passabilite = passabilite;
		this.altitude = altitude;
		this.cousins = cousins;
	}
	
	/**
	 * Calculer l'apparence du carreau en fonction de son voisinnage.
	 * @param xEcran
	 * @param yEcran
	 * @param largeurMap
	 * @param hauteurMap
	 * @param numeroCarreau
	 * @param layer
	 * @return carreau avec la bonne apparence
	 */
	public BufferedImage calculerAutotile(final int xEcran, final int yEcran, final int largeurMap, final int hauteurMap, final int numeroCarreau, final int[][] layer) {
		// On veut d�terminer les connexions du carreau
		boolean connexionBas = false;
		boolean connexionGauche = false;
		boolean connexionDroite = false;
		boolean connexionHaut = false;
		boolean connexionBasGauche = false;
		boolean connexionBasDroite = false;
		boolean connexionHautGauche = false;
		boolean connexionHautDroite = false;
		
		// On consid�re que le bord de l'�cran est liable lui aussi
		if (yEcran == 0) {
			//bord sup�rieur de l'�cran
			connexionHaut = true;
			connexionHautGauche = true;
			connexionHautDroite = true;
		} else if (yEcran == largeurMap-1) {
			//bord inf�rieur de l'�cran
			connexionBas = true;
			connexionBasGauche = true;
			connexionBasDroite = true;
		}
		if (xEcran == 0) {
			//bord gauche de l'�cran
			connexionGauche = true;
			connexionHautGauche = true;
			connexionBasGauche = true;
		} else if (xEcran == hauteurMap-1) {
			//bord droit de l'�cran
			connexionDroite = true;
			connexionHautDroite = true;
			connexionBasDroite = true;
		}
		
		// On regarde les connexions possibles avec les carreaux voisins
		int numeroVoisin;
		if (!connexionHaut) {
			numeroVoisin = layer[xEcran][yEcran-1];
			connexionHaut = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionBas) {
			numeroVoisin = layer[xEcran][yEcran+1];
			connexionBas = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionGauche) {
			numeroVoisin = layer[xEcran-1][yEcran];
			connexionGauche = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionDroite) {
			numeroVoisin = layer[xEcran+1][yEcran];
			connexionDroite = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		
		// Selon les cas, ceux-l� ne sont pas forc�ment utiles pour dessiner le carreau
		if (!connexionHautGauche) {
			numeroVoisin = layer[xEcran-1][yEcran-1];
			connexionHaut = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionHautDroite) {
			numeroVoisin = layer[xEcran+1][yEcran-1];
			connexionDroite = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionBasGauche) {
			numeroVoisin = layer[xEcran-1][yEcran+1];
			connexionGauche = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionBasDroite) {
			numeroVoisin = layer[xEcran+1][yEcran+1];
			connexionBas = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		return null;
	}
}
