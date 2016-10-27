package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.Fenetre;
import main.Lecteur;

/**
 * Un Autotile est un carreau liable. 
 * Selon la nature de ses voisins, il prendra une apparence diff�rente.
 */
public class Autotile {
	// Constantes
	/** fr�quence d'animation des autotiles anim�s (eau, etc.) */
	public static final int FREQUENCE_ANIMATION_AUTOTILE = 4;
	
	public static final int LARGEUR_AUTOTILE_FIXE = 3*Fenetre.TAILLE_D_UN_CARREAU;
	public static final int NOMBRE_VIGNETTES_AUTOTILE_ANIME = 4;
	public static final int LARGEUR_AUTOTILE_ANIME = NOMBRE_VIGNETTES_AUTOTILE_ANIME*LARGEUR_AUTOTILE_FIXE;
	public static final int HAUTEUR_AUTOTILE = 4*Fenetre.TAILLE_D_UN_CARREAU;
	public static final int TAILLE_MORCEAU = Fenetre.TAILLE_D_UN_CARREAU/2;
	
	//quart haut gauche
	public static final int X_PLEIN_HAUT_GAUCHE = 2;
	public static final int Y_PLEIN_HAUT_GAUCHE = 4;
	public static final int X_PLEIN_HAUT_GAUCHE_VIDE_A_DROITE = 4;
	public static final int Y_PLEIN_HAUT_GAUCHE_VIDE_A_DROITE = 4;
	public static final int X_PLEIN_HAUT_GAUCHE_VIDE_EN_BAS = 2;
	public static final int Y_PLEIN_HAUT_GAUCHE_VIDE_EN_BAS = 6;
	public static final int X_PLEIN_HAUT_GAUCHE_VIDE_EN_BAS_ET_A_DROITE = 4;
	public static final int Y_PLEIN_HAUT_GAUCHE_VIDE_EN_BAS_ET_A_DROITE = 6;
	public static final int X_COIN_RENTRANT_HAUT_GAUCHE = 4;
	public static final int Y_COIN_RENTRANT_HAUT_GAUCHE = 0;
	public static final int X_BORD_VERTICAL_HAUT_GAUCHE_LOIN_D_UN_COIN = 0;
	public static final int Y_BORD_VERTICAL_HAUT_GAUCHE_LOIN_D_UN_COIN = 4;
	public static final int X_BORD_VERTICAL_HAUT_GAUCHE_PRES_D_UN_COIN = 0;
	public static final int Y_BORD_VERTICAL_HAUT_GAUCHE_PRES_D_UN_COIN = 6;
	public static final int X_BORD_HORIZONTAL_HAUT_GAUCHE_LOIN_D_UN_COIN = 2;
	public static final int Y_BORD_HORIZONTAL_HAUT_GAUCHE_LOIN_D_UN_COIN = 2;
	public static final int X_BORD_HORIZONTAL_HAUT_GAUCHE_PRES_D_UN_COIN = 4;
	public static final int Y_BORD_HORIZONTAL_HAUT_GAUCHE_PRES_D_UN_COIN = 2;
	public static final int X_COIN_SORTANT_HAUT_GAUCHE = 0;
	public static final int Y_COIN_SORTANT_HAUT_GAUCHE = 2;
	
