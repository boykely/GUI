import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.tools.JavaFileObject;
import javax.xml.crypto.Data;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Main 
{
	public static int gl=0;
	public static void main(String[] args) throws HeadlessException, IndexOutOfBoundsException, IOException
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);		
		JFrame frame=new JFrame("Traitement d'images");
		frame.setResizable(false);
		frame.setPreferredSize(new Dimension(1600,800));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);		
		FlowLayout layout=new FlowLayout();
		frame.setLayout(layout);
		frame.pack();
		GUIHandler gui=new GUIHandler(frame);
		try
		{
			JLabel displayLabel=new JLabel();gui.setDisplayLabel(displayLabel);
			displayLabel.setName("MainLabel");
			displayLabel.addMouseListener(gui);displayLabel.addMouseMotionListener(gui);
			JPanel paramPanel=new JPanel();
			JScrollPane scrollDisplay=new JScrollPane(displayLabel);
			JScrollPane scrollParam=new JScrollPane(paramPanel);
			scrollDisplay.setPreferredSize(new Dimension(750,400));
			scrollParam.setPreferredSize(new Dimension(550,400));
			BoxLayout box=new BoxLayout(paramPanel, BoxLayout.Y_AXIS);
			paramPanel.setLayout(box);
			//Contenu du paramPanel
			//first param
			JPanel firstPanel=new JPanel();
			BoxLayout firstParam=new BoxLayout(firstPanel,BoxLayout.X_AXIS);
			firstPanel.setLayout(firstParam);
			JButton btn_load=new JButton("Ouvrir");btn_load.setName("Ouvrir");
			JButton btn_cam=new JButton("Camera");btn_cam.setName("Camera");
			btn_load.addActionListener(gui);
			btn_cam.addActionListener(gui);
			firstPanel.add(btn_load);
			firstPanel.add(btn_cam);
			//
			//secod param
			JPanel secondPanel=new JPanel();secondPanel.setEnabled(false);
			GroupLayout gr1=new GroupLayout(secondPanel);
			gr1.setAutoCreateContainerGaps(true);
			gr1.setAutoCreateGaps(true);
			secondPanel.setLayout(gr1);
			JCheckBox chk_second=new  JCheckBox("Activer filtre");chk_second.setName("ActiverFiltre");
			chk_second.addChangeListener(gui);
			JComboBox cbx_second=new JComboBox<>(new String[]{
				"Pick Color","Moyenne","Mediane","Gaussian","Sharpening"	
			});
			cbx_second.setName("SelectFilter");
			cbx_second.addActionListener(gui);
			JLabel lbl_second=new JLabel("Largeur du filtre:");
			JSlider sld_second=new JSlider(JSlider.HORIZONTAL, 3, 25, 3);sld_second.setName("Kernel");
			sld_second.addChangeListener(gui);
			sld_second.setMajorTickSpacing(10);
			sld_second.setMinorTickSpacing(2);
			sld_second.setPaintTicks(true);
			sld_second.setPaintLabels(true);
			JLabel lbl_second_col=new JLabel();lbl_second_col.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
			gui.setLabelPickColor(lbl_second_col);
			lbl_second_col.setIcon(new ImageIcon(UtilsOpenCV.fillSquareColor(new Color(0,0,0), new Dimension(35,35))));
			JCheckBox chk_second_pick=new JCheckBox("Activer Pick Color");chk_second_pick.setName("ActivePickColor");
			chk_second_pick.addChangeListener(gui);
			JButton btn_second=new JButton("Appliquer");btn_second.setName("AppliquerFiltre");
			JButton btn_second_reset=new JButton("Reset");btn_second_reset.setName("Reset");
			btn_second.addActionListener(gui);
			btn_second_reset.addActionListener(gui);
			gr1.setHorizontalGroup(gr1.createSequentialGroup()
					.addGroup(gr1.createParallelGroup(Alignment.LEADING).addComponent(chk_second).addComponent(lbl_second).addComponent(chk_second_pick))
					.addGroup(gr1.createParallelGroup(Alignment.LEADING).addComponent(cbx_second).addComponent(sld_second).addComponent(lbl_second_col).addComponent(btn_second))
					.addComponent(btn_second_reset)
					);
			gr1.setVerticalGroup(gr1.createSequentialGroup()
					.addGroup(gr1.createParallelGroup(Alignment.BASELINE).addComponent(chk_second).addComponent(cbx_second))
					.addGroup(gr1.createParallelGroup(Alignment.LEADING).addComponent(lbl_second).addComponent(sld_second))
					.addGroup(gr1.createParallelGroup(Alignment.LEADING).addComponent(chk_second_pick).addComponent(lbl_second_col))
					.addGroup(gr1.createParallelGroup(Alignment.LEADING).addComponent(btn_second).addComponent(btn_second_reset))
					);
			//
			//last param
			JPanel lastPanel=new JPanel();
			BoxLayout boxLast=new BoxLayout(lastPanel,BoxLayout.LINE_AXIS);
			lastPanel.setLayout(boxLast);
			JButton btn_save=new JButton("Enregistrer");btn_save.setName("Enregistrer");
			btn_save.addActionListener(gui);
			lastPanel.add(btn_save);
			//
			//
			//Ajouter tous les param child panel au param panel 
			paramPanel.add(firstPanel);
			paramPanel.add(secondPanel);
			paramPanel.add(lastPanel);
			//
			frame.add(scrollDisplay);
			frame.add(scrollParam);			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		frame.setVisible(true);
	}	
	public static void saveImage(BufferedImage image,String filename)
	{
		String dir="C:\\Users\\valimo\\Desktop\\";
		try 
		{
			ImageIO.write(image, "jpg", new File(dir+filename+".jpg"));
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Mat MirrorImage(Mat im)
	{
		Mat res=new Mat(im.rows(),im.cols(),CvType.CV_8UC3);
		int k;
		byte[] data=new byte[3];
		for(int i=0;i<im.rows();i++)
		{
			k=im.cols();
			for(int j=0;j<im.cols();j++)
			{
				im.get(i, k,data);
				res.put(i, j,data);
				k--;
			}
		}
		return res;
	}
	public static Mat RotateImage(Mat im,double theta)
	{
		int rows=im.rows();
		int cols=im.cols();
		Mat res=new Mat(rows*2,cols*2,CvType.CV_8UC3);
		byte[] org=new byte[3];
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{
				im.get(i, j,org);
				int[] rot=ChangeBaseA(rows, cols, new int[]{i,j});
				rot=rot(rot, theta);
				int[] finalRot=ChangeBaseB(rot);
				res.put(finalRot[0], finalRot[1], org);
			}
		}
		return res;
	}
	/*
	 * vecteur xy répresente coordonnées (ligne,colonne)=(x,y)
	 * retourne le coordonnées (ligne,colonne)
	 * Il faut faire attention au répère
	 */
	public static int[] ChangeBaseA(int rows,int cols,int[] xy)
	{
		int Ox=rows/2;
		int Oy=cols/2;
		return new int[]{
			-1*xy[0]+Ox,
			1*xy[1]+Oy
		};
	}
	public static int[] ChangeBaseB(int[] xy)
	{
		return new int[]{
			1*xy[0]+(0),
			1*xy[1]+(0)
		};
	}
	public static int[] rot(int[] xy,double theta)
	{
		theta=(180*theta)/Math.PI;
		int[] res=new int[2];
		res[0]=(int)(xy[1]*Math.sin(theta)+xy[0]*Math.cos(theta));//ligne => y'
		res[1]=(int)(xy[1]*Math.cos(theta)-xy[0]*Math.sin(theta));//colonne => x'
		return res;
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
	public static int byteColorCVtoIntJava(byte b)
	{		
		int i=(b+128)+128;		
		return b>=0?(int)b:i;
	}
	public static void Histogramme(Mat m,int[] hist)
	{		
		//for Grey level images
		byte[] pixel=new byte[3];
		for(int i=0;i<m.rows();i++)
		{
			for(int j=0;j<m.cols();j++)
			{					
				m.get(i, j,pixel);				
				hist[byteColorCVtoIntJava(pixel[0])]+=1;				
			}
		}
	}
	public static void HistogrammeRGB(Mat m,int[] R,int[] G,int[] B)
	{
		byte[] col=new byte[3];
		for(int i=0;i<m.rows();i++)
		{
			for(int j=0;j<m.cols();j++)
			{					
				m.get(i, j,col);
				R[byteColorCVtoIntJava(col[2])]+=1;
				G[byteColorCVtoIntJava(col[1])]+=1;
				B[byteColorCVtoIntJava(col[0])]+=1;				
			}
		}
	}
	public static void HistogrammeHSV(BufferedImage image,float[] H,float[]S)
	{
		float[] hsb;
		int c;
		Color col;
		for(int i=0;i<image.getHeight();i++)
		{
			for(int j=0;j<image.getWidth();j++)
			{
				c=image.getRGB(j, i);
				col=new Color(c);
				hsb=Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
				//System.out.println("R:"+col.getRed()+"G:"+col.getGreen()+"B:"+col.getBlue()+"<>H:"+hsb[0]+"S:"+hsb[1]+"V(B):"+hsb[2]);
				
			}
		}
	}
	public static void HistogrammeCumuleRGB(Mat m,int[] R,int[] G,int[] B,int[] RC,int[] GC,int[] BC,int N)
	{
		//System.out.println("Le nombre total des pixel:"+N);
		int valueR=0;int valueG=0;int valueB=0;
		for(int i=0;i<256;i++)
		{
			valueR+=R[i];RC[i]=R[i]==0?0:valueR;
			valueG+=G[i];GC[i]=G[i]==0?0:valueG;
			valueB+=B[i];BC[i]=B[i]==0?0:valueB;
			
		}
	}
	public static void InverseHistogrammeRGB(int[] RC,int[] GC,int[] BC,Hashtable<Integer, Integer> InvHistoCumulR,Hashtable<Integer, Integer> InvHistoCumulG,Hashtable<Integer, Integer> InvHistoCumulB )
	{
		for(int i=0;i<256;i++)
		{				
			InvHistoCumulR.put(RC[i], i);
			InvHistoCumulG.put(GC[i], i);
			InvHistoCumulB.put(BC[i], i);			
		}
	}
	public static void HistogrammeCumule(Mat m,int[] hist,int[] histoCumul)
	{
		int value=0;		
		for (int i=0;i<256;i++)
		{							    
		    value+=hist[i];
		    histoCumul[i]=value;		    
		}
	}
	public static void InverseHistogrammeCumule(int[] histoCumul,Hashtable<Integer, Integer> InvHistoCumul)
	{
		for(int i=0;i<histoCumul.length;i++)
		{			
			InvHistoCumul.put(histoCumul[i], i);
		}
	}
	public static void MatchingHistogram(Mat imRef,Mat imTarget,Mat result)
	{
		//imRef => image de référence
		//imTarget => image à changer d'histogramme comme l'imRef
		int[] RRef=new int[256];int[] RCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulRRef=new Hashtable<>();
		int[] GRef=new int[256];int[] GCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulGRef=new Hashtable<>();
		int[] BRef=new int[256];int[] BCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulBRef=new Hashtable<>();
		int[] RTar=new int[256];int[] RCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulRTar=new Hashtable<>();
		int[] GTar=new int[256];int[] GCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulGTar=new Hashtable<>();
		int[] BTar=new int[256];int[] BCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulBTar=new Hashtable<>();
		int N=imRef.cols()*imRef.rows();
		HistogrammeRGB(imRef, RRef, GRef, BRef);
		HistogrammeCumuleRGB(imRef, RRef, GRef, BRef,RCRef,GCRef,BCRef,N);
		InverseHistogrammeRGB(RCRef, GCRef, BCRef, InvHistoCumulRRef, InvHistoCumulGRef, InvHistoCumulBRef);
		
		HistogrammeRGB(imTarget, RTar, GTar, BTar);
		HistogrammeCumuleRGB(imTarget, RTar, GTar, BTar,RCTar,GCTar,BCTar,N);
		InverseHistogrammeRGB(RCTar, GCTar, BCTar, InvHistoCumulRTar, InvHistoCumulGTar, InvHistoCumulBTar);		
		byte[] pixel=new byte[3];
		byte[] pixelTarget=new byte[3];
		//on va essayer de calculer le gradient du ref 
		//Imgproc.Sobel(imTarget, imTarget, imTarget.depth(), 1, 1);
		for(int i=0;i<imRef.rows();i++)
		{
			for(int j=0;j<imRef.cols();j++)
			{
				imRef.get(i, j,pixel);
				imTarget.get(i, j,pixelTarget);
				byte blue=pixelTarget[0];byte green=pixelTarget[1];byte red=pixelTarget[2];				
				int r=minimum(RCTar[byteColorCVtoIntJava(red)],RCRef,InvHistoCumulRRef);
				int g=minimum(GCTar[byteColorCVtoIntJava(green)],GCRef,InvHistoCumulGRef);
				int b=minimum(BCTar[byteColorCVtoIntJava(blue)],BCRef,InvHistoCumulBRef);		
				pixel[0]=b>256?(byte)255:(byte)b;
				pixel[1]=b>256?(byte)255:(byte)g;
				pixel[2]=b>256?(byte)255:(byte)r;				
				result.put(i, j, pixel);
			}
		}
	}
	public static Mat TextureMatching(Mat imRef,Mat imTar,Mat result,int n)
	{
		List<Mat> pyramidRef=new ArrayList<>();
		List<Mat> pyramidTar=new ArrayList<>();
		List<Mat> gaussRef=new ArrayList<>();
		List<Mat> gaussTar=new ArrayList<>();
		Mat tempRef=imRef.clone();
		Mat tempTar=imTar.clone();
		createLaplacianPyramid(tempRef, n, pyramidRef,gaussRef);
		//createLaplacianPyramid(tempTar, n, pyramidTar,gaussTar);	
		MatchingHistogram(imRef, imTar, imTar);
		int i=0;		
		createLaplacianPyramid(tempTar, n, pyramidTar,gaussTar);
		while(i<n)
		{
			//result=new Mat();
			MatchingHistogram(pyramidRef.get(i),pyramidTar.get(i), pyramidTar.get(i));
			//result=pyramidTar.get(i).clone();
			i++;
		}
		System.out.println("CollapsePyramid:"+gaussTar.size()+"/"+pyramidTar.size());
		result=collapsePyramid(pyramidTar,gaussTar);
		MatchingHistogram(imRef, result, result);
		
		return result;
	}
	public static void EqualHistogram(Mat im,Mat result)
	{
		int[] RRef=new int[256];int[] RCRef=new int[256];
		int[] GRef=new int[256];int[] GCRef=new int[256];
		int[] BRef=new int[256];int[] BCRef=new int[256];
		int N=im.cols()*im.rows();
		HistogrammeRGB(im, RRef, GRef, BRef);
		HistogrammeCumuleRGB(im, RRef, GRef, BRef,RCRef,GCRef,BCRef,N);
		byte[] pixel=new byte[3];
		//System.out.println(im);System.out.println(result);
		for(int i=0;i<im.rows();i++)
		{
			for(int j=0;j<im.cols();j++)
			{
				im.get(i, j,pixel);
				double r=RCRef[byteColorCVtoIntJava(pixel[2])]*255/N;
				double g=GCRef[byteColorCVtoIntJava(pixel[1])]*255/N;
				double b=BCRef[byteColorCVtoIntJava(pixel[0])]*255/N;
				result.put(i, j, new double[]{b,g,r});
			}
		}
	}
	public static void LaplacianPyramid(Mat src,Mat dest,List<Mat> gauss)
	{		
		Mat temp=src.clone();		
		Imgproc.pyrDown(temp, dest);
		Imgproc.pyrUp(dest, dest,temp.size());
		gauss.add(dest.clone());//we will use it to collapse-pyramid		
		//temp=dest.clone();		
		Core.subtract(src, dest, dest);
		//Core.add(dest, temp, dest);//Si on veut retrouver l'image originale Gi 		
	}
	public static Mat collapsePyramid(List<Mat> pyramid,List<Mat>gauss)
	{
		/*Si Core.add dans LaplacianPyramid
		 * alors i>0
		 * on ne fait que imgproc.pyrUp
		 * Sinon i>=0 et Core.add
		 * NB:Si les 2 images (Ref et Tar sont très différents => il vaut mieux utiliser Imgproc.pyrUp sinon Core.add
		 */
		int i=pyramid.size()-1;
		Mat temp=new Mat();		
		while(i>=0)
		{
			//Imgproc.pyrUp(pyramid.get(i), temp,pyramid.get(i-1).size());			
			Core.add(pyramid.get(i), gauss.get(i), temp);
			//
			i--;
		}
		System.out.println("valeur de i="+i);
		System.out.println(temp);
		return temp;
	}
	public static void createLaplacianPyramid(Mat dest,int n,List<Mat>pyramid,List<Mat>gauss)
	{
		int i=0;
		Mat temp=dest.clone();
		while(i<n)
		{			
			LaplacianPyramid(temp, dest,gauss);
			pyramid.add(dest.clone());			
			Imgproc.pyrDown(temp, temp);			
			i++;
		}
	}
	public static double convertDouble(double x)
	{		
		return ((int)(x*100000000))/100000000.;
	}
	public static int minimum(int value,int[] cumul,Hashtable<Integer, Integer>inv_cumul)
	{
		int[] temp=new int[256];
		Hashtable<Integer, Integer>tempHash=new Hashtable<>();
		for(int i=0;i<256;i++)
		{
			temp[i]=Math.abs(value-cumul[i]);
			tempHash.put(temp[i], cumul[i]);
		}
		//trions temp pour avoir la minimal
		int a=0;
		for(int i=0;i<255;i++)
		{
			for(int j=i+1;j<256;j++)
			{
				if(temp[i]>temp[j])
				{
					a=temp[j];
					temp[j]=temp[i];
					temp[i]=a;
				}
			}
		}
		//
		return inv_cumul.get(tempHash.get(temp[0]));
	}
}
