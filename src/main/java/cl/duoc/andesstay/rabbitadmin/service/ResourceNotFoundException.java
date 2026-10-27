package cl.duoc.andesstay.rabbitadmin.service;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