	//quart haut droite
	public static final int X_PLEIN_HAUT_DROITE = 3;
	public static final int Y_PLEIN_HAUT_DROITE = 4;
	public static final int X_PLEIN_HAUT_DROITE_VIDE_A_GAUCHE = 1;
	public static final int Y_PLEIN_HAUT_DROITE_VIDE_A_GAUCHE = 4;
	public static final int X_PLEIN_HAUT_DROITE_VIDE_EN_BAS = 3;
	public static final int Y_PLEIN_HAUT_DROITE_VIDE_EN_BAS = 6;
	public static final int X_PLEIN_HAUT_DROITE_VIDE_EN_BAS_ET_A_GAUCHE = 1;
	public static final int Y_PLEIN_HAUT_DROITE_VIDE_EN_BAS_ET_A_GAUCHE = 6;
	public static final int X_COIN_RENTRANT_HAUT_DROITE = 5;
	public static final int Y_COIN_RENTRANT_HAUT_DROITE = 0;
	public static final int X_BORD_VERTICAL_HAUT_DROITE_LOIN_D_UN_COIN = 5;
	public static final int Y_BORD_VERTICAL_HAUT_DROITE_LOIN_D_UN_COIN = 4;
	public static final int X_BORD_VERTICAL_HAUT_DROITE_PRES_D_UN_COIN = 5;
	public static final int Y_BORD_VERTICAL_HAUT_DROITE_PRES_D_UN_COIN = 6;
	public static final int X_BORD_HORIZONTAL_HAUT_DROITE_LOIN_D_UN_COIN = 3;
	public static final int Y_BORD_HORIZONTAL_HAUT_DROITE_LOIN_D_UN_COIN = 2;
	public static final int X_BORD_HORIZONTAL_HAUT_DROITE_PRES_D_UN_COIN = 1;
	public static final int Y_BORD_HORIZONTAL_HAUT_DROITE_PRES_D_UN_COIN = 2;
	public static final int X_COIN_SORTANT_HAUT_DROITE = 5;
	public static final int Y_COIN_SORTANT_HAUT_DROITE = 2;
	
	//quart bas gauche
	public static final int X_PLEIN_BAS_GAUCHE = 2;
	public static final int Y_PLEIN_BAS_GAUCHE = 5;
	public static final int X_PLEIN_BAS_GAUCHE_VIDE_A_DROITE = 4;
	public static final int Y_PLEIN_BAS_GAUCHE_VIDE_A_DROITE = 5;
	public static final int X_PLEIN_BAS_GAUCHE_VIDE_EN_HAUT = 2;
	public static final int Y_PLEIN_BAS_GAUCHE_VIDE_EN_HAUT = 3;
	public static final int X_PLEIN_BAS_GAUCHE_VIDE_EN_HAUT_ET_A_DROITE = 4;
	public static final int Y_PLEIN_BAS_GAUCHE_VIDE_EN_HAUT_ET_A_DROITE = 3;
	public static final int X_COIN_RENTRANT_BAS_GAUCHE = 4;
	public static final int Y_COIN_RENTRANT_BAS_GAUCHE = 1;
	public static final int X_BORD_VERTICAL_BAS_GAUCHE_LOIN_D_UN_COIN = 0;
	public static final int Y_BORD_VERTICAL_BAS_GAUCHE_LOIN_D_UN_COIN = 5;
	public static final int X_BORD_VERTICAL_BAS_GAUCHE_PRES_D_UN_COIN = 0;
	public static final int Y_BORD_VERTICAL_BAS_GAUCHE_PRES_D_UN_COIN = 3;
	public static final int X_BORD_HORIZONTAL_BAS_GAUCHE_LOIN_D_UN_COIN = 2;
	public static final int Y_BORD_HORIZONTAL_BAS_GAUCHE_LOIN_D_UN_COIN = 7;
	public static final int X_BORD_HORIZONTAL_BAS_GAUCHE_PRES_D_UN_COIN = 4;
	public static final int Y_BORD_HORIZONTAL_BAS_GAUCHE_PRES_D_UN_COIN = 7;
	public static final int X_COIN_SORTANT_BAS_GAUCHE = 0;
	public static final int Y_COIN_SORTANT_BAS_GAUCHE = 7;
	
