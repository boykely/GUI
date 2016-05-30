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
		PickColor,Moyenne,Mediane,Gauss,Sharpening	
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
	public static void convertCVToTile(Mat im,BufferedImage image)
	{
		byte[] dst=((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		im.get(0, 0,dst);
		
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
	public static void localFilter(BufferedImage image,List<Point> targetPixels,Dimension dim,Filter type,Color color)
	{
		Mat im=convertTileToCV(image);
		int[][] noyau=kernel(dim.height);
		int size=dim.height*dim.width;
		double red=0,green=0,blue=0;
		if(type==Filter.PickColor)
		{
			System.out.println("pickcolor");
			Point p;
			byte[] pixel=new byte[3];
			for(int z=0;z<targetPixels.size();z++)
			{
				p=targetPixels.get(z);
				for(int k=0;k<noyau.length;k++)
				{
					int[] coord=noyau[k];
					int i=(int)p.x+coord[0];
					int j=(int)p.y+coord[1];
					//on test chaque coordonnées maintenant
					if(coord[0]<=0)
					{
						//on est dans la partie <0 du noyau
						if(i<=0)
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					else
					{
						if(i>=im.rows())
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					if(coord[1]<=0)
					{
						if(j<=0)
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					else
					{
						if(j>=im.cols())
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					im.get(i, j,pixel);
					red=color.getRed();
					green=color.getGreen();
					blue=color.getBlue();
					im.put(i, j,new byte[]{(byte)blue,(byte)green,(byte)red});
				}
			}
			convertCVToTile(im, image);
		}
		else if(type==Filter.Moyenne)
		{
			System.out.println("filtre moyenne à traiter");
			Point p;
			byte[] pixel=new byte[3];
			for(int z=0;z<targetPixels.size();z++)
			{
				p=targetPixels.get(z);
				for(int k=0;k<noyau.length;k++)
				{
					int[] coord=noyau[k];
					int i=(int)p.x+coord[0];
					int j=(int)p.y+coord[1];
					//on test chaque coordonnées maintenant
					if(coord[0]<=0)
					{
						//on est dans la partie <0 du noyau
						if(i<=0)
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					else
					{
						if(i>=im.rows())
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					if(coord[1]<=0)
					{
						if(j<=0)
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					else
					{
						if(j>=im.cols())
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					im.get(i, j,pixel);
					red+=byteColorCVtoIntJava(pixel[2]);
					green+=byteColorCVtoIntJava(pixel[1]);
					blue+=byteColorCVtoIntJava(pixel[0]);
					//im.put(i, j,new byte[]{(byte)blue,(byte)green,(byte)red});
				}
				//on répasse la boucle pour re assigner les voisins
				red/=size;
				green/=size;
				blue/=size;
				for(int k=0;k<noyau.length;k++)
				{
					int[] coord=noyau[k];
					int i=(int)p.x+coord[0];
					int j=(int)p.y+coord[1];
					//on test chaque coordonnées maintenant
					if(coord[0]<=0)
					{
						//on est dans la partie <0 du noyau
						if(i<=0)
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					else
					{
						if(i>=im.rows())
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					if(coord[1]<=0)
					{
						if(j<=0)
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					else
					{
						if(j>=im.cols())
						{
							i=(int)p.x;
							j=(int)p.y;
						}
					}
					im.put(i, j,new byte[]{(byte)blue,(byte)green,(byte)red});
				}
			}
			convertCVToTile(im, image);
		}
		else if(type==Filter.Gauss)
		{
			System.out.println("filtre gauss à traiter");
		}
		else if(type==Filter.Mediane)
		{
			System.out.println("filtre Mediane à traiter");
		}
		else if(type==Filter.Sharpening)
		{
			System.out.println("filtre passe haute à traiter");
		}
	}
	public static int[][] kernel(int n)
	{
		if(n%2==0)return null;
		int[][] k=new int[n*n][];
		int demi=(int)Math.round((double)n/2);
		int z=0;
		for(int i=-(demi-1);i<demi;i++)
		{
			for(int j=-(demi-1);j<demi;j++)
			{
				k[z]=new int[]{i,j};
				z++;
			}
		}
		return k;
	}
}
