import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Mat;
import org.opencv.core.Point;


public class GUIHandler implements MouseListener,MouseMotionListener, ChangeListener,ActionListener,ImageLoadedListener
{
	public boolean Clicked=false;
	public BufferedImage image;
	private BufferedImage resetImage; 
	private JFileChooser dlg;
	private JFrame mainFrame;
	private JLabel displayLabel;
	private JLabel labelPickColor;
	private boolean secondParamActivate=false;
	private boolean mousePressed=false;
	private boolean pickColor=false;
	private List<Point> targetPixels;
	private int kernelSize;
	private Color color;
	private UtilsOpenCV.Filter filterType;
	
	public GUIHandler(JFrame frame)
	{
		mainFrame=frame;
		targetPixels=new ArrayList<>();
		kernelSize=3;
		filterType=UtilsOpenCV.Filter.PickColor;
	}
	public void setDisplayLabel(JLabel lbl)
	{
		displayLabel=lbl;
	}
	public void setLabelPickColor(JLabel lbl)
	{
		labelPickColor=lbl;
	}
	private void pickColorFromImage(MouseEvent e)
	{
		int col=e.getX();
		int ligne=e.getY();
		if(col>image.getWidth() || ligne>image.getHeight())return;
		int c=image.getRGB(col, ligne);
		color=new Color(c);
		labelPickColor.setIcon(new ImageIcon(UtilsOpenCV.fillSquareColor(color, new Dimension(35,35))));
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		Object o=e.getSource();
		if(o.getClass().getName()==JLabel.class.getName())
		{
			JLabel lbl=(JLabel)o;
			if(lbl.getName()=="MainLabel" && image!=null)
			{
				fillTargetPixels(e);
			}
		}
		if(pickColor && image!=null)
		{
			pickColorFromImage(e);
			targetPixels.clear();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		Component comp=(Component)e.getComponent();
		if(comp.getName()=="MainLabel" && secondParamActivate)mousePressed=true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		Component comp=(Component)e.getComponent();
		//if(comp.getName()=="MainLabel" && secondParamActivate)mousePressed=false;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		Object o=e.getSource();
		if(o.getClass().getName()==JCheckBox.class.getName())
		{
			JCheckBox chk=(JCheckBox)o;
			if(chk.getName()=="ActiverFiltre")
			{
				secondParamActivate=!secondParamActivate;
				if(secondParamActivate)
				{
					displayLabel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				}
				else
				{
					displayLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}			
			}
			if(chk.getName()=="ActivePickColor")
			{
				pickColor=!pickColor;
			}
		}
		else if(o.getClass().getName()==JSlider.class.getName())
		{
			JSlider sld=(JSlider)o;
			if(sld.getName()=="Kernel")
			{
				kernelSize=sld.getValue();
				if(sld.getValue()%2==0)
				{
					kernelSize=sld.getValue()+1;
					sld.setValue(kernelSize);
				}
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object o=e.getSource();
		if(o.getClass().getName()==JButton.class.getName())
		{
			JButton btn=(JButton)o;
			if(btn.getName()=="Ouvrir")
			{
				openImage();
			}
			if(btn.getName()=="Camera")
			{
				try
				{
					CamDialog cam=new CamDialog(mainFrame,displayLabel);
					cam.addImageLoadedListener(this);
					Thread th=new Thread(cam);
					th.start();
				}
				catch(Exception ex)
				{
					System.err.println("Erreur lors de la cam");
				}
			}
			if(btn.getName()=="AppliquerFiltre")
			{
				if(targetPixels.size()!=0 && image!=null)
				{
					UtilsOpenCV.localFilter(image, targetPixels, new Dimension(kernelSize,kernelSize),filterType,color);
					displayLabel.setIcon(new ImageIcon(image));
				}
				targetPixels.clear();
			}
			if(btn.getName()=="Reset")
			{
				if(resetImage!=null)displayLabel.setIcon(new ImageIcon(resetImage));
				targetPixels.clear();
				UpdateImageReference();
			}
			if(btn.getName()=="Enregistrer")
			{
				saveImage();
			}
		}
		else if(o.getClass().getName()==JComboBox.class.getName())
		{
			JComboBox cb=(JComboBox)o;
			if(cb.getName()=="SelectFilter")
			{
				String selected=(String)cb.getSelectedItem();
				if(selected=="Pick Color")filterType=UtilsOpenCV.Filter.PickColor;
				else if(selected=="Moyenne")filterType=UtilsOpenCV.Filter.Moyenne;
				else if(selected=="Mediane")filterType=UtilsOpenCV.Filter.Mediane;
				else if(selected=="Sharpening")filterType=UtilsOpenCV.Filter.Sharpening;
				else if(selected=="Gaussian")filterType=UtilsOpenCV.Filter.Gauss;
			}
		}
	}
	private void openImage()
	{
		try
		{
			dlg=new JFileChooser();dlg.setMultiSelectionEnabled(false);
			FileFilter filter=new FileNameExtensionFilter("Image (jpeg,png))","jpg","png","jpeg","PNG");
			dlg.setFileFilter(filter);
			
			int res=dlg.showOpenDialog(mainFrame);
			if(res==JFileChooser.APPROVE_OPTION)
			{
				File f=dlg.getSelectedFile();
				image=ImageIO.read(f);
				resetImage=ImageIO.read(f);
				displayLabel.setIcon(new ImageIcon(image));
				displayLabel.setVerticalAlignment(SwingConstants.TOP);
			}
		}
		catch(Exception e)
		{
			System.err.println("Erreur lors de l'ouverture d'une image");
		}
	}
	private void saveImage()
	{
		try
		{
			dlg=new JFileChooser();
			dlg.setCurrentDirectory(new File("C:\\Users\\valimo\\Desktop\\"));
			int res=dlg.showSaveDialog(mainFrame);
			if(res==JFileChooser.APPROVE_OPTION)
			{
				File f=dlg.getSelectedFile();
				String path=f.getAbsolutePath();
				ImageIcon ic=(ImageIcon)displayLabel.getIcon();
				if(ic!=null)
				{
					BufferedImage image=(BufferedImage)ic.getImage();
					UtilsOpenCV.saveImage(image, path);
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Erreur lors de l'enregistrement d'une image");
		}
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(secondParamActivate)
		{
			if(image==null)
			{
				System.out.println("image null");
				return;
			}
			int posX=arg0.getX();
			int posY=arg0.getY();
			if(posX<image.getWidth() && posY<image.getHeight())
			{
				displayLabel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				if(mousePressed)
				{
					fillTargetPixels(arg0);
				}
			}
			else
			{
				displayLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub		
		if(secondParamActivate)
		{
			if(image==null)return;
			int posX=arg0.getX();
			int posY=arg0.getY();
			if(posX<image.getWidth() && posY<image.getHeight())
			{
				displayLabel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
			else
			{
				displayLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	private void fillTargetPixels(MouseEvent e)
	{
		targetPixels.add(new Point(e.getY(), e.getX()));
	}
	@Override
	public void ImageLoaded(ImageLoadedEvent evt) {
		// TODO Auto-generated method stub
		System.out.println("image loader");
		image=evt.getImage();
		resetImage=evt.getImage();
		displayLabel.setIcon(new ImageIcon(image));
	}
	private void UpdateImageReference()
	{
		image=(BufferedImage)((ImageIcon)displayLabel.getIcon()).getImage();
		resetImage=(BufferedImage)((ImageIcon)displayLabel.getIcon()).getImage();
	}
}