	//quart bas droite
	public static final int X_PLEIN_BAS_DROITE = 3;
	public static final int Y_PLEIN_BAS_DROITE = 5;
	public static final int X_PLEIN_BAS_DROITE_VIDE_A_GAUCHE = 1;
	public static final int Y_PLEIN_BAS_DROITE_VIDE_A_GAUCHE = 5;
	public static final int X_PLEIN_BAS_DROITE_VIDE_EN_HAUT = 3;
	public static final int Y_PLEIN_BAS_DROITE_VIDE_EN_HAUT = 3;
	public static final int X_PLEIN_BAS_DROITE_VIDE_EN_HAUT_ET_A_GAUCHE = 1;
	public static final int Y_PLEIN_BAS_DROITE_VIDE_EN_HAUT_ET_A_GAUCHE = 3;
	public static final int X_COIN_RENTRANT_BAS_DROITE = 5;
	public static final int Y_COIN_RENTRANT_BAS_DROITE = 1;
	public static final int X_BORD_VERTICAL_BAS_DROITE_LOIN_D_UN_COIN = 5;
	public static final int Y_BORD_VERTICAL_BAS_DROITE_LOIN_D_UN_COIN = 5;
	public static final int X_BORD_VERTICAL_BAS_DROITE_PRES_D_UN_COIN = 5;
	public static final int Y_BORD_VERTICAL_BAS_DROITE_PRES_D_UN_COIN = 3;
	public static final int X_BORD_HORIZONTAL_BAS_DROITE_LOIN_D_UN_COIN = 3;
	public static final int Y_BORD_HORIZONTAL_BAS_DROITE_LOIN_D_UN_COIN = 7;
	public static final int X_BORD_HORIZONTAL_BAS_DROITE_PRES_D_UN_COIN = 1;
	public static final int Y_BORD_HORIZONTAL_BAS_DROITE_PRES_D_UN_COIN = 7;
	public static final int X_COIN_SORTANT_BAS_DROITE = 5;
	public static final int Y_COIN_SORTANT_BAS_DROITE = 7;
	
	private final int numero;
	private final Tileset tileset;
	private String nomImage;
	private BufferedImage image;
	public boolean passabilite;
	public int altitude;
	public boolean anime;
	public ArrayList<Integer> cousins;
	
	/**
	 * Constructeur explicite
	 * @param numero de l'Autotile connu par le Tileset
	 * @param nomImage nom de l'image de l'Autotile
	 * @param passabilite peut-on marcher sur cette case ?
	 * @param altitude d'affichage dans le d�cor
	 * @param cousins autres autotiles qui peuvent se lier � celui-ci
	 * @param tileset auquel appartient cet Autotile
	 * @throws IOException impossible de charger l'image de l'Autotile
	 */
	public Autotile(final int numero, final String nomImage, final boolean passabilite, final int altitude, final ArrayList<Integer> cousins, final Tileset tileset) throws IOException {
		this.tileset = tileset;
		this.numero = numero;
		
		this.nomImage = nomImage;
		this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Autotile\\"+this.nomImage));
		final int largeurAutotile = this.image.getWidth();
		if (largeurAutotile == LARGEUR_AUTOTILE_FIXE) {
			this.anime = false;
		} else if (largeurAutotile == LARGEUR_AUTOTILE_ANIME) {
			this.anime = true;
		} else {
			System.err.println("L'Autotile n'a pas la bonne taille : "+largeurAutotile);
			throw new IOException();
		}
		final int hauteurAutotile = this.image.getHeight();
		if (hauteurAutotile != HAUTEUR_AUTOTILE) {
			System.err.println("L'Autotile n'a pas la bonne taille : "+largeurAutotile);
			throw new IOException();
		}

		this.passabilite = passabilite;
		this.altitude = altitude;
		this.cousins = cousins;
	}
	
