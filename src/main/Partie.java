package main;

import java.util.ArrayList;

import map.Event;
import map.Map;

public class Partie {
	int numeroMap;
	Map map;
	int xHeros;
	int yHeros;
	int directionHeros;
	int vie;
	int vieMax;
	public boolean[] interrupteurs;
	public int[] variables;
	public boolean[] quetesFaites;
	public static int idArmeEquipee = -1;
	public static ArrayList<Integer> idArmesPossedees = new ArrayList<Integer>();
	
	/**
	 * le h�ros commence en map num�ro 0
	 * coordonn�es (5;5)
	 * 6 vies (sur 6 vies maximum)
	 */
	private Partie(){
		this.numeroMap = 0;
		this.xHeros = 5;
		this.yHeros = 5;
		this.directionHeros = Event.Direction.BAS;
		this.vie = 6;
		this.vieMax = 6;
		this.interrupteurs = new boolean[100];
		this.variables = new int[100];
		this.quetesFaites = new boolean[100];
		Arme.initialiserLesArmesDuJeu();
	}
	
	/**
	 * @param numeroMap num�ro de la map o� se trouve le h�ros
	 * @param xHeros coordonn�e x du h�ros
	 * @param yHeros coordonn�e y du h�ros
	 * @param vie niveau d'�nergie vitale du h�ros
	 * @param vieMax niveau maximal possible d'�nergie vitale du h�ros
	 */
	private Partie(int numeroMap, int xHeros, int yHeros, int directionHeros, int vie, int vieMax){
		this.numeroMap = numeroMap;
		this.xHeros = xHeros;
		this.yHeros = yHeros;
		this.directionHeros = directionHeros;
		this.vie = vie;
		this.vieMax = vieMax;
	}
	
	/**
	 * @return une nouvelle partie
	 */
	public static Partie creerNouvellePartie(){
		return new Partie();
	}
	
	/**
	 * Charger une partie � l'aide d'un fichier de sauvegarde.
	 * @param numeroSauvegarde num�ro de la partie sauvegard�e
	 * @return une partie sauvegard�e
	 */
	public static Partie chargerPartie(int numeroSauvegarde){
		//TODO cr�er un objet Partie � partir d'un fichier de sauvegarde
		return null;
	}
	
}
