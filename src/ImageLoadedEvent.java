import java.awt.image.BufferedImage;
import java.util.EventObject;

public class ImageLoadedEvent extends EventObject 
{
	private BufferedImage _image;
	public ImageLoadedEvent(Object source,BufferedImage image) 
	{
		super(source);
		// TODO Auto-generated constructor stub
		_image=image;
	}
	public BufferedImage getImage()
	{
		return _image;
	}

}