	/**
	 * Calculer l'apparence du carreau liable en fonction de son voisinnage.
	 * @param x coordonnee x de la case sur la Map (en nombre de carreaux)
	 * @param y coordonnee y de la case sur la Map (en nombre de carreaux)
	 * @param largeurMap largeur de la Map (en nombre de carreaux)
	 * @param hauteurMap hauteur de la Map (en nombre de carreaux)
	 * @param numeroCarreau num�ro de ce carreau de d�cor issu du Tileset
	 * @param layer une des trois couches de d�cor de l'�diteur de Maps
	 * @return carreau liable avec la bonne apparence
	 */
	public final BufferedImage[] calculerAutotile(final int x, final int y, final int largeurMap, final int hauteurMap, final int numeroCarreau, final int[][] layer) {
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
		if (y == 0) {
			//bord sup�rieur de l'�cran
			connexionHaut = true;
			connexionHautGauche = true;
			connexionHautDroite = true;
		} else if (y == hauteurMap-1) {
			//bord inf�rieur de l'�cran
			connexionBas = true;
			connexionBasGauche = true;
			connexionBasDroite = true;
		}
		if (x == 0) {
			//bord gauche de l'�cran
			connexionGauche = true;
			connexionHautGauche = true;
			connexionBasGauche = true;
		} else if (x == largeurMap-1) {
			//bord droit de l'�cran
			connexionDroite = true;
			connexionHautDroite = true;
			connexionBasDroite = true;
		}
		
		// On regarde les connexions possibles avec les carreaux voisins
		int numeroVoisin;
		if (!connexionHaut) {
			numeroVoisin = layer[x][y-1];
			connexionHaut = fautIlLierCeCarreauASonVoisin(numeroCarreau, numeroVoisin);
		}
		if (!connexionBas) {
			numeroVoisin = layer[x][y+1];
			connexionBas = fautIlLierCeCarreauASonVoisin(numeroCarreau, numeroVoisin);
		}
		if (!connexionGauche) {
			numeroVoisin = layer[x-1][y];
			connexionGauche = fautIlLierCeCarreauASonVoisin(numeroCarreau, numeroVoisin);
		}
		if (!connexionDroite) {
			numeroVoisin = layer[x+1][y];
			connexionDroite = fautIlLierCeCarreauASonVoisin(numeroCarreau, numeroVoisin);
		}
		
		// Selon les cas, ceux-l� ne sont pas forc�ment utiles pour dessiner le carreau
		if (!connexionHautGauche) {
			numeroVoisin = layer[x-1][y-1];
			connexionHautGauche = fautIlLierCeCarreauASonVoisin(numeroCarreau, numeroVoisin);
		}
		if (!connexionHautDroite) {
			numeroVoisin = layer[x+1][y-1];
			connexionHautDroite = fautIlLierCeCarreauASonVoisin(numeroCarreau, numeroVoisin);
		}
		if (!connexionBasGauche) {
			numeroVoisin = layer[x-1][y+1];
			connexionBasGauche = fautIlLierCeCarreauASonVoisin(numeroCarreau, numeroVoisin);
		}
		if (!connexionBasDroite) {
			numeroVoisin = layer[x+1][y+1];
			connexionBasDroite = fautIlLierCeCarreauASonVoisin(numeroCarreau, numeroVoisin);
		}
		
		final BufferedImage[] resultats = new BufferedImage[NOMBRE_VIGNETTES_AUTOTILE_ANIME];
		for (int i = 0; (this.anime&&i<NOMBRE_VIGNETTES_AUTOTILE_ANIME)||(!this.anime&&i<=0); i++) {
			final BufferedImage resultat = new BufferedImage(Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU, Lecteur.TYPE_DES_IMAGES);
			final Graphics2D g2d = (Graphics2D) resultat.createGraphics();
			int[] morceauChoisi;
			
			//quart haut-gauche du carreau
			morceauChoisi = choisirLeQuartHautGaucheDuCarreau(connexionHaut, connexionGauche, connexionHautGauche, connexionDroite, connexionBas);
			g2d.drawImage(this.image, 
					0, 0, 
					TAILLE_MORCEAU, TAILLE_MORCEAU, 
					morceauChoisi[0]*TAILLE_MORCEAU, morceauChoisi[1]*TAILLE_MORCEAU, 
					(morceauChoisi[0]+1)*TAILLE_MORCEAU, (morceauChoisi[1]+1)*TAILLE_MORCEAU, 
					null);
			
			//quart haut-droite du carreau
			morceauChoisi = choisirLeQuartHautDroiteDuCarreau(connexionHaut, connexionDroite, connexionHautDroite, connexionGauche, connexionBas);
			g2d.drawImage(this.image, 
					TAILLE_MORCEAU, 0, 
					2*TAILLE_MORCEAU, TAILLE_MORCEAU, 
					morceauChoisi[0]*TAILLE_MORCEAU, morceauChoisi[1]*TAILLE_MORCEAU, 
					(morceauChoisi[0]+1)*TAILLE_MORCEAU, (morceauChoisi[1]+1)*TAILLE_MORCEAU, 
					null);
			
			//quart bas-gauche du carreau
			morceauChoisi = choisirLeQuartBasGaucheDuCarreau(connexionBas, connexionGauche, connexionBasGauche, connexionDroite, connexionHaut);
			g2d.drawImage(this.image, 
					0, TAILLE_MORCEAU, 
					TAILLE_MORCEAU, 2*TAILLE_MORCEAU, 
					morceauChoisi[0]*TAILLE_MORCEAU, morceauChoisi[1]*TAILLE_MORCEAU, 
					(morceauChoisi[0]+1)*TAILLE_MORCEAU, (morceauChoisi[1]+1)*TAILLE_MORCEAU, 
					null);
			
			//quart bas-droite du carreau
			morceauChoisi = choisirLeQuartBasDroiteDuCarreau(connexionBas, connexionDroite, connexionBasDroite, connexionGauche, connexionHaut);
			g2d.drawImage(this.image, TAILLE_MORCEAU, TAILLE_MORCEAU, 2*TAILLE_MORCEAU, 2*TAILLE_MORCEAU, 
					morceauChoisi[0]*TAILLE_MORCEAU, morceauChoisi[1]*TAILLE_MORCEAU, 
					(morceauChoisi[0]+1)*TAILLE_MORCEAU, (morceauChoisi[1]+1)*TAILLE_MORCEAU, null);
			
			resultats[i] = resultat;
		}
		
		return resultats;
	}

