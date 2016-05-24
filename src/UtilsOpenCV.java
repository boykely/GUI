import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

public class UtilsOpenCV 
{
	public static enum Filter
	{
		Moyenne,Mediane,Gauss,Simple		
	}
	public static void saveImage(BufferedImage image,String path)
	{
		String dir="C:\\Users\\valimo\\Desktop\\";
		try 
		{
			ImageIO.write(image, "jpg", new File(path+".jpg"));
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Save image error");
		}
	}
	public static Mat convertTileToCV(BufferedImage im)
	{
		Mat m=new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
		byte[] data=((DataBufferByte)im.getRaster().getDataBuffer()).getData();
		m.put(0, 0, data);
		return m;
	}	
	public static BufferedImage convertCVToTile(Mat im)
	{
		BufferedImage image=new BufferedImage(im.cols(), im.rows(), BufferedImage.TYPE_3BYTE_BGR);
		byte[] dest=((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		int taille=im.cols()*im.rows()*im.channels();
		byte[] src=new byte[taille];
		im.get(0, 0,src);
		System.arraycopy(src, 0, dest, 0,taille );
		return image;
	}
	public static void copyBI(BufferedImage src,BufferedImage dest)
	{
		byte[] s=new byte[src.getHeight()*src.getWidth()*3];
		byte[] d=new byte[src.getHeight()*src.getWidth()*3];
		s=((DataBufferByte)src.getRaster().getDataBuffer()).getData();
		d=((DataBufferByte)dest.getRaster().getDataBuffer()).getData();
		System.arraycopy(s, 0, d, 0, src.getHeight()*src.getWidth()*3);
	}
	public static int byteColorCVtoIntJava(byte b)
	{		
		int i=(b+128)+128;		
		return b>=0?(int)b:i;
	}
	public static BufferedImage fillSquareColor(Color col,Dimension dim)
	{
		BufferedImage image=new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_3BYTE_BGR);
		for(int i=0;i<dim.getHeight();i++)
		{
			for(int j=0;j<dim.getWidth();j++)
			{
				image.setRGB(j, i, col.getRGB());
			}
		}
		return image;
	}
	public static BufferedImage fillSquareColor(int col,Dimension dim)
	{
		BufferedImage image=new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_3BYTE_BGR);
		for(int i=0;i<dim.getHeight();i++)
		{
			for(int j=0;j<dim.getWidth();j++)
			{
				image.setRGB(j, i, col);
			}
		}
		return image;
	}
	public static void localFilter(BufferedImage image,List<Point> targetPixels,Dimension dim,Filter type)
	{
		if(type==Filter.Simple)
		{
			
		}
	}
}
