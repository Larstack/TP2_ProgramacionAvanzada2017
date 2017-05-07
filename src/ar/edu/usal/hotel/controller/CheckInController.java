package ar.edu.usal.hotel.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ar.edu.usal.hotel.exception.CategoriaNoValidaException;
import ar.edu.usal.hotel.exception.ClienteNoRegistradoException;
import ar.edu.usal.hotel.exception.NumeroHabitacionNoValidoException;
import ar.edu.usal.hotel.model.dao.ClientesDao;
import ar.edu.usal.hotel.model.dao.ClientesHabitacionDao;
import ar.edu.usal.hotel.model.dao.HabitacionesDao;
import ar.edu.usal.hotel.model.dto.Clientes;
import ar.edu.usal.hotel.model.dto.ClientesHabitacion;
import ar.edu.usal.hotel.model.dto.Cupones;
import ar.edu.usal.hotel.model.dto.Habitaciones;
import ar.edu.usal.hotel.utils.Validador;
import ar.edu.usal.hotel.view.CheckInView;

public class CheckInController {

	private CheckInView checkInView;
	private ArrayList<Clientes> clientesCheckIn;

	public void checkIn() {
		
		this.prepararDatosClientes();
		this.asignarHabitacion();
	}

	public void prepararDatosClientes() {

		this.clientesCheckIn = new ArrayList();
		checkInView = new CheckInView();
		boolean agregarOtroCliente = true;
		
		do{
			int numeroDocumento = checkInView.ingresarDatosPreliminares();
			
			Clientes cliente = null;
			
			try {
				
				cliente = this.checkClienteRegistrado(numeroDocumento);
			
			} catch (ClienteNoRegistradoException e) {
				
				e.printStackTrace();
				cliente = e.registrarCliente(checkInView);
			}
			
			this.clientesCheckIn.add(cliente);
						
			agregarOtroCliente = checkInView.agregarOtroPasajero();
			
		}while(agregarOtroCliente);
	}