	/**
	 * Est-ce que le carreau Autotile est � lier � ce voisin ?
	 * @param numeroCarreau num�ro du carreau actuel selon le Tileset
	 * @param numeroVoisin num�ro du carreau voisin selon le Tileset
	 * @return true si connexion, false si aucun lien de parent�
	 */
	private boolean fautIlLierCeCarreauASonVoisin(final int numeroCarreau, final int numeroVoisin) {
		return (numeroVoisin == numeroCarreau)
				|| this.cousins.contains(numeroVoisin)
				|| (numeroVoisin<-1 && this.tileset.autotiles.get(numeroVoisin).cousins.contains(this.numero));
	}

	/**
	 * Choisir l'apparence du quart haut-gauche du carreau Autotile.
	 * @param connexionHaut le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionGauche le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionHautGauche le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionDroite le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionBas le carreau Autotile est-il connect� � un voisin ?
	 * @return coordonn�es (en quarts de carreaux) du morceau d'Autotile � peindre sur le carreau
	 */
	private int[] choisirLeQuartHautGaucheDuCarreau(final boolean connexionHaut, final boolean connexionGauche, final boolean connexionHautGauche, final boolean connexionDroite, final boolean connexionBas) {
		int xMorceauChoisi;
		int yMorceauChoisi;
		if (connexionHaut) {
			if (connexionGauche) {
				if (connexionHautGauche) {
					if (connexionDroite) {
						if (connexionBas) {
							xMorceauChoisi = X_PLEIN_HAUT_GAUCHE;
							yMorceauChoisi = Y_PLEIN_HAUT_GAUCHE;
						} else {
							xMorceauChoisi = X_PLEIN_HAUT_GAUCHE_VIDE_A_DROITE;
							yMorceauChoisi = Y_PLEIN_HAUT_GAUCHE_VIDE_A_DROITE;
						}
					} else {
						if (connexionBas) {
							xMorceauChoisi = X_PLEIN_HAUT_GAUCHE_VIDE_EN_BAS;
							yMorceauChoisi = Y_PLEIN_HAUT_GAUCHE_VIDE_EN_BAS;
						} else {
							xMorceauChoisi = X_PLEIN_HAUT_GAUCHE_VIDE_EN_BAS_ET_A_DROITE;
							yMorceauChoisi = Y_PLEIN_HAUT_GAUCHE_VIDE_EN_BAS_ET_A_DROITE;
						}
					}
				} else {
					xMorceauChoisi = X_COIN_RENTRANT_HAUT_GAUCHE;
					yMorceauChoisi = Y_COIN_RENTRANT_HAUT_GAUCHE;
				}
			} else {
				if (connexionBas) {
					xMorceauChoisi = X_BORD_VERTICAL_HAUT_GAUCHE_LOIN_D_UN_COIN;
					yMorceauChoisi = Y_BORD_VERTICAL_HAUT_GAUCHE_LOIN_D_UN_COIN;
				} else {
					xMorceauChoisi = X_BORD_VERTICAL_HAUT_GAUCHE_PRES_D_UN_COIN;
					yMorceauChoisi = Y_BORD_VERTICAL_HAUT_GAUCHE_PRES_D_UN_COIN;
				}
				
			}
		} else {
			if (connexionGauche) {
				if (connexionDroite) {
					xMorceauChoisi = X_BORD_HORIZONTAL_HAUT_GAUCHE_LOIN_D_UN_COIN;
					yMorceauChoisi = Y_BORD_HORIZONTAL_HAUT_GAUCHE_LOIN_D_UN_COIN;
				} else {
					xMorceauChoisi = X_BORD_HORIZONTAL_HAUT_GAUCHE_PRES_D_UN_COIN;
					yMorceauChoisi = Y_BORD_HORIZONTAL_HAUT_GAUCHE_PRES_D_UN_COIN;
				}
				
			} else {
				xMorceauChoisi = X_COIN_SORTANT_HAUT_GAUCHE;
				yMorceauChoisi = Y_COIN_SORTANT_HAUT_GAUCHE;
			}
		}
		return new int[]{xMorceauChoisi, yMorceauChoisi};
	}
	
