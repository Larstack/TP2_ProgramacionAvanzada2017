package ar.edu.usal.hotel.model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;

import ar.edu.usal.hotel.model.dto.CuponesDto;
import ar.edu.usal.hotel.utils.Validador;

public class CuponesDao {

	private ArrayList<CuponesDto> cupones;

	public CuponesDao(){

		this.cupones = new ArrayList<CuponesDto>();
	}

	public void loadCupones(){

		File cuponesFile = new File("./archivos/CUPONES.txt");
		Scanner cuponesScanner;

		try {

			cuponesScanner = new Scanner(cuponesFile);

			while(cuponesScanner.hasNextLine()){

				String linea = cuponesScanner.nextLine();
				String[] cuponesArray = linea.split(";");

				for (int i = 0; i < cuponesArray.length; i++) {

					int numeroDocumento = Integer.parseInt(cuponesArray[0]);		
					String nombre = cuponesArray[1];				
					String apellido = cuponesArray[2];
					String fechaCheckInTxt = cuponesArray[3];

					Calendar fechaCheckIn = Validador.stringToCalendar(fechaCheckInTxt);

					double totalConsumido = Double.parseDouble(cuponesArray[4]);
					double descuentoCalculado = Double.parseDouble(cuponesArray[5]);
					String fechaVencimientoTxt = cuponesArray[6];

					Calendar fechaVencimiento = Validador.stringToCalendar(fechaCheckInTxt);

					CuponesDto cuponDto = new CuponesDto(numeroDocumento, nombre, apellido, fechaCheckIn, totalConsumido, descuentoCalculado, fechaVencimiento);

					cupones.add(cuponDto);
				}
			}

		}catch(InputMismatchException e){

			System.out.println("Se ha encontrado un tipo de dato insesperado.");

		}catch (FileNotFoundException e) {

			System.out.println("No se ha encontrado el archivo.");
		}

	}

	public ArrayList<CuponesDto> getCupones() {
		return cupones;
	}

	public void setCupones(ArrayList<CuponesDto> cupones) {
		this.cupones = cupones;
	}
}