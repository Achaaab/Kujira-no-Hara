package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

public abstract class Lecteur {
	public BufferedImage ecranAtuel;
	public Fenetre fenetre = null;
	public static int dureeFrame = 30; //en millisecondes
	public static int imageType = BufferedImage.TYPE_INT_ARGB;
	public Boolean allume = true; //dit si le lecteur est allum�
	public int frameActuelle = 0; //chaque frame passe et le temps s'�coule - Baudelaire

	public abstract  BufferedImage calculerAffichage();
	public abstract void keyPressed(int keycode);
	public abstract void keyReleased(Integer keycode);
	
	public BufferedImage ecranNoir(){
		int largeur = Fenetre.largeurParDefaut;
		int hauteur = Fenetre.hauteurParDefaut;
		BufferedImage image = new BufferedImage(largeur, hauteur, imageType);
		int couleur = new Color(0,0,0,0).getRGB();
		for(int i=0; i<largeur; i++){
			for(int j=0; j<hauteur; j++){
				image.setRGB(i, j, couleur);
			}
		}
		return image;
	}
	
	public BufferedImage imageVide(int largeur, int hauteur){
		BufferedImage image = new BufferedImage(largeur, hauteur, imageType);
		int couleur = new Color(0,0,0,0).getRGB();
		for(int i=0; i<largeur; i++){
			for(int j=0; j<hauteur; j++){
				image.setRGB(i, j, couleur);
			}
		}
		return image;
	}
	
	public static void sauvegarderImage(BufferedImage image){
		try {
			File outputfile = new File("C:\\Users\\Pierre\\Pictures\\saved.png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage superposerImages(BufferedImage ecran, BufferedImage image2, int x, int y){
		BufferedImage image1 = ecran;
		int largeur = image1.getWidth();
		int hauteur = image1.getHeight();
		BufferedImage image3 = new BufferedImage (largeur, hauteur, imageType);
		Graphics2D g2d = image3.createGraphics ();
		g2d.drawImage (image1, null, 0, 0);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g2d.drawImage (image2, null, x, y);
		g2d.dispose ();
		return image3;
	}
	
	public void demarrer(){
		allume = true;
		System.out.println("Nouveau lecteur d�marr�");
		while(allume){
			Date d1 = new Date();
			ecranAtuel = calculerAffichage();
			Date d2 = new Date();
			while(d2.getTime()-d1.getTime() < dureeFrame){
				d2 = new Date();
			}
			fenetre.actualiserAffichage(ecranAtuel);
			frameActuelle++;
			//System.out.println(d2.getTime()-d1.getTime());
		}
		System.out.println("Lecteur actuel arr�t� � la frame "+frameActuelle);
	}
	
}
