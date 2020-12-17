package telran.accounting.api;

import telran.lostfound.domain.entities.Address;
import telran.lostfound.domain.entities.LostFoundEntity;

public class ResponsePostDto {
	
	public String id;
	public boolean typePost;
	public String userLogin;
	public String userName;
	public String avatar;
	public String datePost;	
	public String type;
	public String sex;
	public String breed;
	public String[] tags;
	public String[] photos;
	public Address address;
	public double[] location;
	
	public ResponsePostDto(LostFoundEntity ent) {
		super();
		this.id = ent.getId();
		this.typePost = ent.getTypePost();
		this.userLogin = ent.getUserLogin();
		this.userName = ent.getUserName();
		this.avatar = ent.getAvatar();
		this.datePost = ent.getDatePost();
		this.type = ent.getType();
		this.sex = ent.getSex();
		this.breed = ent.getBreed();
		this.tags = ent.getTags();
		this.photos = ent.getPhotos();
		this.address = ent.getAddress();
		this.location = ent.getLocation();
	}
	
	public ResponsePostDto() {
		super();
	}
	
	

}