	/**
	 * Choisir l'apparence du quart haut-droite du carreau Autotile.
	 * @param connexionHaut le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionDroite le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionHautDroite le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionGauche le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionBas le carreau Autotile est-il connect� � un voisin ?
	 * @return coordonn�es (en quarts de carreaux) du morceau d'Autotile � peindre sur le carreau
	 */
	private int[] choisirLeQuartHautDroiteDuCarreau(final boolean connexionHaut, final boolean connexionDroite, final boolean connexionHautDroite, final boolean connexionGauche, final boolean connexionBas) {
		int xMorceauChoisi;
		int yMorceauChoisi;
		if (connexionHaut) {
			if (connexionDroite) {
				if (connexionHautDroite) {
					if (connexionGauche) {
						if (connexionBas) {
							xMorceauChoisi = X_PLEIN_HAUT_DROITE;
							yMorceauChoisi = Y_PLEIN_HAUT_DROITE;
						} else {
							xMorceauChoisi = X_PLEIN_HAUT_DROITE_VIDE_EN_BAS;
							yMorceauChoisi = Y_PLEIN_HAUT_DROITE_VIDE_EN_BAS;
						}
					} else {
						if (connexionBas) {
							xMorceauChoisi = X_PLEIN_HAUT_DROITE_VIDE_A_GAUCHE;
							yMorceauChoisi = Y_PLEIN_HAUT_DROITE_VIDE_A_GAUCHE;
						} else {
							xMorceauChoisi = X_PLEIN_HAUT_DROITE_VIDE_EN_BAS_ET_A_GAUCHE;
							yMorceauChoisi = Y_PLEIN_HAUT_DROITE_VIDE_EN_BAS_ET_A_GAUCHE;
						}
					}
				} else {
					xMorceauChoisi = X_COIN_RENTRANT_HAUT_DROITE;
					yMorceauChoisi = Y_COIN_RENTRANT_HAUT_DROITE;
				}
			} else {
				if (connexionBas) {
					xMorceauChoisi = X_BORD_VERTICAL_HAUT_DROITE_LOIN_D_UN_COIN;
					yMorceauChoisi = Y_BORD_VERTICAL_HAUT_DROITE_LOIN_D_UN_COIN;
				} else {
					xMorceauChoisi = X_BORD_VERTICAL_HAUT_DROITE_PRES_D_UN_COIN;
					yMorceauChoisi = Y_BORD_VERTICAL_HAUT_DROITE_PRES_D_UN_COIN;
				}
			}
		} else {
			if (connexionDroite) {
				if (connexionGauche) {
					xMorceauChoisi = X_BORD_HORIZONTAL_HAUT_DROITE_LOIN_D_UN_COIN;
					yMorceauChoisi = Y_BORD_HORIZONTAL_HAUT_DROITE_LOIN_D_UN_COIN;
				} else {
					xMorceauChoisi = X_BORD_HORIZONTAL_HAUT_DROITE_PRES_D_UN_COIN;
					yMorceauChoisi = Y_BORD_HORIZONTAL_HAUT_DROITE_PRES_D_UN_COIN;
				}
			} else {
				xMorceauChoisi = X_COIN_SORTANT_HAUT_DROITE;
				yMorceauChoisi = Y_COIN_SORTANT_HAUT_DROITE;
			}
		}
		return new int[]{xMorceauChoisi, yMorceauChoisi};
	}

