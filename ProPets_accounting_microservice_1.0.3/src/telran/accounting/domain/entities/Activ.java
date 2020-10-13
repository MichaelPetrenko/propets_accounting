package telran.accounting.domain.entities;

import java.util.HashSet;

public class Activ {

	HashSet<String> message;
	HashSet<String> lostFound;
	HashSet<String> hotels;
	
	public Activ() {
		super();
		this.message = new HashSet<String>();
		this.lostFound = new HashSet<String>();
		this.hotels = new HashSet<String>();
	}

	public HashSet<String> getMessage() {
		return message;
	}

	public HashSet<String> getLostFound() {
		return lostFound;
	}

	public HashSet<String> getHotels() {
		return hotels;
	}

	public void setMessage(HashSet<String> message) {
		this.message = message;
	}

	public void setLostFound(HashSet<String> lostFound) {
		this.lostFound = lostFound;
	}

	public void setHotels(HashSet<String> hotels) {
		this.hotels = hotels;
	}

}
