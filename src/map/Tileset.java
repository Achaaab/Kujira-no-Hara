package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.Fenetre;
import utilitaire.InterpreteurDeJson;

/**
 * Le Tileset associe � chaque brique de d�cor une passabilit� et une altitude.
 */
public class Tileset {
	//constantes
	public static final int LARGEUR_TILESET = 8; //chaque ligne de Tileset contient 8 carreaux
	
	/** nom du fichier JSON du Tileset */
	public final String nom;
	/** nom de l'image du Tileset */
	private final String nomImage;
	/** image compl�te du Tileset */
	private final BufferedImage image;
	/** Peut-on marcher sur cette case ? Ou bien est-ce un obstacle ? */
	public final boolean[] passabilite;
	/** Altitude d'affichage du carreau (0:sol, 2:h�ros) */
	public final int[] altitude; 
	/** carreaux d�coup�s dans l'image du Tileset */
	public final BufferedImage[] carreaux;
	
	private final String nomImagePanorama;
	public BufferedImage imagePanorama;
	private final String nomImageBrouillard;
	public BufferedImage imageBrouillard;
	
	/**
	 * Constructeur explicite
	 * @param nomTileset nom de l'image de d�cor
	 * @throws IOException erreur lors de l'ouverture du fichier JSON ou des images
	 */
	public Tileset(final String nomTileset) throws IOException {
		this.nom = nomTileset;

		final JSONObject jsonTileset = InterpreteurDeJson.ouvrirJsonTileset(nomTileset);
		
		//image du tileset
		this.nomImage = jsonTileset.getString("nomImage");
		this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Tilesets\\"+this.nomImage));
		final int nombreDeLignesTileset = (image.getHeight()/Fenetre.TAILLE_D_UN_CARREAU);
		final int nombreDeCarreauxTileset = nombreDeLignesTileset * LARGEUR_TILESET;
		
		//d�coupage des carreaux
		this.carreaux = new BufferedImage[nombreDeCarreauxTileset];
		for (int i = 0; i<LARGEUR_TILESET; i++) {
			for (int j = 0; j<nombreDeLignesTileset; j++) {
				carreaux[LARGEUR_TILESET*j + i] = this.image.getSubimage(i*Fenetre.TAILLE_D_UN_CARREAU, j*Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU);
			}
		}
		
		//lecture des passabilit�s
		this.passabilite = new boolean[nombreDeCarreauxTileset];
		final JSONArray jsonPassabilite = jsonTileset.getJSONArray("passabilite");
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.passabilite[i] = ((Integer) jsonPassabilite.get(i)) == 0;
			}
		} catch (JSONException e) {
			System.err.println("Incompatibilit� entre le tableau des passabilit�s du Tileset JSON et de l'image du Tileset : "+this.nom);
			e.printStackTrace();
		}
		
		//lecture des altitudes
		this.altitude = new int[nombreDeCarreauxTileset];
		final JSONArray jsonAltitude = jsonTileset.getJSONArray("altitude");
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.altitude[i] = (Integer) jsonAltitude.get(i);
			}
		} catch (JSONException e) {
			System.err.println("Incompatibilit� entre le tableau des altitudes du Tileset JSON et de l'image du Tileset : "+this.nom);
			e.printStackTrace();
		}
		
		//panorama
		this.nomImagePanorama = jsonTileset.getString("panorama");
		try {
			this.imagePanorama = ImageIO.read(new File(".\\ressources\\Graphics\\Panoramas\\"+this.nomImagePanorama));
		} catch (IOException e) {
			System.err.println("Pas d'image de panorama pour le Tileset : "+this.nom);
			this.imagePanorama = null;
		}
		
		//brouillard
		//TODO opacit� du brouillard, couleur, mode de superposition...
		this.nomImageBrouillard = jsonTileset.getString("brouillard");
		try {
			this.imageBrouillard = ImageIO.read(new File(".\\ressources\\Graphics\\Fogs\\"+this.nomImageBrouillard));
		} catch (IOException e) {
			System.err.println("Pas d'image de brouillard pour le Tileset : "+this.nom);
			this.imageBrouillard = null;
		}
		
		//TODO autotiles
		
		//TODO type de terrain
	}
}
