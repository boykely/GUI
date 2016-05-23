import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Mat;

public class GUIHandler implements MouseListener,ChangeListener,ActionListener
{
	public boolean Clicked=false;
	public BufferedImage image;
	private JFileChooser dlg;
	private JFrame mainFrame;
	private JLabel displayLabel;
	
	public GUIHandler(JFrame frame)
	{
		mainFrame=frame;
	}
	public void setDisplayLabel(JLabel lbl)
	{
		displayLabel=lbl;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		Clicked=!Clicked;
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
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		JCheckBox chk=(JCheckBox)e.getSource();
		if(chk.getName()=="mirrorCheck")
		{
			
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton btn=(JButton)e.getSource();
		if(btn.getName()=="Ouvrir")
		{
			openImage();
		}
		if(btn.getName()=="Camera")
		{
			try
			{
				CamDialog cam=new CamDialog(mainFrame,displayLabel);
				Thread th=new Thread(cam);
				th.start();
			}
			catch(Exception ex)
			{
				System.err.println("Erreur lors de la cam");
			}
		}
		if(btn.getName()=="Enregistrer")
		{
			saveImage();
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
				displayLabel.setIcon(new ImageIcon(image));
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
}
