package ar.edu.usal.hotel.model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

import ar.edu.usal.hotel.model.dto.Habitaciones;
import ar.edu.usal.hotel.model.dto.Precios;

public class HabitacionesDao {
	
	public static final int MAX_HABITACIONES = 150;
	
	private static HabitacionesDao habitacionesDaoInstance = null;
	
	public static final char[] CATEGORIAS_VALIDAS = {'A', 'B', 'C'};
	
	private Habitaciones[] habitaciones;
	
	private HabitacionesDao(){
		
		this.habitaciones = new Habitaciones[MAX_HABITACIONES];
		this.loadHabitaciones();
	}
	
	public static HabitacionesDao getInstance(){
		
		if(habitacionesDaoInstance==null){
			
			habitacionesDaoInstance = new HabitacionesDao();
		}
		
		return habitacionesDaoInstance;
	}
	
	private void loadHabitaciones(){
		
		File habitacionesTxt = new File("./archivos/HABITACIONES.txt");
		Scanner habitacionesScanner;
		
		PreciosDao precios = new PreciosDao();
		precios.loadPrecios();
		
		try {
			
			habitacionesScanner = new Scanner(habitacionesTxt);
			
			while(habitacionesScanner.hasNextLine()){
				
				String linea = habitacionesScanner.nextLine();
				String[] habitacionArray = linea.split(";");
				
				int numero = Integer.parseInt(habitacionArray[0]);
				int capacidad = Integer.parseInt(habitacionArray[1]);
				char categoria = habitacionArray[2].charAt(0);
				boolean tieneBalcon = Boolean.parseBoolean(habitacionArray[3]);
				String comentario = habitacionArray[4];
				
				Habitaciones habitacion = new Habitaciones(numero, capacidad, categoria, tieneBalcon, comentario);
				Precios precio = precios.cargarPrecioHabitacion(categoria, capacidad);
			
				if(!this.agregarHabitacion(habitacion)){
					System.out.println("ERROR! Ya se han cargado las 150 habitaciones.");
				}
			}
			
		}catch(InputMismatchException e){
			
			System.out.println("Se ha encontrado un tipo de dato insesperado.");
			
		}catch (FileNotFoundException e) {
			
			System.out.println("No se ha encontrado el archivo.");
		}
	}

	public boolean agregarHabitacion(Habitaciones habitacion){
		
		for (int i = 0; i < this.habitaciones.length; i++) {
			
			if(habitaciones[i] == null){
				
				habitaciones[i] = habitacion;
				return true;
			}
		}
		
		return false;
	}
	
	public Habitaciones[] getHabitaciones() {
		return habitaciones;
	}
	
	
}