	/**
	 * Choisir l'apparence du quart bas-gauche du carreau Autotile.
	 * @param connexionBas le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionGauche le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionBasGauche le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionDroite le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionHaut le carreau Autotile est-il connect� � un voisin ?
	 * @return coordonn�es (en quarts de carreaux) du morceau d'Autotile � peindre sur le carreau
	 */
	private int[] choisirLeQuartBasGaucheDuCarreau(final boolean connexionBas, final boolean connexionGauche, final boolean connexionBasGauche, final boolean connexionDroite, final boolean connexionHaut) {
		int xMorceauChoisi;
		int yMorceauChoisi;
		if (connexionBas) {
			if (connexionGauche) {
				if (connexionBasGauche) {
					if (connexionDroite) {
						if (connexionHaut) {
							xMorceauChoisi = X_PLEIN_BAS_GAUCHE;
							yMorceauChoisi = Y_PLEIN_BAS_GAUCHE;
						} else {
							xMorceauChoisi = X_PLEIN_BAS_GAUCHE_VIDE_EN_HAUT;
							yMorceauChoisi = Y_PLEIN_BAS_GAUCHE_VIDE_EN_HAUT;
						}
					} else {
						if (connexionHaut) {
							xMorceauChoisi = X_PLEIN_BAS_GAUCHE_VIDE_A_DROITE;
							yMorceauChoisi = Y_PLEIN_BAS_GAUCHE_VIDE_A_DROITE;
						} else {
							xMorceauChoisi = X_PLEIN_BAS_GAUCHE_VIDE_EN_HAUT_ET_A_DROITE;
							yMorceauChoisi = Y_PLEIN_BAS_GAUCHE_VIDE_EN_HAUT_ET_A_DROITE;
						}
					}
				} else {
					xMorceauChoisi = X_COIN_RENTRANT_BAS_GAUCHE;
					yMorceauChoisi = Y_COIN_RENTRANT_BAS_GAUCHE;
				}
			} else {
				if (connexionHaut) {
					xMorceauChoisi = X_BORD_VERTICAL_BAS_GAUCHE_LOIN_D_UN_COIN;
					yMorceauChoisi = Y_BORD_VERTICAL_BAS_GAUCHE_LOIN_D_UN_COIN;
				} else {
					xMorceauChoisi = X_BORD_VERTICAL_BAS_GAUCHE_PRES_D_UN_COIN;
					yMorceauChoisi = Y_BORD_VERTICAL_BAS_GAUCHE_PRES_D_UN_COIN;
				}
			}
		} else {
			if (connexionGauche) {
				if (connexionDroite) {
					xMorceauChoisi = X_BORD_HORIZONTAL_BAS_GAUCHE_LOIN_D_UN_COIN;
					yMorceauChoisi = Y_BORD_HORIZONTAL_BAS_GAUCHE_LOIN_D_UN_COIN;
				} else {
					xMorceauChoisi = X_BORD_HORIZONTAL_BAS_GAUCHE_PRES_D_UN_COIN;
					yMorceauChoisi = Y_BORD_HORIZONTAL_BAS_GAUCHE_PRES_D_UN_COIN;
				}
			} else {
				xMorceauChoisi = X_COIN_SORTANT_BAS_GAUCHE;
				yMorceauChoisi = Y_COIN_SORTANT_BAS_GAUCHE;
			}
		}
		return new int[]{xMorceauChoisi, yMorceauChoisi};
	}
	
