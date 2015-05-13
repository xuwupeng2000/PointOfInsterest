package domain;

public class Image {

	private String id;
	private String image;
	
public Image() {
        
    }

public Image(String id, String image) {
	
	setImage(image);
	setId(id);
    
}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String info) {
        this.image = info;
    }
}
