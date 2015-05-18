package main;

import java.util.ArrayList;
import map.Map;

public class Partie {
	int numeroMap;
	Map map;
	int xHeros;
	int yHeros;
	int vie;
	int vieMax;
	public boolean[] interrupteurs;
	public int[] variables;
	public boolean[] quetesFaites;
	public static int idArmeEquipee;
	public static ArrayList<Integer> idArmesPossedees = new ArrayList<Integer>();
	
	/**
	 * le h�ros commence en map num�ro 0
	 * coordonn�es (5;5)
	 * 6 vies (sur 6 vies maximum)
	 */
	private Partie(){
		numeroMap = 0;
		xHeros = 5;
		yHeros = 5;
		vie = 6;
		vieMax = 6;
		interrupteurs = new boolean[100];
		variables = new int[100];
		quetesFaites = new boolean[100];
		Arme.initialiserLesArmesDuJeu();
	}
	
	/**
	 * @param numeroMap num�ro de la map o� se trouve le h�ros
	 * @param xHeros coordonn�e x du h�ros
	 * @param yHeros coordonn�e y du h�ros
	 * @param vie niveau d'�nergie vitale du h�ros
	 * @param vieMax niveau maximal possible d'�nergie vitale du h�ros
	 */
	private Partie(int numeroMap, int xHeros, int yHeros, int vie, int vieMax){
		this.numeroMap = numeroMap;
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.vie = vie;
		this.vieMax = vieMax;
	}
	
	/**
	 * @return une nouvelle partie
	 */
	public static Partie nouvellePartie(){
		return new Partie();
	}
	
	/**
	 * @param numeroSauvegarde num�ro de la partie sauvegard�e
	 * @return une partie sauvegard�e
	 */
	public static Partie chargerPartie(int numeroSauvegarde){
		//TODO
		return null;
	}
	
}