	/**
	 * Choisir l'apparence du quart bas-droite du carreau Autotile.
	 * @param connexionBas le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionDroite le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionBasDroite le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionGauche le carreau Autotile est-il connect� � un voisin ?
	 * @param connexionHaut le carreau Autotile est-il connect� � un voisin ?
	 * @return coordonn�es (en quarts de carreaux) du morceau d'Autotile � peindre sur le carreau
	 */
	private int[] choisirLeQuartBasDroiteDuCarreau(final boolean connexionBas, final boolean connexionDroite, final boolean connexionBasDroite, final boolean connexionGauche, final boolean connexionHaut) {
		int xMorceauChoisi;
		int yMorceauChoisi;
		if (connexionBas) {
			if (connexionDroite) {
				if (connexionBasDroite) {
					if (connexionGauche) {
						if (connexionHaut) {
							xMorceauChoisi = X_PLEIN_BAS_DROITE;
							yMorceauChoisi = Y_PLEIN_BAS_DROITE;
						} else {
							xMorceauChoisi = X_PLEIN_BAS_DROITE_VIDE_EN_HAUT;
							yMorceauChoisi = Y_PLEIN_BAS_DROITE_VIDE_EN_HAUT;
						}
					} else {
						if (connexionHaut) {
							xMorceauChoisi = X_PLEIN_BAS_DROITE_VIDE_A_GAUCHE;
							yMorceauChoisi = Y_PLEIN_BAS_DROITE_VIDE_A_GAUCHE;
						} else {
							xMorceauChoisi = X_PLEIN_BAS_DROITE_VIDE_EN_HAUT_ET_A_GAUCHE;
							yMorceauChoisi = Y_PLEIN_BAS_DROITE_VIDE_EN_HAUT_ET_A_GAUCHE;
						}
					}
				} else {
					xMorceauChoisi = X_COIN_RENTRANT_BAS_DROITE;
					yMorceauChoisi = Y_COIN_RENTRANT_BAS_DROITE;
				}
			} else {
				if (connexionHaut) {
					xMorceauChoisi = X_BORD_VERTICAL_BAS_DROITE_LOIN_D_UN_COIN;
					yMorceauChoisi = Y_BORD_VERTICAL_BAS_DROITE_LOIN_D_UN_COIN;
				} else {
					xMorceauChoisi = X_BORD_VERTICAL_BAS_DROITE_PRES_D_UN_COIN;
					yMorceauChoisi = Y_BORD_VERTICAL_BAS_DROITE_PRES_D_UN_COIN;
				}
				
			}
		} else {
			if (connexionDroite) {
				if (connexionGauche) {
					xMorceauChoisi = X_BORD_HORIZONTAL_BAS_DROITE_LOIN_D_UN_COIN;
					yMorceauChoisi = Y_BORD_HORIZONTAL_BAS_DROITE_LOIN_D_UN_COIN;
				} else {
					xMorceauChoisi = X_BORD_HORIZONTAL_BAS_DROITE_PRES_D_UN_COIN;
					yMorceauChoisi = Y_BORD_HORIZONTAL_BAS_DROITE_PRES_D_UN_COIN;
				}
			} else {
				xMorceauChoisi = X_COIN_SORTANT_BAS_DROITE;
				yMorceauChoisi = Y_COIN_SORTANT_BAS_DROITE;
			}
		}
		return new int[]{xMorceauChoisi, yMorceauChoisi};
	}
	
}
