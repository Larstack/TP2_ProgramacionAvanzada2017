package ar.edu.usal.hotel.model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;

import ar.edu.usal.hotel.exception.ClienteNoRegistradoException;
import ar.edu.usal.hotel.exception.ProductoInexistenteException;
import ar.edu.usal.hotel.model.dto.Clientes;
import ar.edu.usal.hotel.model.dto.Cupones;
import ar.edu.usal.hotel.model.dto.Productos;
import ar.edu.usal.hotel.utils.Validador;

public class ProductosDao {
	
	private static ProductosDao productosDaoInstance = null;
	private ArrayList<Productos> productos;
	
	private ProductosDao(){

		this.productos = new ArrayList<Productos>();
		this.loadProductos();
	}

	public static ProductosDao getInstance() {
		
		if(productosDaoInstance == null){
			 
			productosDaoInstance = new ProductosDao();
		}
		
		return productosDaoInstance;
	}
	
	private void loadProductos() {
		
		File productosFile = new File("./archivos/PRODUCTOS.txt");
		
		Scanner productosScanner;
		
		try {
			
			try {
				productosFile.createNewFile();
			
			} catch (IOException e) {

				System.out.println("Se ha verificado un error al cargar el archivo de productos.");
			}
			
			productosScanner = new Scanner(productosFile);
				
			while(productosScanner.hasNextLine()){
				
				String productoTxt = productosScanner.nextLine();
				
				String codigo = productoTxt.substring(0, 3).trim();
				String descripcion = productoTxt.substring(3, 33).trim();
				double precio = Double.parseDouble(productoTxt.substring(33, 40).trim());
				
				Productos producto = new Productos(codigo, descripcion, precio); 
				
				this.productos.add(producto);
			}
			
			productosScanner.close();
			
		}catch(InputMismatchException e){
			
			System.out.println("Se ha encontrado un tipo de dato insesperado.");
			
		}catch (FileNotFoundException e) {
			
			System.out.println("No se ha encontrado el archivo.");
		}catch(Exception e){
			
			System.out.println("Se ha verificado un error inesperado.");
		}		
	}
	
	public ArrayList<Productos> getProductos() {
		return productos;
	}

	public Productos loadProductoPorCodigo(String codigo) throws ProductoInexistenteException {
		
		for (int i = 0; i < this.getProductos().size(); i++) {

			Productos producto = this.getProductos().get(i);

			if(producto.getCodigo().trim().equals(codigo) || Validador.fillString(codigo, 3, "0", true).equals(producto.getCodigo().trim())){
				
				return producto;
			}
		}

		throw new ProductoInexistenteException(codigo);
	}
}