	private Clientes checkClienteRegistrado(int numeroDocumento) throws ClienteNoRegistradoException{

		String datosCliente = "";
		
		HabitacionesDao habitacionesDao = HabitacionesDao.getInstance();
		ClientesDao clientes = ClientesDao.getInstance();

		for (int i = 0; i < clientes.getClientes().size(); i++) {

			Clientes cliente = clientes.getClientes().get(i);

			if(cliente.getNumeroDocumento() == numeroDocumento){

				Calendar fechaNacimientoCalendar = cliente.getFechaNacimiento();
				String fechaNacimiento = Validador.calendarToString(fechaNacimientoCalendar, "dd-MM-yyyy");

				Cupones cuponCliente = cliente.getCupon();
				String tieneCupon = cuponCliente!=null ? "El cliente tiene cupones de descuento." : 
					"El cliente no posee cupones de descuento.";

				datosCliente = 
						"Nombre: " + cliente.getNombre() + "\n"
								+ "Apellido: " + cliente.getApellido() + "\n"
								+ "Fecha Nacimiento: " + fechaNacimiento + "\n"
								+ tieneCupon;

				checkInView.mostrarDatosCliente(datosCliente);
				
				return cliente;
			}
		}

		throw new ClienteNoRegistradoException(numeroDocumento);
	}
	

	
	private void asignarHabitacion() {
		
		boolean categoriaValida = false; 
		char categoria;
		
		do{
			
			categoria = checkInView.ingresarCategoria();
			
			try {
				
				categoriaValida = this.validarCategoria(categoria);
				
			} catch (CategoriaNoValidaException e) {
				
				e.printStackTrace();
			}
			
		}while(!categoriaValida);
			
		int diasPermanencia = checkInView.ingresarDiasPermanencia();
		boolean tieneBalcon = checkInView.quiereBalcon();
		int capacidad = clientesCheckIn.size();
		
		HabitacionesDao habitacionesDao = HabitacionesDao.getInstance();
		Habitaciones[] habitaciones = habitacionesDao.getHabitaciones();
		
		Habitaciones habitacionCheckIn = null;
		
		ArrayList<Habitaciones> habitacionesTmp = new ArrayList<Habitaciones>();
		for (int i = 0; i < habitaciones.length; i++) {
			
			Habitaciones habitacionIterada = habitaciones[i];
			if(habitacionIterada.getCapacidad() == capacidad && habitacionIterada.isDisponible()){
				
				habitacionesTmp.add(habitacionIterada);
				
				if(habitacionIterada.getCategoria() == categoria
						&& habitacionIterada.isTieneBalcon() == tieneBalcon){
					
					habitacionCheckIn = habitacionIterada;
					break;
				}
			}
		}
		
		ArrayList<String> habitacionesAconsejadas = new ArrayList();
		
		if(habitacionCheckIn == null && !habitacionesTmp.isEmpty()){
			
			for (int i = 0; i < habitacionesTmp.size(); i++) {
				
				Habitaciones habitacionAconsejada = habitacionesTmp.get(i);
				String habitacionString = "";
				
				habitacionString += " NUMERO: " + habitacionAconsejada.getNumero() + "\n"
						+ "CATEGORIA: " + habitacionAconsejada.getCategoria() + "\n"
						+ "PRECIO: " + habitacionAconsejada.getPrecio().getPrecio();
				
				habitacionesAconsejadas.add(habitacionString);
			}
			
			int numeroHabitacionRequerida;
			
			boolean numeroHabitacionValido = false;
			do{
				numeroHabitacionRequerida = checkInView.ingresarHabitacionRequerida(habitacionesAconsejadas);
				
				try {
					
					numeroHabitacionValido = this.validarNumeroHabitacion(habitacionesTmp, numeroHabitacionRequerida);
				
				} catch (NumeroHabitacionNoValidoException e) {
				
					e.printStackTrace();
				}
				
			}while(!numeroHabitacionValido);
			
			for (int i = 0; i < habitacionesTmp.size(); i++) {
				
				Habitaciones habitacionIterada = habitacionesTmp.get(i);
				
				if(habitacionIterada.getNumero() ==numeroHabitacionRequerida){
					
					habitacionCheckIn = habitacionIterada;
					break;
				}
			}
		}	
		
		String fechaEgresoString = null;
		
		if(habitacionCheckIn != null){
			ClientesHabitacionDao clientesHabitacionDao = ClientesHabitacionDao.getInstance();
			Calendar fechaIngreso = Calendar.getInstance();
			fechaIngreso.setTime(new Date());
			Calendar fechaEgreso = clientesHabitacionDao.calcularFechaEgreso(diasPermanencia);
			
			ClientesHabitacion clientesHabitacionCheckIn = new ClientesHabitacion(
					clientesCheckIn, habitacionCheckIn, diasPermanencia, fechaEgreso, fechaIngreso);
			
			clientesHabitacionDao.getClientesHabitacion().add(clientesHabitacionCheckIn);
			
			habitacionCheckIn.setDisponible(false);
			
			fechaEgresoString = Validador.calendarToString(fechaEgreso, "dd-MM-yyyy");
		}
		
		checkInView.mostrarFechaEgreso(fechaEgresoString);
	}
	
	private boolean validarNumeroHabitacion(ArrayList<Habitaciones> habitacionesTmp, int numeroHabitacion) throws NumeroHabitacionNoValidoException {
		
		for (int i = 0; i < habitacionesTmp.size(); i++) {
			
			if(habitacionesTmp.get(i).getNumero() == numeroHabitacion)
				return true;
		}
		
		throw new NumeroHabitacionNoValidoException(numeroHabitacion);
	}

	private boolean validarCategoria(char categoria) throws CategoriaNoValidaException{
		
		for (int i = 0; i < HabitacionesDao.CATEGORIAS_VALIDAS.length; i++) {
			
			if(categoria == HabitacionesDao.CATEGORIAS_VALIDAS[i]){
				
				return true;
			}
		}
		
		throw new CategoriaNoValidaException(categoria);
	}
}

